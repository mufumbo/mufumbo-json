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
import com.mufumbo.json.JSONObject;
import com.mufumbo.json.JSONObjectBasics;

public class JSONTest extends TestCase {
	public void testHeavy() {
		try {
			String json = readFully(new InputStreamReader(
					JSONTest.class.getResourceAsStream("chicken-search.json")));
			System.out.println("Total STRING memory is "
					+ MemoryUtil.deepMemoryUsageOf(json, VisibilityFilter.ALL));

			for (int i = 0; i < 1000; i++) {
				long start = System.currentTimeMillis();
				org.json.JSONObject nobj = new org.json.JSONObject(new String(json));
				for (int j = 0; j < 5000; j++) {
					String str = "just testin! this " + System.currentTimeMillis();
					nobj.put("test", str);
					assertTrue(str.equals(nobj.get("test")));
					checkResponse(nobj);
				}
				long ntime = System.currentTimeMillis();

				JSONObject obj = new JSONObject(new String(json));
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
				
				System.out.println("Processing str[" + json.length() + "] " +
						"--- MGAIN[" + (nsize - size) + "kb] " + ((float) (nsize - size) / nsize) + "% --- " +
						"--- PGAIN[" + (nptime - ptime) + "ms] " + ((float) (nptime - ptime) / nptime) + "% --- " +
						"\n>>> nonoptimized[" + (ntime - start) + "] memory is [" + MemoryUtil.memoryUsageOf(nobj) + "][" + nsize + "kb] [" + nobj.toString().length()+ "] <<<" +
						" and " +
						">>> optimized[" + (time - ntime) + "] is [" + MemoryUtil.memoryUsageOf(obj) + "][" + size + "kb] out[" + obj.toString().length() + "] <<<\n\n");
				
				assertEquals(nobj.toString().length(), obj.toString().length());
				assertSame(nobj.optString("NON VALID KEY"), obj.optString("NON VALID KEY"));
				
				// System.out.print("Total memory is " +
				// MyAgent.getObjectSize(obj));
			}
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
