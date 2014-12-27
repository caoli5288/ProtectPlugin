package com.mengcraft.bukkit.protect.anti;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class AntiOverload implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult().equals(Result.ALLOWED)) {
			int max = Bukkit.getMaxPlayers();
			while (Bukkit.getOnlinePlayers().length > max) {
				randomKick(Bukkit.getOnlinePlayers());
			}
		}
	}

	private void randomKick(Player[] online) {
		int i = new Random().nextInt(online.length);
		Player kicked = online[i].hasPermission("essentials.joinfullserver") ? null : online[i];
		if (kicked != null) {
			kicked.kickPlayer("服务器人已经满你被挤下线了");
		} else {
			online[0].kickPlayer("服务器人已经满你被挤下线了");
		}
	}
}
