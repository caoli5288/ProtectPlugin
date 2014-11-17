package com.mengcraft.server.protect;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.mengcraft.server.Protect;

public class Events implements Listener, Runnable {
	private final Set<String> reqeustNames;
	private final Set<InetAddress> requestIps;

	public Events() {
		this.requestIps = new HashSet<InetAddress>();
		this.reqeustNames = new HashSet<String>();
	}

	@Override
	public void run() {
		getRequestIps().clear();
		getRequestName().clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult().equals(Result.ALLOWED)) {
			if (getRequestIps().contains(event.getAddress())) {
				event.setLoginResult(Result.KICK_OTHER);
				event.setKickMessage("为防止爆服器蹦服请稍后尝试登陆");
			} else if (getRequestName().size() > 30) {
				event.setLoginResult(Result.KICK_OTHER);
				event.setKickMessage("为防止爆服器蹦服请稍后尝试登陆");
			} else {
				getRequestName().add(event.getName());
				getRequestIps().add(event.getAddress());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		int max = Protect.get().getServer().getMaxPlayers();
		Player[] online = Protect.get().getServer().getOnlinePlayers();
		if (online.length > max) {
			randomKick(online);
		}
	}

	private void randomKick(Player[] online) {
		int i = new Random().nextInt(online.length);
		Player kicked = online[i].hasPermission("essentials.joinfullserver") ? null : online[i];
		if (kicked != null) {
			kicked.kickPlayer("服务器人已经满了, 你被挤下线了!");
		} else {
			randomKick(online);
		}
	}

	public Set<String> getRequestName() {
		return reqeustNames;
	}

	public Set<InetAddress> getRequestIps() {
		return requestIps;
	}
}
