package com.mengcraft.protect.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class AntiJoinBot implements Listener, Runnable {
	private final Set<String> reqeustNames = new HashSet<>();
	private final Set<String> requestIps = new HashSet<>();

	@Override
	public void run() {
		getRequestIps().clear();
		getRequestName().clear();
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		// System.out.println("Events.OnLogin.New."+event.getAddress().getHostAddress());
		if (event.getResult().equals(Result.ALLOWED)) {
			if (getRequestIps().contains(event.getAddress().getHostAddress())) {
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("防止爆服器蹦服请稍后尝试登陆");
			} else if (getRequestName().size() > 30) {
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("防止爆服器蹦服请稍后尝试登陆");
			} else {
				getRequestName().add(event.getPlayer().getName());
				getRequestIps().add(event.getAddress().getHostAddress());
			}
		}
	}

	public Set<String> getRequestName() {
		return reqeustNames;
	}

	public Set<String> getRequestIps() {
		return requestIps;
	}
}
