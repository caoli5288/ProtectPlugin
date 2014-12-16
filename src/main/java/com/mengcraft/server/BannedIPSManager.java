package com.mengcraft.server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannedIPSManager {
	private final static BannedIPSManager MANAGER = new BannedIPSManager();

	private BannedIPSManager() {
		File file = new File("banned-ip-segments.txt");
		if (file.exists()) {
			initFile(file);
		} else {
			createNewFile(file);
			initFile(file);
		}
	}

	public void createNew(String addr, int limit, long untill, String message) {
		// TODO
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
			parseLines(line);
		}
	}

	private void parseLines(String line) {
		// TODO
	}

	private List<String> readLines(File file) {
		try {
			return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public class BannedIPSegment {
		// TODO
	}
}
