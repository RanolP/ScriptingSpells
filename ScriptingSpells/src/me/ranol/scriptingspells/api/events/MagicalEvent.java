package me.ranol.scriptingspells.api.events;

import org.bukkit.event.Event;

public interface MagicalEvent {
	public static <T extends Event> boolean isMagicalEvent(T event) {
		return event instanceof MagicalEvent;
	}
}
