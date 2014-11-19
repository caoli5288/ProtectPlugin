package com.mengcraft.server.protect;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ModifySpigot implements Runnable {
	private final int value;

	public ModifySpigot(int i) {
		// TODO Auto-generated constructor stub
		this.value = i;
	}

	@Override
	public void run() {
		File file = new File("spigot.yml");
		if (file.exists()) {
			FileConfiguration spigot = YamlConfiguration.loadConfiguration(file);
			spigot.set("world-settings.default.entity-activation-range.animals", getValue() * 3);
			spigot.set("world-settings.default.entity-activation-range.monsters", getValue() * 6);
			spigot.set("world-settings.default.entity-activation-range.misc", getValue());
			try {
				spigot.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getValue() {
		return value;
	}
}
