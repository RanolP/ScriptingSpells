package me.ranol.scriptingspells.utils;

public class Wrap<T> {
	T real;

	public Wrap(T start) {
		real = start;
	}

	public Wrap() {
		this(null);
	}

	public T get() {
		return real;
	}

	public Wrap set(T obj) {
		real = obj;
		return this;
	}

	public static <T> Wrap<T> of(T object) {
		return new Wrap<T>(object);
	}

	public static <T> Wrap<T> empty() {
		return new Wrap<T>();
	}
}
