package me.ranol.scriptingspells.api.defaultparser;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.ranol.scriptingspells.api.parser.StringParser;

public class DamageCauseParser extends StringParser<DamageCause> {

	@Override
	public DamageCause parse(String object) {
		return DamageCause.valueOf(object.toUpperCase());
	}

	@Override
	public String options() {
		StringBuilder b = new StringBuilder();
		for (DamageCause c : DamageCause.values()) {
			b.append(", " + c.name());
		}
		return b.toString()
			.replaceFirst(", ", "");
	}

}
