package me.ranol.scriptingspells.spells.targeted;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.spells.TargetedEntitySpell;

@SpellDocs("대상을 벡터로 밀어 이동시킵니다.")
public class VelocitySpell extends TargetedEntitySpell {

	@SpellOption("horiz-velocity")
	@OptionDocs("x, z 반경으로 움직이는 양입니다.")
	protected double horizVelocity = 40;

	@SpellOption("vert-velocity")
	@OptionDocs("y 반경으로 움직이는 양입니다.")
	protected double vertVelocity = 10;

	@SpellOption("cancel-damage")
	@OptionDocs("데미지 취소 여부입니다.")
	protected boolean cancelDamage = false;

	@SpellOption("yaw-modify")
	@OptionDocs("적용된 값만큼 x, z 이동의 방향이 변경됩니다.")
	protected float yawModify = 0;

	@SpellOption("caster-yaw")
	@OptionDocs("시전자 기준으로 yaw를 설정하는 여부입니다.")
	protected boolean casterYaw = true;

	private HashSet<UUID> cancel = new HashSet<>();

	public VelocitySpell(String name) {
		super(name);
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		Location tloc = target.getLocation();
		tloc.setYaw((casterYaw ? caster.getLocation()
			.getYaw() : tloc.getYaw()) + yawModify);
		Vector v = tloc.getDirection();
		v.setY(0)
			.normalize()
			.multiply(horizVelocity / 10 * power)
			.setY(vertVelocity / 10 * power);
		target.setVelocity(v);
		if (cancelDamage) cancel.add(target.getUniqueId());
		return SpellCastState.SUCESS;
	}

}
