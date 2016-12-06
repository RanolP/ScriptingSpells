package me.ranol.scriptingspells.spells.targeted;

import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@SpellDocs("대상의 체력을 수정하는 스펠입니다.")
public class HealthModifySpell extends TargetedEntitySpell {
	@SpellOption("modify")
	@OptionDocs("수정할 양입니다.")
	protected double modify = 8;

	public HealthModifySpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		target.setHealth(target.getHealth() + modify * power);
		return SpellCastState.SUCESS;
	}

}
