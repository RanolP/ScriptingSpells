package me.ranol.scriptingspells;

import java.util.HashMap;

import me.ranol.scriptingspells.api.IParser;

public enum ParserManagement {
	INSTANCE;
	HashMap<Class<? extends IParser>, IParser> cache = new HashMap<>();

	public static <T extends IParser<?>> T restore(Class<T> parserClass) {
		if (!INSTANCE.cache.containsKey(parserClass)) {
			try {
				INSTANCE.cache.put(parserClass, parserClass.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return (T) INSTANCE.cache.get(parserClass);
	}

	public static IParser[] restore(Class<? extends IParser<?>>[] parserClasses) {
		IParser[] result = new IParser[parserClasses.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = restore(parserClasses[i]);
		}
		return result;
	}
}
