package me.ranol.scriptingspells.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TabCompletor {
	public static List<String> complete(String message, List<String> args) {
		return complete(message, args.toArray(new String[args.size()]));
	}

	public static List<String> complete(String message, String... args) {
		List<String> result = new ArrayList<>();
		for (String arg : args) {
			if (arg.toLowerCase()
				.startsWith(message.toLowerCase())) result.add(arg);
		}
		return result;
	}

	public static void addPlayers(List<String> addto, boolean offline) {
		if (offline) for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (player.getName() != null && !addto.contains(player.getName())) addto.add(player.getName());
		}
		else for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName() != null && !addto.contains(player.getName())) addto.add(player.getName());
		}
	}

	public static String getArgs(String[] args, int start) {
		String s = "";
		for (; start < args.length; start++) {
			s += args[start] + " ";
		}
		return s.trim();
	}
}
