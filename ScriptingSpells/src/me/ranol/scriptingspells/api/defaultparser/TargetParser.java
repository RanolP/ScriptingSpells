package me.ranol.scriptingspells.api.defaultparser;

import static me.ranol.scriptingspells.api.TargetFilter.TargetOption.*;

import me.ranol.scriptingspells.api.AbstractParser;
import me.ranol.scriptingspells.api.TargetFilter;

public class TargetParser extends AbstractParser<String, TargetFilter> {

	@Override
	public TargetFilter parse(String obj) {
		String[] data = obj.replace(" ", "")
			.split(",");
		TargetFilter f = new TargetFilter();
		for (String s : data) {
			switch (s.toLowerCase()) {
			case "all":
				f.set(ALL, true);
				break;
			case "!all":
				f.set(ALL, false);
				break;
			case "player":
				f.set(PLAYER, true);
				break;
			case "!player":
				f.set(PLAYER, false);
				break;
			case "self":
				f.set(SELF, true);
				break;
			case "!self":
				f.set(SELF, false);
				break;
			case "monster":
				f.set(MONSTER, true);
				break;
			case "!monster":
				f.set(MONSTER, false);
				break;
			case "animal":
				f.set(ANIMAL, true);
				break;
			case "!animal":
				f.set(ANIMAL, false);
				break;
			default:
				boolean set = !s.startsWith("!");
				String type = s.replace("!", "")
					.toUpperCase();
				f.addType(type, set);
			}
		}
		return f;
	}

	@Override
	public String options() {
		return "player, self, monster, animal, 그 외에 엔티티 타입";
	}

}
