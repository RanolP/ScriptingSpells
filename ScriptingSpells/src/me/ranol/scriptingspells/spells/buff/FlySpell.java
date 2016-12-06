package me.ranol.scriptingspells.spells.buff;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.spells.BuffSpell;
import me.ranol.scriptingspells.utils.UUIDStorange;

public class FlySpell extends BuffSpell {

	public FlySpell(String name) {
		super(name);
	}

	UUIDStorange<Boolean> alreadyActivate = new UUIDStorange<>();

	@Override
	public SpellCastState activate(LivingEntity e, float power) {
		if (e instanceof Player) {
			alreadyActivate.set(e, ((Player) e).getAllowFlight());
			((Player) e).setAllowFlight(true);
			return SpellCastState.SUCESS;
		}
		return SpellCastState.CANTCAST;
	}

	@Override
	public void deactivate(LivingEntity e) {
		super.deactivate(e);
		((Player) e).setAllowFlight(alreadyActivate.get(e));
	}

}
