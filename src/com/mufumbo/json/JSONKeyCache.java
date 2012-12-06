package com.mufumbo.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class JSONKeyCache implements Serializable {
	private static final long serialVersionUID = 4399741430699209789L;

	/**
	 * A bit faster, but {@link HashMap} uses more memory than an {@link ArrayList} Total GAIN[594kb] nonoptimized[11]
	 * memory is [16][2347kb] and optimized[9] is [24][1753kb]
	 */
	protected LinkedHashMap<Object, Integer> keyIndex;

	protected transient Object objectCache = null;
	//int cacheIndex;

	public JSONKeyCache() {
		keyIndex = new LinkedHashMap<Object, Integer>();
	}

	protected int keyCount;

	public final int findKeyIndex(final Object key) {
		final Integer result = keyIndex.get(key);
		if (result == null) {
			keyCount = keyIndex.size();
			keyIndex.put(key, keyCount);
			return keyCount;
		}
		return result;
	}

	protected Set<Object> keySet() {
		return keyIndex.keySet();
	}

	public Object getFromObjectCache(final Object obj) {
		if (!String.class.isInstance(obj))
			return obj;

		final String intern = ((String) obj).intern();
		if (intern == objectCache)
			return objectCache;

		objectCache = intern;
		return objectCache;
	}

	/*
	 * public Object getFromObjectCache(Object obj) {
	 * if (objectCache == null)
	 * return obj;
	 * if (!String.class.isInstance(obj))
	 * return obj;
	 * final int len = objectCache.length;
	 * if (len == 0)
	 * return obj;
	 * String intern = ((String) obj).intern();
	 * for (int i = 0; i < len; i++) {
	 * Object o = objectCache[i];
	 * if (o != null) {
	 * if (intern == o)
	 * return o;
	 * }
	 * else {
	 * break;
	 * }
	 * }
	 * objectCache[cacheIndex++] = intern;
	 * if (cacheIndex >= len) {
	 * cacheIndex = 0;
	 * }
	 * return intern;
	 * }
	 */
}
