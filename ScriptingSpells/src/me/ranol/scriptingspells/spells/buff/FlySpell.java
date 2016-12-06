package me.ranol.scriptingspells.spells.buff;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.ranol.scriptingspells.StaticScheduler;
import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.spells.BuffSpell;
import me.ranol.scriptingspells.utils.UUIDStorange;
import me.ranol.scriptingspells.utils.Wrap;

public class FlySpell extends BuffSpell {

	@SpellOption("cast-with-vector")
	@OptionDocs("스펠 사용 시, 위로 살짝 뜰 여부입니다.")
	protected boolean castWithVector = true;

	public FlySpell(String name) {
		super(name);
	}

	UUIDStorange<Boolean> alreadyActivate = new UUIDStorange<>();

	@Override
	public SpellCastState activate(LivingEntity e, float power) {
		if (e instanceof Player) {
			alreadyActivate.set(e, ((Player) e).getAllowFlight());
			((Player) e).setAllowFlight(true);
			if (castWithVector) e.setVelocity(new Vector(0, 0.3, 0));
			((Player) e).setFlying(true);

			Wrap<Integer> temp = Wrap.empty();
			temp.set(StaticScheduler.repeatTask(() -> {
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
	}

}
