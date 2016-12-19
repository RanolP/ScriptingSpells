package me.ranol.scriptingspells.nms;

import org.bukkit.Location;

public class VolatileCodeDisabled implements VolatileCode {
	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void playParticle(Location target, String name, float speed, int count, int radius, float xSpread,
			float ySpread, float zSpread) {
	}
}
