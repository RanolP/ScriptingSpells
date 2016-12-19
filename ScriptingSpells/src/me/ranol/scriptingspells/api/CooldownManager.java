package me.ranol.scriptingspells.api;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.utils.UUIDStorage;

public class CooldownManager {
	private static UUIDStorage<HashMap<String, Long>> apply = new UUIDStorage<>();

	public static void apply(LivingEntity e, String name, float cooldown) {
		if (!apply.containsKey(e)) {
			apply.set(e, new HashMap<>());
		}
		apply.get(e)
			.put(name, System.currentTimeMillis() + (long) (cooldown * 1000));
	}

	public static float get(LivingEntity e, String name) {
		if (!apply.containsKey(e)) {
			apply.set(e, new HashMap<>());
		}
		if (apply.get(e)
			.containsKey(name)) {
			float cur = (apply.get(e)
				.get(name) - System.currentTimeMillis()) / 1000f;
			if (cur <= 0) apply.get(e)
				.remove(name);
			return cur;
		}
		return 0f;
	}

	public static void reset(LivingEntity e) {
		apply.set(e, new HashMap<>());
	}
}
