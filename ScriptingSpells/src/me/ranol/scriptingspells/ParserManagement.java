package me.ranol.scriptingspells;

import java.util.HashMap;

import me.ranol.scriptingspells.api.IParser;

public enum ParserManagement {
	INSTANCE;
	HashMap<Class<? extends IParser>, IParser> cache = new HashMap<>();

	public static IParser restore(Class<? extends IParser> parserClass) {
		if (!INSTANCE.cache.containsKey(parserClass)) {
			try {
				INSTANCE.cache.put(parserClass, parserClass.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return INSTANCE.cache.get(parserClass);
	}
}
