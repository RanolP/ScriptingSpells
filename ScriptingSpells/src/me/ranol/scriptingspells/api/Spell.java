package me.ranol.scriptingspells.api;

import static me.ranol.scriptingspells.ScriptingSpells.error;
import static me.ranol.scriptingspells.ScriptingSpells.line;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import me.ranol.scriptingspells.ParserManagement;
import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.api.defaultparser.CastItemParser;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.utils.UUIDStorange;

@SpellDocs("모든 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class Spell {
	public static final Spell NONE = new Spell("ScriptingSpells:NONE") {

		@Override
		public SpellCastState castReal(LivingEntity entity, float power) {
			return SpellCastState.SUCESS;
		}
	};

	@SpellOption("debug")
	@OptionDocs("스펠을 디버깅할 여부입니다.")
	protected boolean debug = false;

	@SpellOption("cooldown")
	@OptionDocs("스펠의 재사용 대기 시간입니다.")
	protected float cooldown = 15f;

	@SpellOption("always-have")
	@OptionDocs("모든 시전자가 스펠을 가지는 여부입니다.")
	protected boolean alwaysHave = true;

	@SpellOption("mana")
	@OptionDocs("스펠 사용시 소모되는 마나량입니다.")
	protected int mana = 0;

	@SpellOption("caster-message")
	@OptionDocs("시전자에게 보내는 메시지입니다.")
	protected String casterMessage = "";

	@SpellOption("cast-items")
	@SpellParser(CastItemParser.class)
	@OptionDocs("스펠을 사용 가능하게 지정된 아이템입니다.")
	protected CastItem[] castItems = {};

	private final String name;

	private UUIDStorange<Long> castAt = new UUIDStorange<>();

	private static HashMap<Class<? extends Spell>, HashMap<String, Field>> fieldMap = new HashMap<>();

	private static final boolean isFieldRegistered(Class<? extends Spell> clazz) {
		if (fieldMap == null) synchronized (clazz) {
			fieldMap = new HashMap<>();
		}
		return fieldMap.containsKey(clazz);
	}

	public Spell(String name) {
		this.name = name;
		if (!isFieldRegistered(this.getClass())) registerFields();
		if (this instanceof Listener) {
			Bukkit.getPluginManager()
				.registerEvents((Listener) this, ScriptingSpells.getInstance());
		}
	}

	private final void registerFields() {
		HashMap<String, Field> fields = new HashMap<>();
		Class<?> sup = this.getClass();
		while (Spell.class.isAssignableFrom(sup)) {
			for (Field f : sup.getDeclaredFields()) {
				SpellOption anno = f.getAnnotation(SpellOption.class);
				if (anno != null) {
					f.setAccessible(true);
					fields.put(anno.value(), f);
				}

			}
			sup = sup.getSuperclass();
		}
		fieldMap.put(this.getClass(), fields);
	}

	private final Class<?> getFieldClass(String key) {
		if (!isFieldRegistered(getClass())) return Object.class;
		HashMap<String, Field> fields = fieldMap.get(getClass());
		if (fields.containsKey(key)) {
			try {
				return fields.get(key)
					.getType();
			} catch (IllegalArgumentException e) {
				return Object.class;
			}
		}
		return Object.class;
	}

	public final boolean setOption(String key, Object value) {
		if (!isFieldRegistered(getClass())) return false;
		HashMap<String, Field> fields = fieldMap.get(getClass());
		if (fields.containsKey(key)) {
			try {
				Field field = fields.get(key);
				SpellParser parser = field.getAnnotation(SpellParser.class);
				if (parser != null) {
					IParser<Object, Object> p = ParserManagement.restore(parser.value());
					try {
						value = p.parse(value);
					} catch (Exception e) {
						error("스킬 " + getName() + "에서 " + key + " 스킬 로드 중에 에러가 발생했습니다.");
						error("옵션 " + key + "의 값이 정확하지 않습니다.");
						error("옵션 " + key + "에는 " + p.options() + "만이 올 수 있습니다.");
						return false;
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

	public String getName() {
		return name;
	}

	public float getCooldown(LivingEntity caster) {
		if (castAt.containsKey(caster.getUniqueId()))
			return (castAt.get(caster.getUniqueId()) - System.currentTimeMillis()) / 1000f;
		return 0;
	}

	public float getCooldown(CommandSender s) {
		if (s instanceof LivingEntity) return getCooldown((LivingEntity) s);
		return 0f;
	}

	public boolean onCooldown(LivingEntity caster) {
		return getCooldown(caster) > 0.0f;
	}

	private void cooldown(LivingEntity caster) {
		castAt.set(caster.getUniqueId(), System.currentTimeMillis() + (long) (cooldown * 1000));
	}

	public SpellCastState cast(LivingEntity caster, float power) {
		if (onCooldown(caster)) return SpellCastState.COOLDOWN;
		SpellCastState state = castReal(caster, power);
		if (!state.isSpellCancelled()) {
			cooldown(caster);
			if (!casterMessage.isEmpty()) caster.sendMessage(casterMessage.replace('&', '§')
				.replace("&&", "&"));
		}
		return state;
	}

	public abstract SpellCastState castReal(LivingEntity entity, float power);

	public static Spell newInstance(YamlConfiguration cfg, String key, String className) {
		if (className.startsWith(".")) className = "me.ranol.scriptingspells.spells" + className;
		Spell result = Spell.NONE;
		try {
			Class<?> clazz = Class.forName(className);
			if (!Spell.class.isAssignableFrom(clazz)) {
				error("스킬 파일 " + cfg.getCurrentPath() + "에서 " + key + " 스킬 로드 중에 에러가 발생했습니다.");
				error("스킬 클래스 " + className + "는 Spell을 상속받지 않았습니다.");
				return result;
			}
			result = (Spell) clazz.getConstructor(String.class)
				.newInstance(key);
			for (String s : cfg.getConfigurationSection(key)
				.getKeys(false)) {
				Object o;
				String keys = key + "." + s;
				Class<?> type = result.getFieldClass(keys);
				if (type == byte.class) o = (byte) cfg.getInt(keys);
				else if (type == short.class) o = (short) cfg.getInt(keys);
				else if (type == int.class) o = cfg.getInt(keys);
				else if (type == long.class) o = cfg.getInt(keys);
				else if (type == float.class) o = (float) cfg.getDouble(keys);
				else if (type == double.class) o = cfg.getDouble(keys);
				else if (type == boolean.class) o = cfg.getBoolean(keys);
				else if (type == String.class) o = cfg.getString(keys);
				else o = cfg.get(keys);
				if (o == null) continue;
				if (result.setOption(s, o)) {
					ScriptingSpells.debug("옵션 설정 완료: " + s + "=" + o);
				}
			}
		} catch (ClassNotFoundException e) {
			error("스킬 파일 " + cfg.getCurrentPath() + "에서 " + key + " 스킬 로드 중에 에러가 발생했습니다.");
			error("스킬 클래스 " + className + "은 존재하지 않습니다.");
		} catch (InstantiationException e) {
			error("스킬 클래스 " + className + "를 생성하는 도중 오류가 발생했습니다.");
			error("인스턴스화 불가능한 종류(abstract class, interface)의 클래스입니다.");
			error("플러그인 제작자에게 문의하여, 이 오류를 수정하세요.");
		} catch (IllegalAccessException e) {
			error("스킬 클래스 " + className + "를 생성하는 도중 오류가 발생했습니다.");
			error("공개되어 있는 생성자가 존재하지 않습니다.");
			error("플러그인 제작자에게 문의하여, 이 오류를 수정하세요.");
		} catch (IllegalArgumentException e) {
			error("스킬 클래스 " + className + "를 생성하는 도중 오류가 발생했습니다.");
			error("String만을 인자로 받는 생성자가 존재하지 않습니다.");
			error("플러그인 제작자에게 문의하여, 이 오류를 수정하세요.");
		} catch (InvocationTargetException e) {
			error("스킬 클래스 " + className + "를 생성하는 도중 오류가 발생했습니다.");
			error("인스턴스 생성 도중, 생성자가 오류를 보냈습니다, 오류 StackTrace::");
			line('6', 'l');
			Throwable t = e.getTargetException();
			error(t.getClass()
				.getName() + ": " + t.getMessage());
			for (StackTraceElement s : t.getStackTrace()) {
				error(
						s.getFileName() + "»" + s.getClassName() + "." + s.getMethodName() + "(" + s.getLineNumber()
								+ ")");
			}
			line('6', 'l');
			error("플러그인 제작자에게 문의하여, 이 오류를 수정하세요.");
		} catch (NoSuchMethodException e) {
			error("스킬 클래스 " + className + "를 생성하는 도중 오류가 발생했습니다.");
			error("YamlConfiguration과 String을 인자로 받는 생성자가 존재하지 않습니다.");
			error("플러그인 제작자에게 문의하여, 이 오류를 수정하세요.");
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isNone(Spell spell) {
		return spell.getName()
			.equals("ScriptingSpells:NONE");
	}
}