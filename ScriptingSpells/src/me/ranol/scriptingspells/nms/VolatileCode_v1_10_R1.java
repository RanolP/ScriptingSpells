package me.ranol.scriptingspells.nms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.exceptions.InvalidParticleException;
import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;

public class VolatileCode_v1_10_R1 extends VolatileCodeEnabled {
	private Field[] particlePacketField = new Field[11];
	private HashMap<String, EnumParticle> particleMap = new HashMap<>();

	public VolatileCode_v1_10_R1() {
		try {
			particlePacketField[0] = PacketPlayOutWorldParticles.class.getDeclaredField("a");
			particlePacketField[1] = PacketPlayOutWorldParticles.class.getDeclaredField("b");
			particlePacketField[2] = PacketPlayOutWorldParticles.class.getDeclaredField("c");
			particlePacketField[3] = PacketPlayOutWorldParticles.class.getDeclaredField("d");
			particlePacketField[4] = PacketPlayOutWorldParticles.class.getDeclaredField("e");
			particlePacketField[5] = PacketPlayOutWorldParticles.class.getDeclaredField("f");
			particlePacketField[6] = PacketPlayOutWorldParticles.class.getDeclaredField("g");
			particlePacketField[7] = PacketPlayOutWorldParticles.class.getDeclaredField("h");
			particlePacketField[8] = PacketPlayOutWorldParticles.class.getDeclaredField("i");
			particlePacketField[9] = PacketPlayOutWorldParticles.class.getDeclaredField("j");
			particlePacketField[10] = PacketPlayOutWorldParticles.class.getDeclaredField("k");
			Arrays.stream(particlePacketField)
				.forEach(f -> f.setAccessible(true));
			Arrays.stream(EnumParticle.values())
				.forEach(e -> particleMap.put(e.b(), e));
		} catch (Exception e) {

		}
	}

	@Override
	public void playParticle(Location target, String name, float speed, int count, int radius, float xSpread,
			float ySpread, float zSpread) throws InvalidParticleException {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		int[] data = null;
		String applyName = name;
		if (name.contains("_")) {
			String[] split = name.split("_");
			applyName = split[0] + "_";
			if (split.length > 1) {
				String[] split2 = split[1].split(":");
				data = new int[split2.length];
				for (int i = 0; i < data.length; i++) {
					data[i] = Integer.parseInt(split2[i]);
				}
			}
		}
		EnumParticle particle = particleMap.get(applyName);
		if (particle == null) throw new InvalidParticleException(name);
		try {
			particlePacketField[0].set(packet, particle);
			particlePacketField[1].setFloat(packet, (float) target.getX());
			particlePacketField[2].setFloat(packet, (float) target.getY());
			particlePacketField[3].setFloat(packet, (float) target.getZ());
			particlePacketField[4].setFloat(packet, xSpread);
			particlePacketField[5].setFloat(packet, ySpread);
			particlePacketField[6].setFloat(packet, zSpread);
			particlePacketField[7].setFloat(packet, speed);
			particlePacketField[8].setInt(packet, count);
			particlePacketField[9].setBoolean(packet, radius >= 30);
			if (data != null) particlePacketField[10].set(packet, data);
			int rSq = radius * radius;
			for (Player p : target.getWorld()
				.getPlayers()) {
				if (p.getLocation()
					.distanceSquared(target) <= rSq) {
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				}
			}
		} catch (Exception e) {
			ScriptingSpells.fancyException(e);
		}
	}
}
