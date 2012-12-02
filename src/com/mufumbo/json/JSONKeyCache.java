package com.mufumbo.json;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONKeyCache {
	/**
	 * A bit faster, but {@link HashMap} uses more memory than an {@link ArrayList}
	 * Total GAIN[594kb] nonoptimized[11] memory is [16][2347kb] and optimized[9] is [24][1753kb]
	 */
	protected HashMap<Object, Integer> keyIndex;

	/**
	 * This one saves more memory, with a bit slower processing.
	 * Total GAIN[597kb] nonoptimized[11] memory is [16][2347kb] and optimized[10] is [24][1750kb]
	 */
	//protected ArrayList<Object> keyIndex;

	public JSONKeyCache() {
		//keyIndex = new ArrayList<Object>();
		keyIndex = new HashMap<Object, Integer>();
	}

	public final int findKeyIndex(final Object key) {
		/*
		 * final int result = keyCache.keyIndex.indexOf(key);
		 * //
		 * if (result < 0) {
		 * keyCache.keyIndex.add(key);
		 * return keyCache.keyIndex.size();
		 * }
		 * return result;
		 */

		final Integer result = keyIndex.get(key);
		if (result == null) {
			final int n = keyIndex.size();
			keyIndex.put(key, n);
			return n;
		}
		return result;
	}
}
