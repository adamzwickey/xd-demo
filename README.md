xd-demo
=======

Some Spring-XD demo stuff

In order to get this running with Pivotal HD
1) Start Pivotal HD instance.

2) Update your spring-xd hadoop config (conf/hadoop.properties) to reflect webhdfs:
	mbp:spring-xd azwickey$ cat hadoop.properties 
	fs.default.name=webhdfs://192.168.72.172:50070

3) Start Spring-XD

4) Create some streams.  Depending on the version of spring xd you will have to use a http internface such as curl (milestone1) or the spring-xd shell interface (current build snapshot).  Sample commands for creating stream using spring xd shell:
	- Create stream to write to hdfs: deploy stream --definition "http --port=8000 | hdfs --rollover=10000000" --name demo
	- Create stream to simply write to a log file: deploy stream --definition "http --port=8000 | log" --name demo
	- Create a tap on stream that will count into redis: deplay stream --definition "tap @ demo | counter

5) For sample data contact adam zwickey: azwickey@gopivotal.com

6) After starting an http stream, execute XDDemo program.  Sample arguments: /Users/azwickey/development/demo/data/cms/cms.csv http://localhost:8000 -1


In order to test this with Esper and SQLFire or Gemfire
1) After building the project in maven you'll have a number of jars in your local repository that need to be copied to the /lib directory of XD.  For convienience I've placed this in a lib dir in the project this project:
	antlr-2.7.7.jar
	antlr-runtime-3.1.1.jar
	esper-3.5.0.jar
	esper-si-support-2.1
	esper-template-2.1.jar

	** also copy xd-demo.jar in the root of the project to the xd lib dir

2) Copy cep.xml files from src/main/resources to $XD_HOME/modules/processor/

3) Copy the following files from /src/main/resources to $XD_HOME/modules/process/scripts/
	cep2json.groovy
	empty.epl
	esper.groovy
	txt2order.groovy

4)  ** follow the steps to prepare the "retail demo" of spring XD.  For info, azwickey@gopivotal.com

5) Start Spring XD and create streams:
	a) this combination will simply log the data to a file and CEP events to the XD log.  The CEP engine will compute the average purchase amount over a 1 minute window and emit the [store,avgAmount] every 5 seconds
	 - stream create --definition "http --port=8000 | filter --script=order-filter.groovy | transform --script=order-transformer.groovy | file" --name order_stream
	 - stream create --definition "tap @ order_stream | filter --script=order-filter2.groovy | transform --script=txt2order.groovy | cep --statement='select storeId,avg(amt) as avgAmount from com.pivotal.example.xd.RetailOrder.win:time(1 minute) group by storeId output every 5 seconds' --fields='storeId,avgAmount' | log" --name order_tap

	b)This combination builds on previous example but stores the store average in SQLFire, an in-memory retaional datastore.  Prior to executing this example you'll need SQLFire setup and a sample table created.  See sqlfire_setup.sql in the resources folder for schema.  This schema assumes you have 10 storeIds
	 - stream create --definition "http --port=8000 | filter --script=order-filter.groovy | transform --script=order-transformer.groovy | file" --name order_stream
	 - stream create --definition "tap @ order_stream | filter --script=order-filter2.groovy | transform --script=txt2order.groovy | cep --statement='select storeId,avg(amt) as avgAmount from com.pivotal.example.xd.RetailOrder.win:time(1 minute) group by storeId output every 5 seconds' --fields='storeId,avgAmount' | sqlf --url=sqlf --insert='update stores set amount=:payload[1] where id=:payload[0]'" --name order_tap
	 
	c)This combination builds on previous example but stores a representation of the store as a JSON doncument in GemFire, an in-memory data grid. Version 7.0.1 is required.  Prior to running this example you should have a running gemfire cluster with a "Stores" region defined.  You can create the region in realtime using gfsh with this command:'create region --name=Stores --type=PARTITION'.  Also see cache.xml in resources for a config that statically creates a region.  In the SpringXD modules/sink folder rename (or copy) gemfire-json-server.xml to gemfireJsonServer.xml
	 - stream create --definition "http --port=8000 | filter --script=order-filter.groovy | transform --script=order-transformer.groovy | file" --name order_stream
	 - stream create --definition "tap @ order_stream | filter --script=order-filter2.groovy | transform --script=txt2order.groovy | cep --statement='select storeId,avg(amt) as avgAmount from com.pivotal.example.xd.RetailOrder.win:time(1 minute) group by storeId output every 5 seconds' --fields='storeId,avgAmount' | transform --script=cep2json.groovy | gemfireJsonServer --gemfireHost=192.168.72.167 --regionName=Stores --keyExpression=payload.getField('storeId')" --name order_tap

6) Execute the python retail data generator.  You may find it helpful to limit the number of storeIds that will be generated to 10 or 100 at most.