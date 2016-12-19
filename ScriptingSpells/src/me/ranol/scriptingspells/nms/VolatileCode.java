package me.ranol.scriptingspells.nms;

import org.bukkit.Location;

import me.ranol.scriptingspells.exceptions.InvalidParticleException;

public interface VolatileCode {
	public boolean isEnabled();

	public void playParticle(Location target, String name, float speed, int count, int radius, float xSpread, float ySpread,
			float zSpread) throws InvalidParticleException;
}
