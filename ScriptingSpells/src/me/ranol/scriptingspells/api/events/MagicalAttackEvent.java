package me.ranol.scriptingspells.api.events;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.google.common.base.Function;

public class MagicalAttackEvent extends EntityDamageByEntityEvent implements MagicalEvent {
	@Deprecated
	public MagicalAttackEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
		super(damager, damagee, cause, damage);
	}

	@Deprecated
	public MagicalAttackEvent(Entity damager, Entity damagee, DamageCause cause, int damage) {
		super(damager, damagee, cause, damage);
	}

	public MagicalAttackEvent(Entity damager, Entity damagee, DamageCause cause, Map<DamageModifier, Double> modifiers,
			Map<DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions) {
		super(damager, damagee, cause, modifiers, modifierFunctions);
	}
}
