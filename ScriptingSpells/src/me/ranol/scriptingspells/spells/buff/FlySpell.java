package me.ranol.scriptingspells.spells.buff;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.ranol.scriptingspells.StaticScheduler;
import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.spells.BuffSpell;
import me.ranol.scriptingspells.utils.UUIDStorage;
import me.ranol.scriptingspells.utils.Wrap;

public class FlySpell extends BuffSpell {

	@ConfigOption("cast-with-vector")
	@ConfigDocument("스펠 사용 시, 위로 살짝 뜰 여부입니다.")
	protected boolean castWithVector = true;

	public FlySpell(String name) {
		super(name);
	}

	UUIDStorage<Boolean> alreadyActivate = new UUIDStorage<>();

	@Override
	public SpellCastState activate(LivingEntity e, float power) {
		if (e instanceof Player) {
			alreadyActivate.set(e, ((Player) e).getAllowFlight());
			((Player) e).setAllowFlight(true);
			if (castWithVector) e.setVelocity(new Vector(0, 0.3, 0));
			((Player) e).setFlying(true);

			Wrap<Integer> temp = Wrap.empty();
			temp.set(StaticScheduler.repeatTask(() -> {
				playEffects(EffectPosition.TICKS, e);
				if (!isActive(e)) {
					deactivate(e);
					StaticScheduler.cancelTask(temp);
				}
			}, 20));
			return SpellCastState.SUCESS;
		}
		return SpellCastState.CANTCAST;
	}

	@Override
	public void deactivate(LivingEntity e) {
		super.deactivate(e);
		((Player) e).setAllowFlight(alreadyActivate.get(e));
		playEffects(EffectPosition.END, e);
	}

}
