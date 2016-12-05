package me.ranol.scriptingspells.spells.targeted;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.spells.TargetedLocationSpell;

public class TeleportSpell extends TargetedLocationSpell {

	public TeleportSpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtLocation(LivingEntity caster, Location target, float power) {
		target.setPitch(caster.getLocation().getPitch());
		target.setYaw(caster.getLocation().getYaw());
		caster.teleport(target.add(0, 1, 0));
		return SpellCastState.SUCESS;
	}

}
