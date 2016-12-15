package me.ranol.scriptingspells.api;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.utils.UUIDStorage;

public enum SpellManager {
	INSTANCE;
	List<Spell> registeredSpells = new ArrayList<>();
	final File folder = new File(ScriptingSpells.getInstance()
		.getDataFolder(), "spellData");
	private List<Spell> allHaves = new ArrayList<>();
	UUIDStorage<List<Spell>> hasMap = new UUIDStorage<>();

	public static void register(Spell spell) {
		if (spell == null) return;
		INSTANCE.registeredSpells.add(spell);
		if (spell.hasAllUsers()) INSTANCE.allHaves.add(spell);
	}

	public static void clear() {
		INSTANCE.registeredSpells.clear();
	}

	public static void save(UUID uid) {
		if (!INSTANCE.folder.exists()) INSTANCE.folder.mkdirs();
		File f = new File(INSTANCE.folder, uid.toString());
		try {
			Files.write(f.toPath(), INSTANCE.hasMap.get(uid)
				.stream()
				.map(Spell::getName)
				.collect(Collectors.joining("\n"))
				.getBytes());
		} catch (Exception e) {
			ScriptingSpells.fancyException(e);
		}
	}

	public static void save() {
		for (UUID uid : INSTANCE.hasMap.uuidSet()) {
			if (Bukkit.getPlayer(uid) != null) save(uid);
		}
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
			List<Spell> temp = INSTANCE.hasMap.get(((LivingEntity) s).getUniqueId());
			temp.addAll(INSTANCE.allHaves);
			return temp;
		}
		return INSTANCE.allHaves;
	}
}
