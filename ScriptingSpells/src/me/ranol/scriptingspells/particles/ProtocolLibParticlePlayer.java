package me.ranol.scriptingspells.particles;

import org.bukkit.Location;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class ProtocolLibParticlePlayer implements ParticlePlayer {
	ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	@Override
	public void playParticle(Location l, String name, double speed, int count, double radius, float spreadX,
			float spreadY, float spreadZ) {
		PacketContainer packet = pm.createPacket(63);
		packet.getStrings()
			.write(0, name);
		packet.getFloat()
			.write(0, (float) l.getX())
			.write(1, (float) l.getY())
			.write(2, (float) l.getZ());
	}

}
