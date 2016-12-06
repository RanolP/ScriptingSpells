package me.ranol.scriptingspells.particles;

import org.bukkit.Location;

public interface ParticlePlayer {
	public void playParticle(Location l, String name, double speed, int count, double radius, float spreadX,
			float spreadY, float spreadZ);
}
