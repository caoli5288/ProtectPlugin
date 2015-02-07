package com.mengcraft.protect.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CheckUpdate implements Runnable {

	final private InputStream resource;
	final private File file;

	public CheckUpdate(File file, InputStream resource) {
		this.file = file;
		this.resource = resource;
	}

	@Override
	public void run() {
		try {
			InputStreamReader reader = new InputStreamReader(this.resource, "UTF-8");
			char[] buffer = new char[256];
			reader.read(buffer);
			String local = new String(buffer);
			// This file created by jenkins.
			URL url = new URL("http://ci.mengcraft.com:8080/job/ProtectPlugin/ws/src/main/resources/checkuuid");
			new InputStreamReader(url.openStream(), "UTF-8").read(buffer);
			String remote = new String(buffer);
			checkUpdate(local, remote);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkUpdate(String local, String remote) {
		if (local.hashCode() != remote.hashCode()) {
			update();
		}
	}

	private void update() {
		try {
			URL url = new URL("http://ci.mengcraft.com:8080/job/ProtectPlugin/lastSuccessfulBuild/artifact/target/protect-0.3.0-SNAPSHOT.jar");
			Files.copy(url.openStream(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("[ProtectPlugin] Upgrade done!");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
