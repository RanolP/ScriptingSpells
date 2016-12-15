package me.ranol.scriptingspells.spells;

import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;

@ClassDocument("모든 대상 지정 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class TargetedSpell extends InstantSpell {
	@ConfigOption("range")
	@ConfigDocument("스펠이 맞을 수 있는 사거리입니다.")
	protected double range = 10;

	public TargetedSpell(String name) {
		super(name);
	}

}
