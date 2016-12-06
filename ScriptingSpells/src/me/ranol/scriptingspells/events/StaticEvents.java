package me.ranol.scriptingspells.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import me.ranol.scriptingspells.ScriptingSpells;

public enum StaticEvents implements Listener {
	INSTANCE;
	private HashMap<Class<? extends Event>, List<EventRunnable<Event>>> executors = new HashMap<>();

	public static <T extends Event> void register(Class<T> clazz, EventRunnable<T> executor) {
		if (!INSTANCE.executors.containsKey(clazz)) {
			EventExecutor exec = (l, e) -> {
				for (EventRunnable<Event> r : INSTANCE.executors.get(clazz))
					r.run(e);
			};
			Bukkit.getPluginManager()
				.registerEvent(clazz, INSTANCE, EventPriority.NORMAL, exec, ScriptingSpells.getInstance());
			INSTANCE.executors.put(clazz, new ArrayList<>());
		}
		INSTANCE.executors.get(clazz)
			.add((EventRunnable<Event>) executor);
	}
}
