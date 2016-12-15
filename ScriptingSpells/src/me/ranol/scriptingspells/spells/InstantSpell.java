package me.ranol.scriptingspells.spells;

import me.ranol.scriptingspells.api.Spell;
import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;

@ClassDocument("모든 즉발성 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class InstantSpell extends Spell {
	@ConfigOption("can-cast-by-command")
	@ConfigDocument("명령어를 통해 사용 가능하게 할 여부입니다.")
	protected boolean canCastByCommand = true;

	public InstantSpell(String name) {
		super(name);
	}

	public final boolean canCastByCommand() {
		return canCastByCommand;
	}
}
