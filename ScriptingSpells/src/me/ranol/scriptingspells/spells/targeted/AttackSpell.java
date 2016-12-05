package me.ranol.scriptingspells.spells.targeted;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.SpellParser;
import me.ranol.scriptingspells.api.defaultparser.DamageCauseParser;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.api.docs.SpigotDoc;
import me.ranol.scriptingspells.api.docs.ValueList;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@SpellDocs("대상에게 공격을 가하는 스펠입니다.")
public class AttackSpell extends TargetedEntitySpell {
	@SpellOption("damage")
	@OptionDocs("대상에게 가하는 데미지입니다.")
	protected double damage = 4;

	@SpellOption("damage-cause")
	@SpellParser(DamageCauseParser.class)
	@OptionDocs("대상이 맞는 공격의 종류입니다.")
	@ValueList("각 버킷 버전에 따라 다르며, 자세한 것은 Spigot Doc을 참조하세요")
	@SpigotDoc("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html")
	protected DamageCause damageCause = DamageCause.ENTITY_ATTACK;

	public AttackSpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		double dam = damage * power;
		target.damage(dam, caster);
		return SpellCastState.SUCESS;
	}

}
