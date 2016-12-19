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
import me.ranol.scriptingspells.nms.VolatileCode;
import me.ranol.scriptingspells.nms.VolatileCodeDisabled;
import me.ranol.scriptingspells.nms.VolatileCode_ProtocolLib;
import me.ranol.scriptingspells.nms.VolatileCode_v1_10_R1;
import net.md_5.bungee.api.ChatColor;

public class ScriptingSpells extends JavaPlugin {
	private static final String PREFIX = "&8&l[&aScriptingSpells&8&l]";
	private YamlConfiguration config;
	private static ScriptingSpells instance;
	private VolatileCode volatileCode;

	@Override
	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			saveDefaultConfig();
			saveResource("spells-default.yml", false);
			saveResource("general.yml", false);
		}
		config = (YamlConfiguration) getConfig();
		grabVolatile();
		loadConfigs();
		loadSpells();
		register(new CastCommand(), "cast");
	}

	public static VolatileCode getVolatile() {
		return getInstance().volatileCode;
	}

	private void grabVolatile() {
		String version = Bukkit.getServer()
			.getClass()
			.getPackage()
			.getName()
			.replace('.', ',')
			.split(",")[3];
		switch (version) {
		case "v1_10_R1":
			volatileCode = new VolatileCode_v1_10_R1();
			break;
		default:
			if (Bukkit.getPluginManager()
				.getPlugin("ProtocolLib") != null) {
				console("&aProtocolLib을 찾았습니다.");
				volatileCode = new VolatileCode_ProtocolLib();
				break;
			}
			volatileCode = new VolatileCodeDisabled();
			break;
		}
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
		SpellManager.clear();
		File[] spells = instance.getDataFolder()
			.listFiles(f -> f.isFile() && f.getName()
				.startsWith("spell"));
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
		Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void msg(CommandSender s, String message) {
		rawMessage(s, PREFIX + " &a" + message);
	}

	public static void fancyException(Throwable t) {
		line('6', 'l');
		StringBuilder msg = new StringBuilder();
		msg.append("&a" + t.getClass()
			.getName() + ": " + t.getMessage() + "\n");
		for (StackTraceElement s : t.getStackTrace()) {
			msg.append(
					"\t&e" + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":"
							+ s.getLineNumber() + ")\n");
		}
		console(msg.toString());
		line('6', 'l');
	}
}
