package me.ranol.scriptingspells.particles;

import org.bukkit.Effect;
import org.bukkit.Location;

public class SpigotParticlePlayer implements ParticlePlayer {

	@Override
	public void playParticle(Location l, String name, double speed, int count, double radius, float spreadX,
			float spreadY, float spreadZ) {
		l.getWorld()
			.spigot()
			.playEffect(l, Effect.valueOf(name));
	}

}
