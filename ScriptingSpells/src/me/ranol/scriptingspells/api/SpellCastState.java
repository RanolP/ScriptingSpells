package me.ranol.scriptingspells.api;

public enum SpellCastState {
	SUCESS(false),
	COOLDOWN(true),
	EVENT_CANCEL(false),
	NOTARGET(true),
	CANTCAST(true),
	IGNORE(false),
	IGNORE_CANCEL(true),
	BUFF_DISABLE(false);

	private final boolean cancel;

	SpellCastState(boolean cancelled) {
		cancel = cancelled;
	}

	public boolean isSpellCancelled() {
		return cancel;
	}
}
