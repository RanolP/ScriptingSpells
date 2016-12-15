package me.ranol.scriptingspells.spells.targeted;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.ConfigParser;
import me.ranol.scriptingspells.api.defaultparser.DamageCauseParser;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.LinkDocument;
import me.ranol.scriptingspells.api.docs.ValueList;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@ClassDocument("대상에게 공격을 가하는 스펠입니다.")
public class AttackSpell extends TargetedEntitySpell {
	@ConfigOption("damage")
	@ConfigDocument("대상에게 가하는 데미지입니다.")
	protected double damage = 4;

	@ConfigOption("damage-cause")
	@ConfigParser(DamageCauseParser.class)
	@ConfigDocument("대상이 맞는 공격의 종류입니다.")
	@ValueList("각 버킷 버전에 따라 다르며, 자세한 것은 Spigot Doc을 참조하세요")
	@LinkDocument("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html")
	protected DamageCause damageCause = DamageCause.ENTITY_ATTACK;

	public AttackSpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		double dam = damage * power;
		target.damage(dam, caster);
		playEffects(EffectPosition.LINE, caster, target);
		playEffects(EffectPosition.TARGET, caster, target);
		return SpellCastState.SUCESS;
	}

}
