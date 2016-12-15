package me.ranol.scriptingspells.api;

import org.bukkit.configuration.ConfigurationSection;

public interface IParser<U> {
	public U parse(ConfigurationSection section, String key);

	public String options();
}
