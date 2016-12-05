package me.ranol.scriptingspells.spells;

import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;

@SpellDocs("모든 대상 지정 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class TargetedSpell extends InstantSpell {
	@SpellOption("range")
	@OptionDocs("스펠이 맞을 수 있는 사거리입니다.")
	protected double range = 10;

	public TargetedSpell(String name) {
		super(name);
	}

}
