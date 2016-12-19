package me.ranol.scriptingspells.spells;

import org.bukkit.entity.LivingEntity;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.utils.UUIDStorage;

@ClassDocument("모든 지속형 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class BuffSpell extends TargetedEntitySpell {
	@ConfigOption("targeted")
	@ConfigDocument("바라보는 대상에게 버프를 걸 여부입니다.")
	protected boolean targeted = false;

	@ConfigOption("toggle")
	@ConfigDocument("버프를 껏다 켰다 할 수 있는 여부입니다.")
	protected boolean toggle = true;

	@ConfigOption("disable-message")
	@ConfigDocument("종료되었을 때, 대상에게 보내는 메시지입니다.")
	protected String disableMessage = "";

	@ConfigOption("duration")
	@ConfigDocument("버프의 지속 시간입니다.")
	protected float duration = 0f;

	@ConfigOption("start-at-cooldown")
	@ConfigDocument("시작과 동시에 쿨타임을 적용시킬 여부입니다.")
	protected boolean startAtCooldown = false;

	private UUIDStorage<Long> endAt = new UUIDStorage<>();

	public BuffSpell(String name) {
		super(name);
		cooldownCheck = false;
	}

	@Override
	public SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		LivingEntity e = targeted ? target : caster;
		if (startAtCooldown) cooldown(e);
		if (toggle && isActive(e)) {
			if (!startAtCooldown) cooldown(e);
			deactivate(e);
			return SpellCastState.BUFF_DISABLE;
		}
		if (onCooldown(caster)) return SpellCastState.COOLDOWN;
		endAt.set(e.getUniqueId(), System.currentTimeMillis() + (long) (duration * power) * 1000);
		SpellCastState state = activate(e, power);
		return state;
	}

	public abstract SpellCastState activate(LivingEntity e, float power);

	public void deactivate(LivingEntity e) {
		if (!endAt.containsKey(e)) return;
		endAt.remove(e);
		if (!disableMessage.isEmpty()) e.sendMessage(disableMessage.replace('&', '§')
			.replace("§§", "&"));
	}

	public final boolean isActive(LivingEntity e) {
		return remainDuration(e) > 0f && endAt.containsKey(e);
	}

	public final float remainDuration(LivingEntity e) {
		if (endAt.containsKey(e.getUniqueId()))
			return (endAt.get(e.getUniqueId()) - System.currentTimeMillis()) / 1000f;
		return 0f;
	}

}
