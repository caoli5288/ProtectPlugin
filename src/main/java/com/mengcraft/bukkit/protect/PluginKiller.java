package com.mengcraft.bukkit.protect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginKiller {
	private final static PluginKiller KILLER = new PluginKiller();
	private final Events events = new Events();
	private final List<String> names = new ArrayList<>();
	private final List<Plugin> markedPlugins = new ArrayList<>();
	
	private PluginKiller(){
	}

	public static PluginKiller getKiller() {
		return KILLER;
	}

	public Events getEvents() {
		return events;
	}

	public void addName(String name) {
		getNames().add(name);
	}

	public void runPluginKiller() {
		for (String name : this.getNames()) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
			if (plugin != null && plugin.isEnabled()) {
				this.markedPlugins.add(plugin);
				Bukkit.getPluginManager().disablePlugin(plugin);
			}
			if (plugin != null) {
				this.markedPlugins.add(plugin);
				Bukkit.getLogger().warning("已禁用功能重复的[" + plugin.getName() + "]插件");
			}
		}
		unloadPlugins();
	}

	private List<String> getNames() {
		return names;
	}

	private void unloadPlugins() {
		List<Plugin> plugins = new ArrayList<>(Arrays.asList(Bukkit.getPluginManager().getPlugins()));
		plugins.removeAll(this.markedPlugins);
		Map<String, Plugin> lookupNames = new HashMap<>();
		for (Plugin plugin : plugins) {
			lookupNames.put(plugin.getName(), plugin);
		}
		try {
			Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
			Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
			pluginsField.setAccessible(true);
			lookupNamesField.setAccessible(true);
			pluginsField.set(Bukkit.getPluginManager(), plugins);
			lookupNamesField.set(Bukkit.getPluginManager(), lookupNames);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private class Events implements Listener {
		@EventHandler
		public void onPluginEnable(PluginEnableEvent event) {
			if (PluginKiller.getKiller().getNames().contains(event.getPlugin().getName())) {
				event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
			}
		}
	}
}
