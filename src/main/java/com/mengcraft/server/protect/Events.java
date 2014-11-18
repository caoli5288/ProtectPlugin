package com.mengcraft.server.protect;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.mengcraft.server.Protect;

public class Events implements Listener, Runnable {
	private final Set<String> reqeustNames;
	private final Set<String> requestIps;

	public Events() {
		this.requestIps = new HashSet<String>();
		this.reqeustNames = new HashSet<String>();
		Bukkit.getServer().getPluginManager().registerEvents(this, Protect.get());
	}

	@Override
	public void run() {
		getRequestIps().clear();
		getRequestName().clear();
	}

	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event) {
		if (event.getEntityType() != EntityType.PLAYER && event.getBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		List<Entity> entities = event.getEntity().getNearbyEntities(16, 16, 16);
		int count = 0;
		for (Entity entity : entities) {
			if (entity.getType().equals(event.getEntity().getType())) {
				count = count + 1;
			}
		}
		if (count > 16) {
			event.setCancelled(true);
			// System.out.println("Events.OnCreatureSpawn.Cancelled");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
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
				int max = Bukkit.getServer().getMaxPlayers();
				Player[] online = Bukkit.getServer().getOnlinePlayers();
				if (online.length > max) {
					randomKick(online);
				}
			}
		}
	}

	private void randomKick(Player[] online) {
		int i = new Random().nextInt(online.length);
		Player kicked = online[i].hasPermission("essentials.joinfullserver") ? null : online[i];
		if (kicked != null) {
			kicked.kickPlayer("服务器人已经满你被挤下线了");
		} else {
			randomKick(online);
		}
	}

	public Set<String> getRequestName() {
		return reqeustNames;
	}

	public Set<String> getRequestIps() {
		return requestIps;
	}
}
