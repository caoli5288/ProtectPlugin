package com.mengcraft.protect.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.mengcraft.protect.util.StringMap;

public class PlayerManager {

	private final static PlayerManager MANAGER = new PlayerManager();

	private final Events events = new Events();

	private final File file = new File("cached-player-record.txt");

	private final StringMap<String> playerAddress = new StringMap<>();
	private final StringMap<Long> playerJoinTime = new StringMap<>();

	private PlayerManager() {
		if (this.file.isFile()) {
			readFile();
		}
	}

	public String getAddress(String name) {
		return this.playerAddress.get(name);
	}

	public boolean hasPlayer(String name) {
		return this.playerAddress.containsKey(name);
	}

	public long getLastLogin(String name) {
		return this.playerJoinTime.get(name);
	}

	public void saveRecords() {
		try {
			FileWriter writer = new FileWriter(this.file);
			List<String> names = new ArrayList<>(this.playerAddress.keySet());
			loopWrite(writer, names);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateRecord(String name, String address, long time) {
		this.playerAddress.put(name, address);
		this.playerJoinTime.put(name, time);
	}

	private void loopWrite(FileWriter writer, List<String> names) throws IOException {
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			if (i > 0) {
				writer.write("\n");
			}
			String addr = this.playerAddress.get(name);
			String time = this.playerJoinTime.get(name).toString();
			writer.write(name + "|" + addr + "|" + time);
		}
	}

	private void readFile() {
		try {
			Charset charset = Charset.defaultCharset();
			List<String> list = Files.readAllLines(this.file.toPath(), charset);
			parse(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parse(List<String> list) {
		for (String string : list) {
			parse(string.split("\\|"));
		}
	}

	private void parse(String[] split) {
		String name = split[0];
		String address = split[1];
		Long time = new Long(split[2]);
		this.playerAddress.put(name, address);
		this.playerJoinTime.put(name, time);
	}

	private class Events implements Listener {

		private final StringMap<Long> addressTime = new StringMap<>();

		@EventHandler
		public void onJoin(PlayerLoginEvent event) {
			String name = event.getPlayer().getName();
			InetAddress address = event.getAddress();
			String host = address.getHostAddress();
			long time = System.currentTimeMillis();
			if (SegmentManager.getManager().contains(address)) {
				event.setKickMessage("您的IP段已被服务器封禁");
				event.setResult(Result.KICK_BANNED);
			} else if (checkLoginTime(host, time)) {
				event.setKickMessage("请勿频繁尝试登陆服务器");
				event.setResult(Result.KICK_BANNED);
			} else if (checkLastTime(time)) {
				event.setKickMessage("当前登陆高峰请稍后尝试");
				event.setResult(Result.KICK_OTHER);
			} else {
				PlayerManager.this.updateRecord(name, host, time);
			}
			this.addressTime.put(host, time);
		}

		private boolean checkLastTime(long time) {
			if (this.addressTime.containsKey("LAST")) {
				return this.addressTime.get("LAST") + 500 > time;
			}
			return false;
		}

		private boolean checkLoginTime(String address, long time) {
			if (this.addressTime.containsKey(address)) {
				return this.addressTime.get(address) + 4000 > time;
			}
			return false;
		}

	}

	public static PlayerManager getManager() {
		return MANAGER;
	}

	public Events getEvents() {
		return this.events;
	}

}
