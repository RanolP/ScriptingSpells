package me.ranol.scriptingspells.api;

public interface IParser<T, U> {
	public U parse(T object);

	public String options();
}
