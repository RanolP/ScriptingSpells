package me.ranol.scriptingspells.spells.targeted;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@ClassDocument("대상을 벡터로 밀어 이동시킵니다.")
public class VelocitySpell extends TargetedEntitySpell {

	@ConfigOption("horiz-velocity")
	@ConfigDocument("x, z 반경으로 움직이는 양입니다.")
	protected double horizVelocity = 40;

	@ConfigOption("vert-velocity")
	@ConfigDocument("y 반경으로 움직이는 양입니다, 0 미만일 경우 대상의 보는 방향 영향으로 갑니다.")
	protected double vertVelocity = 10;

	@ConfigOption("cancel-damage")
	@ConfigDocument("데미지 취소 여부입니다.")
	protected boolean cancelDamage = false;

	@ConfigOption("yaw")
	@ConfigDocument("적용된 값만큼 x, z 이동의 방향이 변경됩니다.")
	protected float yaw = 0;

	@ConfigOption("caster-location")
	@ConfigDocument("시전자 좌표 기준으로 계산하는 여부입니다.")
	protected boolean casterLocation = true;

	private HashSet<UUID> cancel = new HashSet<>();

	public VelocitySpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		Location tloc = target.getLocation();
		tloc.setYaw((casterLocation ? caster.getLocation()
			.getYaw() : tloc.getYaw()) + yaw);
		Vector v = tloc.getDirection();
		if (vertVelocity > 0) {
			v.setY(vertVelocity / 10 * power);
		}
		v.normalize()
			.multiply(horizVelocity / 10 * power);
		target.setVelocity(v);
		if (!caster.equals(target)) playEffects(EffectPosition.LINE, caster, target);
		playEffects(EffectPosition.TARGET, caster, target);
		if (cancelDamage) cancel.add(target.getUniqueId());
		return SpellCastState.SUCESS;
	}

}
