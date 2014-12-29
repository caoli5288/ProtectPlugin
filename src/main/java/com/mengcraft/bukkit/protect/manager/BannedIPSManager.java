package com.mengcraft.bukkit.protect.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class BannedIPSManager {
	private final static BannedIPSManager MANAGER = new BannedIPSManager();
	private final Events events = new Events();
	private final Map<String, Segment> segments = new HashMap<>();

	private BannedIPSManager() {
		File file = new File("banned-ip-segments.txt");
		if (file.exists()) {
			initFile(file);
		} else {
			createNewFile(file);
			initFile(file);
		}
	}

	public static BannedIPSManager getManager() {
		return MANAGER;
	}

	public Events getEvents() {
		return events;
	}

	public String[] getMessage() {
		List<String> strings = new ArrayList<>();
		for (Segment segment : this.segments.values()) {
			strings.add(segment.toString());
		}
		if (strings.size() < 1) {
			strings.add("N/A");
		}
		return strings.toArray(new String[strings.size()]);
	}

	public void createRecord(String addr, int limit, long until) {
		this.segments.put(addr, new Segment(addr, limit, until));
	}

	public boolean remove(String string) {
		return this.segments.remove(string) != null;
	}

	public void saveLines() {
		try {
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean contains(InetAddress addr) {
		for (Segment segment : this.segments.values()) {
			if (segment.contains(addr)) { return true; }
		}
		return false;
	}

	private void save() throws Exception {
		File file = new File("banned-ip-segments.txt");
		FileWriter out = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(out);
		for (Segment segment : this.segments.values()) {
			writer.write(segment.toString());
		}
		writer.close();
	}

	private void createNewFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initFile(File file) {
		List<String> lines = readLines(file);
		parseLines(lines);
	}

	private void parseLines(List<String> lines) {
		for (String line : lines) {
			parseLine(line);
		}
	}

	private void parseLine(String line) {
		String[] split = line.split("\\|");
		if (new Long(split[2]) < System.currentTimeMillis()) { return; }
		try {
			createRecord(split[0], new Integer(split[1]), new Long(split[2]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private List<String> readLines(File file) {
		try {
			return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private class Events implements Listener {
		@EventHandler
		public void onLogin(PlayerLoginEvent event) {
			if (BannedIPSManager.getManager().contains(event.getAddress())) {
				event.setResult(Result.KICK_BANNED);
				event.setKickMessage("你的IP段已被服务器临时封禁");
			}
		}
	}

	private byte[] getAddrByte(String addr, int limit) {
		String[] segment = addr.split("\\.");
		byte[] bs = new byte[limit];
		for (int i = 0; i < bs.length; i++) {
			bs[i] = new Integer(segment[i]).byteValue();
		}
		return bs;
	}

	private class Segment {
		private final String host;
		private int limit;
		private final byte[] segment;
		private final long until;

		public Segment(String addr, int limit, long until) {
			this.host = addr;
			this.limit = limit;
			// this.segment = Arrays.copyOf(addr.getAddress(), limit);
			this.segment = getAddrByte(addr, limit);
			this.until = until;
		}

		public boolean contains(InetAddress key) {
			if (System.currentTimeMillis() > this.until) {
				return false;
			} else if (Arrays.equals(this.segment, Arrays.copyOf(key.getAddress(), this.limit))) {
				// This one ohhh
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return new StringBuilder().append(this.host).append("|").append(this.limit).append("|").append(this.until).toString();
		}
	}
}
