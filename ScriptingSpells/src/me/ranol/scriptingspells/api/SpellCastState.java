package me.ranol.scriptingspells.api;

public enum SpellCastState {
	SUCESS(false),

	COOLDOWN(true),

	CANCELLED(false),

	NOTARGET(true),

	CANTCAST(true);

	private final boolean cancel;

	SpellCastState(boolean cancelled) {
		cancel = cancelled;
	}

	public boolean isSpellCancelled() {
		return cancel;
	}
}
