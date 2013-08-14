CREATE TABLE STORES
   (
      ID INT NOT NULL,
      AMOUNT NUMERIC(10,2),
      PRIMARY KEY (ID)
   ) REPLICATE;

insert into stores(id) values(1);
insert into stores(id) values(2);
insert into stores(id) values(3);
insert into stores(id) values(4);
insert into stores(id) values(5);
insert into stores(id) values(6);
insert into stores(id) values(7);
insert into stores(id) values(8);
insert into stores(id) values(9);
insert into stores(id) values(10);


CREATE TABLE REALTIME_ORDERS
   (
      CUSTOMER_ID INT NOT NULL,
      ORDER_ID INT NOT NULL,
      ORDER_AMOUNT NUMERIC(10,2),
      STORE_ID INT
   ) REPLICATE;
