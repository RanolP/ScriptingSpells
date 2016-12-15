package me.ranol.scriptingspells.api;

import static me.ranol.scriptingspells.ScriptingSpells.error;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.api.defaultparser.CastItemParser;
import me.ranol.scriptingspells.api.defaultparser.EffectsParser;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.effects.EffectPosition;
import me.ranol.scriptingspells.api.effects.SpellEffect;
import me.ranol.scriptingspells.exceptions.ParserException;
import me.ranol.scriptingspells.utils.UUIDStorage;

@ClassDocument("모든 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class Spell extends OptionReciever {
	public static final Spell NONE = new Spell("ScriptingSpells:NONE") {

		@Override
		public SpellCastState castReal(LivingEntity entity, float power) {
			return SpellCastState.SUCESS;
		}
	};

	protected boolean cooldownCheck = true;

	@ConfigOption("debug")
	@ConfigDocument("스펠을 디버깅할 여부입니다.")
	protected boolean debug = false;

	@ConfigOption("cooldown")
	@ConfigDocument("스펠의 재사용 대기 시간입니다.")
	protected float cooldown = 15f;

	@ConfigOption("always-have")
	@ConfigDocument("모든 시전자가 스펠을 가지는 여부입니다.")
	protected boolean alwaysHave = true;

	@ConfigOption("mana")
	@ConfigDocument("스펠 사용시 소모되는 마나량입니다.")
	protected int mana = 0;

	@ConfigOption("caster-message")
	@ConfigDocument("시전자에게 보내는 메시지입니다.")
	protected String casterMessage = "";

	@ConfigOption("cast-items")
	@ConfigParser(CastItemParser.class)
	@ConfigDocument("스펠을 사용 가능하게 지정된 아이템입니다.")
	protected CastItem[] castItems = {};

	private final String name;

	private UUIDStorage<Long> castAt = new UUIDStorage<>();

	@ConfigOption("effects")
	@ConfigParser(EffectsParser.class)
	@ConfigDocument("스펠에 추가적으로 부여되는 효과입니다.")
	private HashMap<EffectPosition, List<SpellEffect>> effects = new HashMap<>();

	public Spell(String name) {
		this.name = name;
		if (!isFieldRegistered(this.getClass())) registerFields();
	}

	public boolean hasAllUsers() {
		return alwaysHave;
	}

	public String getName() {
		return name;
	}

	public void playEffects(EffectPosition pos, LivingEntity caster) {
		if (!effects.containsKey(pos)) {
			return;
		}
		List<SpellEffect> effect = effects.get(pos);
		for (SpellEffect e : effect) {
			e.play(pos, caster);
		}
	}

	public void playEffects(EffectPosition pos, LivingEntity caster, Location target) {
		if (!effects.containsKey(pos)) {
			return;
		}
		List<SpellEffect> effect = effects.get(pos);
		for (SpellEffect e : effect) {
			e.play(pos, caster, target);
		}
	}

	public void playEffects(EffectPosition pos, LivingEntity caster, LivingEntity target) {
		if (!effects.containsKey(pos)) {
			return;
		}
		List<SpellEffect> effect = effects.get(pos);
		for (SpellEffect e : effect) {
			e.play(pos, caster, target);
		}
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

	@Override
	public boolean setOption(String key, Object value) {
		try {
			return super.setOption(key, value);
		} catch (ParserException e) {
			error("스킬 " + getName() + "에서 " + key + " 옵션 로드 중에 에러가 발생했습니다.");
			error("옵션 " + key + "의 값이 정확하지 않습니다.");
			error("옵션 " + key + "에는 " + e.getParser()
				.options() + "만이 올 수 있습니다.");
			ScriptingSpells.fancyException(e.getCause());
		}
		return false;
	}

	@Override
	public boolean setOption(String key, ConfigurationSection section, String realKey) {
		try {
			return super.setOption(key, section, realKey);
		} catch (ParserException e) {
			error("스킬 " + getName() + "에서 " + key + " 옵션 로드 중에 에러가 발생했습니다.");
			error("옵션 " + key + "의 값이 정확하지 않습니다.");
			error("옵션 " + key + "에는 " + e.getParser()
				.options() + "만이 올 수 있습니다.");
			ScriptingSpells.fancyException(e.getCause());
		}
		return false;
	}

	public boolean onCooldown(LivingEntity caster) {
		return getCooldown(caster) > 0.0f;
	}

	protected void cooldown(LivingEntity caster) {
		castAt.set(caster.getUniqueId(), System.currentTimeMillis() + (long) (cooldown * 1000));
	}

	public SpellCastState cast(LivingEntity caster, float power) {
		if (cooldownCheck && onCooldown(caster)) return SpellCastState.COOLDOWN;
		SpellCastState state = castReal(caster, power);
		if (!state.isSpellCancelled()) {
			cooldown(caster);
			playEffects(EffectPosition.CASTER, caster);
			if (!casterMessage.isEmpty()) caster.sendMessage(casterMessage.replace('&', '§')
				.replace("§§", "&"));
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
				result.setOption(s, cfg.getConfigurationSection(key), s);
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
			ScriptingSpells.fancyException(e.getTargetException());
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