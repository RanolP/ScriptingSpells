package me.ranol.scriptingspells.api.defaultparser;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import me.ranol.scriptingspells.api.IParser;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.api.effects.SpellEffect;
import me.ranol.scriptingspells.exceptions.ParserException;

public class EffectParser implements IParser<Entry<EffectPosition, SpellEffect>> {

	@Override
	public Entry<EffectPosition, SpellEffect> parse(ConfigurationSection section, String key) {
		ConfigurationSection sec = section.getConfigurationSection(key);
		EffectPosition pos = EffectPosition.valueOf(sec.getString("position")
			.toUpperCase());
		SpellEffect result = SpellEffect.newInstance(sec.getString("type"));
		if (pos == null) {
			pos = EffectPosition.CASTER;
		}
		for (String s : sec.getKeys(false)) {
			switch (s) {
			case "type":
			case "position":
				continue;
			}
			try {
				result.setOption(s, sec);
			} catch (ParserException e) {

			}
		}
		return new SimpleEntry(pos, result);
	}

	@Override
	public String options() {
		return "자세한 내용은 위키 참조";
	}

}
