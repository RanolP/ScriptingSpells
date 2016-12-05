package me.ranol.scriptingspells;

import static me.ranol.scriptingspells.ServerOptions.option;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.ranol.scriptingspells.ServerOptions.Option;
import me.ranol.scriptingspells.api.SpellManager;
import me.ranol.scriptingspells.commands.CastCommand;
import net.md_5.bungee.api.ChatColor;

public class ScriptingSpells extends JavaPlugin {
	private static final String PREFIX = "&8&l[&aScriptingSpells&8&l]";
	private YamlConfiguration config;
	private static ScriptingSpells instance;

	@Override
	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			saveDefaultConfig();
			saveResource("spells-default.yml", false);
		}
		config = (YamlConfiguration) getConfig();
		loadConfigs();
		loadSpells();
		register(new CastCommand(), "cast");
	}

	void register(TabExecutor exe, String name) {
		PluginCommand cmd = getCommand(name);
		cmd.setExecutor(exe);
		cmd.setTabCompleter(exe);
	}

	public static ScriptingSpells getInstance() {
		return instance;
	}

	public static void loadSpells() {
		debug("스펠 로드를 시작합니다.");
		if (option(Option.THREAD, boolean.class)) {
			new Thread(ScriptingSpells::loadSpellsUnsafe).start();
		} else {
			loadSpellsUnsafe();
		}
	}

	private static void loadSpellsUnsafe() {
		SpellManager.clear();
		File[] spells = instance.getDataFolder().listFiles(f -> f.isFile() && f.getName().startsWith("spell"));
		debug("발견된 스펠 파일 : " + spells.length + "개");
		for (File f : spells) {
			debug("스펠 파일 " + f.getName() + "을 불러옵니다.");
			YamlSpellParser.parse(YamlConfiguration.loadConfiguration(f));
		}
	}

	@Override
	public void onDisable() {
	}

	public static void loadConfigs() {
		ServerOptions.set(Option.DEBUG, instance.config.getBoolean("debug", false));
		ServerOptions.set(Option.THREAD, instance.config.getBoolean("thread-loading", true));
	}

	public static void debug(String message) {
		if (option(Option.DEBUG, boolean.class)) {
			console("&8&l[&aScriptingSpells-&5&lDEBUG&8&l] &a> &b" + message);
		}
	}

	public static void error(String message) {
		console("&8&l[&aScriptingSpells-&c&lERROR&8&l] &a> &b" + message);
	}

	public static void warn(CommandSender s, String message) {
		msg(s, "&e[!] &6" + message);
	}

	public static void rawMessage(CommandSender s, String message) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void line(char... colors) {
		StringBuilder color = new StringBuilder();
		for (char c : colors) {
			color.append(ChatColor.getByChar(c));
		}
		console(color + "===============================");
	}

	public static void console(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void msg(CommandSender s, String message) {
		rawMessage(s, PREFIX + " &a" + message);
	}
}
