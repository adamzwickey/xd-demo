package com.pivotal.xd.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Csv2MapTransformer {

	public Map transform(String payload) {
		StringTokenizer st = new StringTokenizer(payload, ",");
		Map<String, String> m = new HashMap<String, String>();
		int i = 0;
		while (st.hasMoreTokens()) {
			m.put(String.valueOf(i), (String) st.nextElement());
			i++;
		}
		return m;
		//This isn't working now... for some reason the TAP cnverts this to a plain string
		//literally:  14:04:51,716  WARN New I/O worker #2 logger.demoTap:141 - DefaultTuple [names=[1, 2], values=[adam, test], id=42c37875-b6d3-4207-a3a9-403acfc58048, timestamp=1373479491715]
	}
}
