package me.ranol.scriptingspells;

import org.bukkit.Bukkit;

import me.ranol.scriptingspells.utils.Wrap;

public enum StaticScheduler {
	INSTANCE;

	public static void delayedTask(Runnable r) {
		Bukkit.getScheduler()
			.scheduleSyncDelayedTask(ScriptingSpells.getInstance(), r);
	}

	public static void delayedTask(Runnable r, long delay) {
		Bukkit.getScheduler()
			.scheduleSyncDelayedTask(ScriptingSpells.getInstance(), r, delay);
	}

	public static int repeatTask(Runnable r, long period) {
		return Bukkit.getScheduler()
			.scheduleSyncRepeatingTask(ScriptingSpells.getInstance(), r, 0, period);
	}

	public static void cancelTask(Wrap<Integer> wrap) {
		cancelTask(wrap.get());
	}

	public static void cancelTask(int i) {
		Bukkit.getScheduler()
			.cancelTask(i);
	}
}
