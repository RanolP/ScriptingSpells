package me.ranol.scriptingspells.api;

import static me.ranol.scriptingspells.api.TargetFilter.TargetOption.*;

import java.util.HashMap;

import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class TargetFilter {
	public static enum TargetOption {
		SELF, PLAYER, ANIMAL, MONSTER, ALL
	}

	HashMap<TargetOption, Boolean> option = new HashMap<>();
	HashMap<String, Boolean> mobTypes = new HashMap<>();

	public TargetFilter() {
		option.put(PLAYER, true);
		option.put(MONSTER, true);
		option.put(ANIMAL, true);
		option.put(ALL, false);
		option.put(SELF, false);
	}

	public void set(TargetOption o, boolean val) {
		option.put(o, val);
	}

	public boolean canTarget(LivingEntity caster, LivingEntity target) {
		boolean result = false;
		if (target instanceof Player) {
			switch (((Player) target).getGameMode()) {
			case CREATIVE:
			case SPECTATOR:
				return false;
			default:
				break;
			}
		}
		if (caster.equals(target))
			return option.get(SELF);
		if (option.get(ALL))
			return true;
		if (target instanceof Player)
			result |= option.get(PLAYER);
		if (target instanceof Monster)
			result |= option.get(MONSTER);
		if (target instanceof Animals)
			result |= option.get(ANIMAL);
		if (mobTypes.containsKey(target.getType().name())) {
			result &= mobTypes.get(target.getType().name());
		}
		return result;
	}

	public void addType(String type, boolean set) {
	}
}
