package com.mengcraft.bukkit.protect.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerRecordManager {
	private final static long TIME_MOON_LONG = 2592000000L;

	private final static PlayerRecordManager MANAGER = new PlayerRecordManager();
	private final Map<String, Record> map = new HashMap<>();
	private final Events events = new Events();

	public PlayerRecordManager() {
		File file = new File("cached-player-record.dat");
		if (file.exists()) {
			init(file);
		}
	}

	public Record getRecord(String name) {
		return this.map.get(name);
	}

	public void createRecord(Player player) {
		this.map.put(player.getName(), new Record(player.getName(), player.getAddress().getAddress().getHostAddress()));
	}

	public void save() {
		try {
			FileWriter out = new FileWriter(new File("cached-player-record.txt"));
			BufferedWriter writer = new BufferedWriter(out);
			List<Record> n = new ArrayList<>(this.map.values());
			for (int i = 0; i < n.size(); i = i + 1) {
				if (i > 0) {
					writer.newLine();
				}
				writer.write(n.get(i).getName() + "|" + n.get(i).getAddr() + "|" + n.get(i).getLast());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init(File file) {
		try {
			List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
			for (String string : lines) {
				initRecord(string.split("\\|"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initRecord(String[] data) {
		if (new Long(data[2]) + TIME_MOON_LONG > System.currentTimeMillis()) {
			this.map.put(data[0], new Record(data[0], data[1]));
		}
	}

	public static PlayerRecordManager getManager() {
		return MANAGER;
	}

	public Events getEvents() {
		return events;
	}

	private class Events implements Listener {
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			createRecord(event.getPlayer());
		}
	}

	private class Record {
		private final String name;
		private final String addr;
		private final long last;

		public Record(String name, String addr) {
			this.name = name;
			this.last = System.currentTimeMillis();
			this.addr = addr;
		}

		public long getLast() {
			return this.last;
		}

		public String getAddr() {
			return this.addr;
		}

		public String getName() {
			return name;
		}
	}
}
