package com.mengcraft.protect.listener;

import java.net.InetAddress;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.mengcraft.protect.manager.SegmentManager;
import com.mengcraft.protect.util.StringMap;

public class PlayerLoginHandler implements Listener {

	private final SegmentManager segment = SegmentManager.getManager();
	private final StringMap<Long> addrLast = new StringMap<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handler(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult().equals(Result.ALLOWED)) {
			work(event);
		}
	}

	private void work(AsyncPlayerPreLoginEvent event) {
		InetAddress addr = event.getAddress();
		if (this.segment.contains(addr)) {
			event.setLoginResult(Result.KICK_BANNED);
			event.setKickMessage("您的IP已被服务器封禁");
		} else if (checkAddr(addr.getHostAddress())) {
			event.setLoginResult(Result.KICK_OTHER);
			event.setKickMessage("请不要频繁登陆服务器");
		} else {
			this.addrLast.put(addr.getHostAddress(), System.currentTimeMillis());
		}
	}

	private boolean checkAddr(String host) {
		if (this.addrLast.containsKey(host)) {
			return this.addrLast.get(host) + 4000 > System.currentTimeMillis();
		}
		return false;
	}

}
