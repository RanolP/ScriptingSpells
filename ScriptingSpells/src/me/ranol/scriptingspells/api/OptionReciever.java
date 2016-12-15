package me.ranol.scriptingspells.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import me.ranol.scriptingspells.ParserManagement;
import me.ranol.scriptingspells.exceptions.ParserException;

public class OptionReciever {
	private static HashMap<Class<? extends OptionReciever>, HashMap<String, Field>> fieldMap = new HashMap<>();

	protected static final boolean isFieldRegistered(Class<? extends OptionReciever> clazz) {
		if (fieldMap == null) synchronized (clazz) {
			fieldMap = new HashMap<>();
		}
		return fieldMap.containsKey(clazz);
	}

	protected final void registerFields() {
		HashMap<String, Field> fields = new HashMap<>();
		Class<?> sup = this.getClass();
		while (Spell.class.isAssignableFrom(sup)) {
			for (Field f : sup.getDeclaredFields()) {
				ConfigOption anno = f.getAnnotation(ConfigOption.class);
				if (anno != null) {
					f.setAccessible(true);
					fields.put(anno.value(), f);
				}

			}
			sup = sup.getSuperclass();
		}
		fieldMap.put(this.getClass(), fields);
	}

	public boolean setOption(String key, Object value) throws ParserException {
		if (!isFieldRegistered(getClass())) return false;
		HashMap<String, Field> fields = fieldMap.get(getClass());
		if (fields.containsKey(key)) {
			try {
				Field field = fields.get(key);
				ConfigParser parser = field.getAnnotation(ConfigParser.class);
				if (parser != null) {
					AbstractParser<Object, Object> p = null;
					Iterator<AbstractParser> it = Arrays.stream(ParserManagement.restore(parser.value()))
						.filter(pp -> pp instanceof AbstractParser)
						.map(pp -> (AbstractParser) pp)
						.collect(Collectors.toList())
						.iterator();
					try {
						while (it.hasNext()) {
							p = it.next();
							value = p.parse(value);
						}
					} catch (Exception e) {
						throw new ParserException(p, e);
					}
				}
				field.set(this, value);
				return true;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return false;
			}
		}
		return false;
	}

	public boolean setOption(String key, ConfigurationSection section, String realKey) throws ParserException {
		if (!isFieldRegistered(getClass())) return false;
		HashMap<String, Field> fields = fieldMap.get(getClass());
		if (fields.containsKey(key)) {
			try {
				Object value = AbstractParser.DEFAULT.parse(section, realKey);
				Field field = fields.get(key);
				ConfigParser parser = field.getAnnotation(ConfigParser.class);
				if (parser != null) {
					IParser<?> p = null;
					Iterator<IParser> it = Arrays.asList(ParserManagement.restore(parser.value()))
						.iterator();
					try {
						while (it.hasNext()) {
							p = it.next();
							value = p.parse(section, realKey);
						}
					} catch (Exception e) {
						throw new ParserException(p, e);
					}
				}
				field.set(this, value);
				return true;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return false;
			}
		}
		return false;
	}

}
