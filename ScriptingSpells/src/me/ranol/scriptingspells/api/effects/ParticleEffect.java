package me.ranol.scriptingspells.api.effects;

import org.bukkit.Location;

import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.exceptions.InvalidParticleException;

@ClassDocument("입자 효과입니다.")
public class ParticleEffect extends SpellEffect {
	@ConfigOption("count")
	@ConfigDocument("입자의 갯수입니다.")
	protected int count = 100;

	@ConfigOption("horiz-spread")
	@ConfigDocument("입자가 가로로 퍼지는 크기입니다.")
	protected float horizSpread = 1f;

	@ConfigOption("vert-spread")
	@ConfigDocument("입자가 세로로 퍼지는 크기입니다.")
	protected float vertSpread = 1f;

	@ConfigOption("speed")
	@ConfigDocument("입자의 속도입니다.")
	protected float speed = 0;

	@ConfigOption("name")
	@ConfigDocument("입자의 이름입니다.")
	protected String name = "FLAME";

	@ConfigOption("radius")
	@ConfigDocument("입자가 보이는 범위입니다.")
	protected int radius = 20;

	@Override
	public String getName() {
		return "particle";
	}

	@Override
	public void playAtLocation(EffectPosition pos, Location l) {
		if (ScriptingSpells.getVolatile()
			.isEnabled()) {
			try {
				ScriptingSpells.getVolatile()
					.playParticle(l, name, speed, count, radius, horizSpread, vertSpread, horizSpread);
			} catch (InvalidParticleException e) {
				ScriptingSpells.fancyException(e);
			}
		}
	}

}
