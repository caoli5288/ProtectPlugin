package com.mengcraft.protect.listener;

import java.net.InetAddress;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

import com.mengcraft.protect.manager.DataHandler;
import com.mengcraft.protect.manager.SegmentManager;
import com.mengcraft.protect.util.StringMap;

public class PlayerLoginHandler implements Listener {

	private final SegmentManager segment = SegmentManager.getManager();
	private final StringMap<Long> addrLast = new StringMap<>();

	private final StringMap<Integer> addrCount = DataHandler.getHandler().getAddrCount();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void login(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult().equals(Result.ALLOWED)) {
			work(event);
		}
	}

	@EventHandler
	public void ping(ServerListPingEvent event) {
		checkIncreases(event.getAddress());
	}

	private void work(AsyncPlayerPreLoginEvent event) {
		InetAddress addr = event.getAddress();
		checkIncreases(addr);
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

	private void checkIncreases(InetAddress addr) {
		String host = addr.getHostAddress();
		Integer i = this.addrCount.remove(host);
		if (i != null) {
			this.addrCount.put(host, i + 1);
		} else {
			this.addrCount.put(host, 1);
		}
	}

	private boolean checkAddr(String host) {
		if (this.addrLast.containsKey(host)) {
			return this.addrLast.get(host) + 4000 > System.currentTimeMillis();
		}
		return false;
	}

}
