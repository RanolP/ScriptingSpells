package me.ranol.scriptingspells.spells.targeted;

import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@ClassDocument("대상의 체력을 수정하는 스펠입니다.")
public class HealthModifySpell extends TargetedEntitySpell {
	@ConfigOption("modify")
	@ConfigDocument("수정할 양입니다.")
	protected double modify = 8;

	public HealthModifySpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		double h = target.getHealth() + modify * power;
		if (h >= target.getMaxHealth()) h = target.getMaxHealth();
		target.setHealth(h);
		playEffects(EffectPosition.TARGET, caster, target);
		playEffects(EffectPosition.LINE, caster, target);
		return SpellCastState.SUCESS;
	}

}
