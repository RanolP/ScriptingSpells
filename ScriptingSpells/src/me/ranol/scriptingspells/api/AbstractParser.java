package me.ranol.scriptingspells.api;

import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractParser<T, U> implements IParser<U> {
	public static final AbstractParser<?, ?> DEFAULT = new AbstractParser() {

		@Override
		public String options() {
			return "";
		}

		@Override
		public Object parse(Object object) {
			return object;
		}
	};

	@Override
	public U parse(ConfigurationSection section, String key) {
		Object o;
		if (section.isBoolean(key)) {
			o = section.getBoolean(key);
		} else if (section.isDouble(key)) {
			o = section.getDouble(key);
		} else if (section.isInt(key)) {
			o = section.getInt(key);
		} else if (section.isLong(key)) {
			o = section.getLong(key);
		} else if (section.isString(key)) {
			o = section.getString(key);
		} else if (section.isList(key)) {
			o = section.getList(key);
		} else o = section.get(key);

		return parse((T) o);
	}

	public abstract U parse(T object);

}
