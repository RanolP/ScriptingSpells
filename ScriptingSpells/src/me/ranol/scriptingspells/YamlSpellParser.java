package me.ranol.scriptingspells;

import org.bukkit.configuration.file.YamlConfiguration;

import me.ranol.scriptingspells.api.Spell;
import me.ranol.scriptingspells.api.SpellManager;

public class YamlSpellParser {
	public static void parse(YamlConfiguration cfg) {
		for (String key : cfg.getKeys(false)) {
			if (cfg.contains(key + ".type")) {
				Spell spell = Spell.newInstance(cfg, key, cfg.getString(key + ".type"));
				SpellManager.register(spell);
				ScriptingSpells.debug("스펠 " + spell.getName() + "을 로드했습니다.");
			} else {
				ScriptingSpells.error("스펠 " + key + "에는 type이 존재하지 않습니다.");
			}
		}
	}
}
