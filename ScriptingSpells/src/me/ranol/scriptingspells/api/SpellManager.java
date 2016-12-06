package me.ranol.scriptingspells.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.utils.UUIDStorange;

public enum SpellManager {
	INSTANCE;
	List<Spell> registeredSpells = new ArrayList<>();
	UUIDStorange<List<Spell>> hasMap = new UUIDStorange<>();

	public static void register(Spell spell) {
		if (spell == null) return;
		INSTANCE.registeredSpells.add(spell);
	}

	public static void clear() {
		INSTANCE.registeredSpells.clear();
	}

	public static Spell getByName(String name) {
		Spell spell = Spell.NONE;
		for (Spell s : INSTANCE.registeredSpells) {
			if (s.getName()
				.equals(name)) spell = s;
		}
		return spell;
	}

	public static List<Spell> hasSpells(CommandSender s) {
		if (s instanceof ConsoleCommandSender) {
			return INSTANCE.registeredSpells;
		}
		if (s instanceof LivingEntity && INSTANCE.hasMap.containsKey(((LivingEntity) s).getUniqueId())) {
			return INSTANCE.hasMap.get(((LivingEntity) s).getUniqueId());
		}
		return Collections.emptyList();
	}
}
