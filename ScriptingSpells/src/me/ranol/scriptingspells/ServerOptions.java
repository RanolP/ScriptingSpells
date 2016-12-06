package me.ranol.scriptingspells;

import java.util.HashMap;

public class ServerOptions {
	static enum Option {
		DEBUG
	}

	static HashMap<Option, Object> map = new HashMap<>();

	public static void set(Option key, Object value) {
		map.put(key, value);
	}

	public static <T> T option(Option key) {
		return (T) map.get(key);
	}

	public static <T> T option(Option key, Class<T> clazz) {
		return (T) map.get(key);
	}
}
