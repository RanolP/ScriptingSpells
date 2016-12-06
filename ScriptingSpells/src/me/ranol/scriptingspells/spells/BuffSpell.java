package me.ranol.scriptingspells.spells;

import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.utils.UUIDStorange;

@SpellDocs("모든 지속형 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class BuffSpell extends TargetedEntitySpell {
	@SpellOption("targeted")
	@OptionDocs("바라보는 대상에게 버프를 걸 여부입니다.")
	protected boolean targeted = false;

	@SpellOption("toggle")
	@OptionDocs("버프를 껏다 켰다 할 수 있는 여부입니다.")
	protected boolean toggle = true;

	@SpellOption("disable-message")
	@OptionDocs("종료되었을 때, 대상에게 보내는 메시지입니다.")
	protected String disableMessage = "";

	@SpellOption("duration")
	@OptionDocs("버프의 지속 시간입니다.")
	protected float duration = 0f;

	private UUIDStorange<Long> endAt = new UUIDStorange<>();

	public BuffSpell(String name) {
		super(name);
		cooldownCheck = false;
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		LivingEntity e = targeted ? target : caster;
		if (toggle && isActive(e)) {
			deactivate(e);
			cooldown(e);
			return SpellCastState.IGNORE;
		}
		if (onCooldown(caster)) return SpellCastState.COOLDOWN;
		endAt.set(e.getUniqueId(), System.currentTimeMillis() + (long) (duration * power) * 1000);
		SpellCastState state = activate(e, power);
		return state;
	}

	public abstract SpellCastState activate(LivingEntity e, float power);

	public void deactivate(LivingEntity e) {
		endAt.remove(e.getUniqueId());
		if (!disableMessage.isEmpty()) e.sendMessage(disableMessage.replace('&', '§')
			.replace("§§", "&"));
	}

	public final boolean isActive(LivingEntity e) {
		return remainDuration(e) > 0f;
	}

	public final float remainDuration(LivingEntity e) {
		if (endAt.containsKey(e.getUniqueId()))
			return (endAt.get(e.getUniqueId()) - System.currentTimeMillis()) / 1000f;
		return 0f;
	}

}
