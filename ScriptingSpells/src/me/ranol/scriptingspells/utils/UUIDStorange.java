package me.ranol.scriptingspells.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;

public class UUIDStorange<T> {
	HashMap<UUID, T> map = new HashMap<>();

	public void set(UUID uid, T obj) {
		map.put(uid, obj);
	}

	public void set(LivingEntity e, T obj) {
		set(e.getUniqueId(), obj);
	}

	public T get(UUID uid) {
		return map.get(uid);
	}

	public T get(LivingEntity e) {
		return get(e.getUniqueId());
	}

	public boolean containsKey(UUID uid) {
		return map.containsKey(uid);
	}

	public boolean containsKey(LivingEntity e) {
		return containsKey(e.getUniqueId());
	}

	public void remove(UUID uid) {
		map.remove(uid);
	}

	public void remove(LivingEntity e) {
		remove(e.getUniqueId());
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}
}
