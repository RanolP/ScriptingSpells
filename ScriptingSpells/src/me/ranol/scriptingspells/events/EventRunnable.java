package me.ranol.scriptingspells.events;

import org.bukkit.event.Event;

@FunctionalInterface
public interface EventRunnable<T extends Event> {
	public void run(T e);
}
