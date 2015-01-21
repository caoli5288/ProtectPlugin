package com.mengcraft.protect.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.mengcraft.protect.util.StringMap;

public class PlayerRecordManager {

	private final static PlayerRecordManager MANAGER = new PlayerRecordManager();

	private final File file = new File("cached-player-record.txt");

	private final StringMap<String> playerAddress = new StringMap<>();
	private final StringMap<Long> playerJoinTime = new StringMap<>();

	private PlayerRecordManager() {
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

	public static PlayerRecordManager getManager() {
		return MANAGER;
	}

}
