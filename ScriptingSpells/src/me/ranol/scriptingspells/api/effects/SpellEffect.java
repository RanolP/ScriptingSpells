package me.ranol.scriptingspells.api.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import me.ranol.scriptingspells.api.OptionReciever;

public abstract class SpellEffect extends OptionReciever {
	private int between = 1;
	private static final HashMap<String, Class<? extends SpellEffect>> registered = new HashMap<>();

	static {
		register(new LightningEffect());
	}

	public SpellEffect() {
		if (!isFieldRegistered(getClass())) registerFields();
	}

	public static void register(SpellEffect effect) {
		registered.put(effect.getName(), effect.getClass());
	}

	public void play(EffectPosition pos, LivingEntity caster) {
		playAtEntity(pos, caster);
	}

	public void play(EffectPosition pos, LivingEntity caster, LivingEntity target) {
		play(pos, caster, target.getLocation());
	}

	public void play(EffectPosition pos, LivingEntity caster, Location target) {
		playWithLocations(pos, caster.getLocation(), target);
	}

	public void playAtLocation(EffectPosition pos, Location l) {
	}

	public void playWithLocations(EffectPosition pos, Location l, Location l2) {
		playAtLocation(pos, l);
	}

	public void playAtEntity(EffectPosition pos, LivingEntity e) {
		playAtLocation(pos, e.getLocation());
	}

	public List<Location> getLines(Location a, Location b) {
		List<Location> result = new ArrayList<>();
		int c = (int) (Math.ceil(a.distance(b) / between) - 1);
		Vector v = b.toVector()
			.subtract(a.toVector())
			.normalize()
			.multiply(between);
		Location l = a.clone();
		for (int i = 0; i < c; i++) {
			l.add(v);
			result.add(l.clone());
		}
		return result;
	}

	public abstract String getName();

	public static SpellEffect newInstance(String name) {
		if (registered.containsKey(name)) {
			try {
				return registered.get(name)
					.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("효과 '" + name + "'을 사용할 수 없습니다, 기본 생성자가 존재하지 않습니다.");
			} catch (IllegalAccessException e) {
				throw new RuntimeException("효과 '" + name + "'을 사용할 수 없습니다, 공개적인 효과가 아닙니다.");
			}
		}
		return null;
	}

	public static Class<? extends SpellEffect> classByName(String name) {
		if (registered.containsKey(name)) {
			return registered.get(name);
		}
		return null;
	}

	public static String nameByClass(Class<? extends SpellEffect> clazz) {
		for (Entry<String, Class<? extends SpellEffect>> entry : registered.entrySet()) {
			if (entry.getValue()
				.equals(clazz)) return entry.getKey();
		}
		return "";
	}
}
