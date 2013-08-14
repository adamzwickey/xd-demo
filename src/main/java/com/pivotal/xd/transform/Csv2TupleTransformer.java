package com.pivotal.xd.transform;

import java.util.StringTokenizer;

import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

public class Csv2TupleTransformer {

	public Tuple transform(String payload) {
		StringTokenizer st = new StringTokenizer(payload, ",");
		TupleBuilder tb = new TupleBuilder();
		int i = 0;
		while (st.hasMoreTokens()) {
			tb.put(String.valueOf(i), st.nextElement());
			i++;
		}
		return tb.build();
		//This isn't working now... for some reason the TAP cnverts this to a plain string
		//literally:  14:04:51,716  WARN New I/O worker #2 logger.demoTap:141 - DefaultTuple [names=[1, 2], values=[adam, test], id=42c37875-b6d3-4207-a3a9-403acfc58048, timestamp=1373479491715]
	}
}
