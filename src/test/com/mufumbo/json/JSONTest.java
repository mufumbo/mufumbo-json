package test.com.mufumbo.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;
import com.mufumbo.json.JSONArray;
import com.mufumbo.json.JSONArrayBasics;
import com.mufumbo.json.JSONException;
import com.mufumbo.json.JSONKeyCache;
import com.mufumbo.json.JSONObject;
import com.mufumbo.json.JSONObjectBasics;

public class JSONTest extends TestCase {
	public void testAndroidError() throws JSONException {
		JSONObject firstTest = new JSONObject();
		firstTest.put("test", "value");
		assertSame("value", firstTest.optString("test"));
		String str = firstTest.toString();
		assertTrue("{\"test\":\"value\"}".equals(str));
		
		JSONKeyCache jkc = new JSONKeyCache();
		final JSONObject json = new JSONObject(jkc);
		final JSONObject failure = new JSONObject(jkc);
		json.accumulate("result", null);
		json.accumulate("success", false);
		failure.accumulate("message", "my message");
		json.accumulate("failure", failure);
		
		JSONKeyCache jkc2 = new JSONKeyCache();
		final JSONObject json2 = new JSONObject(jkc2);
		final JSONObject failure2 = new JSONObject(jkc2);
		//json2.put("result", null);
		json2.put("success", false);
		failure2.put("message", "my message");
		json2.put("failure", failure);
		
		String test = json.toString(0);
		String test2 = json2.toString(0);
		
		assertEquals(test, test2);
	}
	
	public void testHeavy() {
		try {
			String json = readFully(new InputStreamReader(
					JSONTest.class.getResourceAsStream("chicken-search.json")));
			System.out.println("Total STRING memory is "
					+ MemoryUtil.deepMemoryUsageOf(json, VisibilityFilter.ALL));

			float procAccumulated = 0;
			float memAccumulated = 0;
			int RUNS = 50;
			for (int i = 0; i < RUNS; i++) {
				long start = System.currentTimeMillis();
				org.json.JSONObject nobj = new org.json.JSONObject(new String(json));
				for (int j = 0; j < 5000; j++) {
					String str = "just testin! this " + System.currentTimeMillis();
					nobj.put("test", str);
					assertTrue(str.equals(nobj.get("test")));
					checkResponse(nobj);
				}
				long ntime = System.currentTimeMillis();

				JSONKeyCache cache = new JSONKeyCache();
				JSONObject obj = new JSONObject(new String(json), cache);
				for (int j = 0; j < 5000; j++) {
					String str = "just testin! this " + System.currentTimeMillis();
					obj.put("test", str);
					assertTrue(str.equals(obj.get("test")));
					checkResponse(obj);
				}
				long time = System.currentTimeMillis();

				long nsize = Math.round(MemoryUtil.deepMemoryUsageOf(nobj, VisibilityFilter.ALL) * 0.000976562);
				long size = Math.round(MemoryUtil.deepMemoryUsageOf(obj, VisibilityFilter.ALL) * 0.000976562);

				long nptime = (ntime - start);
				long ptime = (time - ntime);

				float pgain = ((float) (nptime - ptime) / nptime);
				float mgain = ((float) (nsize - size) / nsize);

				memAccumulated += mgain;
				procAccumulated += pgain;

				System.out.println("Processing str[" + json.length() + "] " +
						"--- MGAIN[" + (nsize - size) + "kb] " + mgain + "% --- " +
						"--- PGAIN[" + (nptime - ptime) + "ms] " + pgain + "% --- " +
						"\n>>> nonoptimized[" + (ntime - start) + "] memory is [" + MemoryUtil.memoryUsageOf(nobj) + "][" + nsize + "kb] [" + nobj.toString().length() + "] <<<" +
						" and " +
						">>> optimized[" + (time - ntime) + "] is [" + MemoryUtil.memoryUsageOf(obj) + "][" + size + "kb] out[" + obj.toString().length() + "] <<<");
				cache.flushStats();
				System.out.println("\n\n");
				assertEquals(nobj.toString().length(), obj.toString().length());
				assertSame(nobj.optString("NON VALID KEY"), obj.optString("NON VALID KEY"));

				for (int k = 0; k < 10; k++) {
					//	System.gc();
				}
				// System.out.print("Total memory is " +
				// MyAgent.getObjectSize(obj));
			}

			System.out.println("Proc[" + ((float) (procAccumulated / RUNS)) + "] MEM[" + ((float) (memAccumulated / RUNS)) + "]");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void checkResponse(JSONObjectBasics o) {
		Object v = o.optString("NON VALID KEY", null);
		assertNull(v);

		JSONArrayBasics result = o.optJSONArray("result");
		for (int i = 0; i < result.length(); i++) {
			JSONObjectBasics r = result.optJSONObject(i);
			checkRecipe(r);
		}
	}

	public void checkRecipe(JSONObjectBasics r) {
		assertNotNull(r.optString("title"));
		assertNotNull(r.optJSONArray("ingredients"));
	}

	static String readFully(final Reader reader) throws IOException {
		try {
			final StringWriter writer = new StringWriter();
			final char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		}
		finally {
			reader.close();
		}
	}
}
