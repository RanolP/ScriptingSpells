package me.ranol.scriptingspells.spells;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.docs.ClassDocument;

@ClassDocument("모든 위치 대상 지정 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class TargetedLocationSpell extends TargetedSpell {
	public TargetedLocationSpell(String name) {
		super(name);
	}

	public abstract SpellCastState castAtLocation(LivingEntity caster, Location target, float power);

	@Override
	public final SpellCastState castReal(LivingEntity entity, float power) {
		Location loc = getTarget(entity, range * power);
		if (loc == null) return SpellCastState.NOTARGET;
		SpellCastState state = castAtLocation(entity, loc, power);
		return state;
	}

	private final Location getTarget(LivingEntity caster, double range) {
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
