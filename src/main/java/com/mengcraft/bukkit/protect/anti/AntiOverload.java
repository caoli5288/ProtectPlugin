package com.mengcraft.bukkit.protect.anti;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class AntiOverload implements Listener {

	private final List<?> handle;

	public AntiOverload() {
		Object playerList = getPlayerListField();
		handle = getHandle(playerList);
	}

	private List<?> getHandle(Object playerList) {
		Class<?> c = playerList.getClass().getSuperclass();
		Field field = getField(c, "players");
		Object object = null;
		try {
			object = field.get(playerList);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return (List<?>) object;
	}

	private Object getPlayerListField() {
		Class<?> server = Bukkit.getServer().getClass();
		Field field = getField(server, "playerList");
		Object object = null;
		try {
			object = field.get(Bukkit.getServer());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	private Field getField(Class<?> server, String string) {
		Field field = null;
		try {
			field = server.getDeclaredField(string);
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult().equals(Result.ALLOWED)) {
			int max = Bukkit.getMaxPlayers();
			while (this.handle.size() > max) {
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
