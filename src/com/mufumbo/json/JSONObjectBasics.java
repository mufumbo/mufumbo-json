package com.mufumbo.json;

/**
 * Useful for test cases.
 */
public interface JSONObjectBasics {
	public JSONArrayBasics optJSONArray(String key);
	public JSONObjectBasics optJSONObject(String key);
	public String optString(String key);
	public String optString(String key, String def);
	public long optLong(String key);
	public int optInt(String key);
	public boolean optBoolean(String key);
	public double optDouble(String key);
}
