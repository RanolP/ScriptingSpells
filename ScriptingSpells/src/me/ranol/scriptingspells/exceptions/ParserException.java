package me.ranol.scriptingspells.exceptions;

import me.ranol.scriptingspells.api.IParser;

public class ParserException extends Exception {
	IParser<?> parser;

	public ParserException(IParser<?> parser, Exception e) {
		super(e);
		this.parser = parser;
	}

	public IParser<?> getParser() {
		return parser;
	}
}
