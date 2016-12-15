package me.ranol.scriptingspells.api.defaultparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import me.ranol.scriptingspells.ParserManagement;
import me.ranol.scriptingspells.api.IParser;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.api.effects.SpellEffect;

public class EffectsParser implements IParser<HashMap<EffectPosition, List<SpellEffect>>> {
	@Override
	public HashMap<EffectPosition, List<SpellEffect>> parse(ConfigurationSection section, String key) {
		EffectParser parser = ParserManagement.restore(EffectParser.class);
		HashMap<EffectPosition, List<SpellEffect>> result = new HashMap<>();
		ConfigurationSection cur = section.getConfigurationSection(key);
		for (String k : cur.getKeys(false)) {
			Entry<EffectPosition, SpellEffect> entry = parser.parse(cur, k);
			if (!result.containsKey(entry.getKey())) {
				result.put(entry.getKey(), new ArrayList<>());
			}
			result.get(entry.getKey())
				.add(entry.getValue());
		}
		return result;
	}

	@Override
	public String options() {
		return "effects 내부에는 이름이 지정된 효과 객체";
	}

}
