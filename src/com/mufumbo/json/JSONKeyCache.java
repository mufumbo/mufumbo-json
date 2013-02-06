package com.mufumbo.json;

import java.io.Serializable;
import java.lang.ref.SoftReference;
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

	//protected transient Object objectCache = null;
	//protected transient Object[] objectCache = new Object[10];
	//Map<Object, Object> objectCache = new WeakHashMap<>(50);
	protected transient SoftReference[] objectCache = new SoftReference[50];
	protected int cacheIndex;

	int objCalls;
	int objTypeCalls;
	int objHit;
	int weakMiss;

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

	/*
	 * public Object getFromObjectCache(final Object obj) {
	 * if (!String.class.isInstance(obj))
	 * return obj;
	 * final String intern = ((String) obj).intern();
	 * if (intern == objectCache)
	 * return objectCache;
	 * objectCache = intern;
	 * return objectCache;
	 * }
	 */

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

	/*
	 * public Object getFromObjectCache(Object obj) {
	 * objCalls++;
	 * if (objectCache == null)
	 * return obj;
	 * if (!String.class.isInstance(obj))
	 * return obj;
	 * Object res = objectCache.get(obj);
	 * if (res == null) {
	 * objectCache.put(obj, obj);
	 * return obj;
	 * }
	 * objHit++;
	 * return res;
	 * }
	 */

	/*
	 * public Object getFromObjectCache(Object obj) {
	 * objCalls++;
	 * if (objectCache == null)
	 * return obj;
	 * if (!String.class.isInstance(obj))
	 * return obj;
	 * final int len = objectCache.length;
	 * if (len == 0)
	 * return obj;
	 * String intern = (String) obj;
	 * for (int i = 0; i < len; i++) {
	 * Object o = objectCache[i];
	 * if (o != null) {
	 * if (intern.equals(o)) {
	 * objHit++;
	 * return o;
	 * }
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

	public Object getFromObjectCache(final Object obj) {
		objCalls++;

		if (objectCache == null)
			return obj;

		if (!String.class.isInstance(obj))
			return obj;

		objTypeCalls++;

		final int len = objectCache.length;
		if (len == 0)
			return obj;

		for (int i = 0; i < len; i++) {
			final SoftReference o = objectCache[i];
			if (o != null) {
				final Object c = o.get();
				if (c != null) {
					if (obj.equals(c)) {
						objHit++;
						return c;
					}
				}
				else {
					weakMiss++;
				}
			}
			else {
				break;
			}
		}

		if (objectCache[cacheIndex] != null) {
			objectCache[cacheIndex].clear();
		}

		objectCache[cacheIndex] = new SoftReference(obj);

		if (++cacheIndex >= len) {
			cacheIndex = 0;
		}

		return obj;
	}

	public void flushStats() {
		System.out.println("Cache stats calls[" + objCalls + "] typeCalls[" + objTypeCalls + "] hits[" + objHit + "] weakMiss[" + weakMiss + "]");
		objCalls = 0;
		objHit = 0;
		objTypeCalls = 0;
		weakMiss = 0;
	}
}
