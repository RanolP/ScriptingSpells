package me.ranol.scriptingspells.utils;

public class TaskTimer {
	private long start;

	public void start() {
		start = System.currentTimeMillis();
	}

	public long time() {
		return System.currentTimeMillis() - start;
	}

	public double timeAsSecond() {
		return (System.currentTimeMillis() - start) / 1000d;
	}
}
