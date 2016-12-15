package me.ranol.scriptingspells.api.effects;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;

public class LightningEffect extends SpellEffect {
	@ConfigOption("silent")
	@ConfigDocument("번개가 조용하게 치는 여부입니다.")
	boolean silent = false;

	@Override
	public String getName() {
		return "lightning";
	}

	@Override
	public void play(EffectPosition pos, LivingEntity caster) {
		play(pos, caster, caster.getLocation());
	}

	@Override
	public void play(EffectPosition pos, LivingEntity caster, LivingEntity target) {
		play(pos, caster, target.getLocation());
	}

	@Override
	public void play(EffectPosition pos, LivingEntity caster, Location target) {
		switch (pos) {
		case CASTER:
		case END:
		case TICKS:
			play(EffectPosition.LOCATION, caster, caster.getLocation());
			break;
		case TARGET:
		case LOCATION:
			target.getWorld()
				.spigot()
				.strikeLightningEffect(target, silent);
			break;
		case LINE:
			for (Location l : getLines(caster.getLocation(), target)) {
				play(EffectPosition.LOCATION, caster, l);
			}
			break;
		}
	}

}
