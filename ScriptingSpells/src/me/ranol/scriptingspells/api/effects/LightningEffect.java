package me.ranol.scriptingspells.api.effects;

import org.bukkit.Location;

import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.ConfigDocument;

@ClassDocument("번개 효과입니다.")
public class LightningEffect extends SpellEffect {
	@ConfigOption("silent")
	@ConfigDocument("번개가 조용하게 치는 여부입니다.")
	protected boolean silent = false;

	@Override
	public String getName() {
		return "lightning";
	}

	@Override
	public void playAtLocation(EffectPosition pos, Location l) {
		l.getWorld()
			.spigot()
			.strikeLightningEffect(l, silent);
	}

}
