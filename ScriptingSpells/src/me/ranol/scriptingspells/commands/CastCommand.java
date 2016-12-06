package me.ranol.scriptingspells.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.ranol.scriptingspells.ScriptingSpells;
import me.ranol.scriptingspells.api.Spell;
import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellManager;
import me.ranol.scriptingspells.export.DocExporter;
import me.ranol.scriptingspells.utils.TabCompletor;
import me.ranol.scriptingspells.utils.TaskTimer;

public class CastCommand implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
		List<String> def = new ArrayList<>();
		if (a.length <= 1) {
			if (s.isOp()) {
				def.addAll(TabCompletor.complete(a[0], "reload", "rconf", "rspell", "export"));
			}
			if (s instanceof Player) {
				def.addAll(TabCompletor.complete(a[0], SpellManager.hasSpells(s)
					.stream()
					.map(Spell::getName)
					.collect(Collectors.toList())));
			}
		}
		return def;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			return true;
		}
		if (a[0].equals("reload")) {
			TaskTimer t = new TaskTimer();
			ScriptingSpells.msg(s, "컨피그을 다시 불러옵니다.");
			t.start();
			ScriptingSpells.loadConfigs();
			ScriptingSpells.msg(s, "컨피그를 불러왔습니다. (" + t.timeAsSecond() + "초 소모됨)");
			ScriptingSpells.msg(s, "스펠들을 다시 불러옵니다.");
			t.start();
			ScriptingSpells.loadSpells();
			ScriptingSpells.msg(s, "스펠들을 모두 불러왔습니다. (" + t.timeAsSecond() + "초 소모됨)");
			return true;
		} else if (a[0].equals("rconf")) {
			TaskTimer t = new TaskTimer();
			ScriptingSpells.msg(s, "컨피그을 다시 불러옵니다.");
			t.start();
			ScriptingSpells.loadConfigs();
			ScriptingSpells.msg(s, "컨피그를 불러왔습니다. (" + t.timeAsSecond() + "초 소모됨)");
			return true;
		} else if (a[0].equals("rspell")) {
			TaskTimer t = new TaskTimer();
			ScriptingSpells.msg(s, "스펠들을 다시 불러옵니다.");
			t.start();
			ScriptingSpells.loadSpells();
			ScriptingSpells.msg(s, "스펠들을 모두 불러왔습니다. (" + t.timeAsSecond() + "초 소모됨)");
			return true;
		} else if (a[0].equals("export")) {
			TaskTimer t = new TaskTimer();
			File file = new File(ScriptingSpells.getInstance()
				.getDataFolder(), a.length == 1 ? "docs.json" : a[1]);
			ScriptingSpells.msg(s, "Doc을 내보냅니다. (대상 : " + file.getAbsolutePath() + ")");
			t.start();
			DocExporter.exportAll(file);
			ScriptingSpells.msg(s, "Doc을 내보냈습니다. (" + t.timeAsSecond() + "초 소모됨)");
			return true;
		} else if (a.length == 1) {
			if (s instanceof Player) {
				Spell spell = SpellManager.getByName(a[0]);
				if (Spell.isNone(spell)) {
					ScriptingSpells.warn(s, "존재하지 않는 스킬입니다. [" + a[0] + "]");
					return false;
				}
				SpellCastState state = spell.cast((Player) s, 1.0f);
				switch (state) {
				case EVENT_CANCEL:
					ScriptingSpells.msg(s, "스펠 발동이 취소되었습니다.");
					break;
				case CANTCAST:
					ScriptingSpells.msg(s, "사용할 수 없습니다.");
					break;
				case COOLDOWN:
					ScriptingSpells.msg(s, "남은 대기 시간: " + spell.getCooldown(s) + "초");
					break;
				case NOTARGET:
					ScriptingSpells.msg(s, "타겟이 없습니다.");
					break;
				case IGNORE:
				case IGNORE_CANCEL:
				case SUCESS:
					break;
				}
				return true;
			}
			ScriptingSpells.warn(s, "플레이어만 /" + l + " <스킬명> 으로 캐스팅 가능합니다.");
			return false;
		}
		return false;
	}

}
