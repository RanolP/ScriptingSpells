package me.ranol.scriptingspells.spells;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;

public abstract class TargetedLocationSpell extends InstantSpell {
	public TargetedLocationSpell(String name) {
		super(name);
	}

	@SpellOption("range")
	@OptionDocs("스펠이 맞을 수 있는 사거리입니다.")
	protected double range = 10;

	public abstract SpellCastState castAtLocation(LivingEntity caster, Location target, float power);

	@Override
	public SpellCastState castReal(LivingEntity entity, float power) {
		Location loc = getTarget(entity, range * power);
		if (loc == null)
			return SpellCastState.NOTARGET;
		SpellCastState state = castAtLocation(entity, loc, power);
		return state;
	}

	private Location getTarget(LivingEntity caster, double range) {
		BlockIterator it = new BlockIterator(caster, (int) range);
		while (it.hasNext()) {
			Block b = it.next();
			if (!(b.isLiquid() || b.isEmpty())) {
				return b.getLocation();
			}
		}
		return null;
	}

}
