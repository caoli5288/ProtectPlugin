package com.mengcraft.bukkit.protect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.mengcraft.bukkit.protect.manager.BannedSegmentManager;
import com.mengcraft.bukkit.protect.manager.TickPerSecondManager;
import com.mengcraft.common.util.OptionParser;
import com.mengcraft.common.util.OptionParser.FilterMode;
import com.mengcraft.common.util.OptionParser.ParsedOption;

public class Commands implements CommandExecutor {
	private final static int INT_MB = 1048576;
	private final static int INT_256_MB = 268435456;
	private long memory = 0;
	private long cpu = 0;
	private final ExecutorService pool = Executors.newCachedThreadPool();

	private String[] getPluginInfo() {
		String[] strings = new String[] {
				ChatColor.GOLD + "/protect entity [world STRING]",
				ChatColor.GOLD + "/protect entity purge <ENTITY_TYPE|all> [world STRING] [rate INT]",
				ChatColor.GOLD + "/protect chunk",
				ChatColor.GOLD + "/protect chunk unload",
				ChatColor.GOLD + "/protect ips",
				ChatColor.GOLD + "/protect ips ban PLAYER [rate INT] [time INT_HOUR]",
				ChatColor.GOLD + "/protect ips unban IP_SEGMENT",
				ChatColor.GOLD + "/protect system",
				ChatColor.GOLD + "/protect system test <mem|cpu>"
		};
		return strings;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(getPluginInfo());
		} else if (args[0].equals("entity")) {
			OptionParser parser = new OptionParser();
			parser.addFilter("purge", FilterMode.WITH_ARGUMENT);
			parser.addFilter("world", FilterMode.WITH_ARGUMENT);
			parser.addFilter("rate", FilterMode.WITH_ARGUMENT);
			ParsedOption option = parser.parse(Arrays.copyOfRange(args, 1, args.length));
			if (option.getSingleList().size() > 0) {
				sender.sendMessage(ChatColor.RED + "不正确的参数");
			} else if (option.has("purge")) {
				int rate = 16;
				if (option.has("rate") && option.isInteger("rate")) {
					rate = option.getInteger("rate");
				} else if (option.has("rate")) {
					rate = -1;
				}
				if (rate < 0) {
					sender.sendMessage(ChatColor.RED + "不正确的限制值");
				} else if (option.has("world") && Bukkit.getWorld(option.getString("world")) != null) {
					sender.sendMessage(purgeEntity(Bukkit.getWorld(option.getString("world")), option.getString("purge"), rate));
				} else if (option.has("world")) {
					sender.sendMessage(ChatColor.RED + "错误的世界名");
				} else {
					sender.sendMessage(purgeEntity(option.getString("purge"), rate));
				}
			} else if (option.has("world") && Bukkit.getWorld(option.getString("world")) != null) {
				sender.sendMessage(getEntityInfo(Bukkit.getWorld(option.getString("world"))));
			} else if (option.has("world")) {
				sender.sendMessage(ChatColor.RED + "错误的世界名");
			} else {
				sender.sendMessage(getEntityInfo());
			}
		} else if (args[0].equals("chunk")) {
			if (args.length < 2) {
				sender.sendMessage(getChunkInfo());
			} else if (args.length < 3) {
				if (args[1].equals("unload")) {
					sender.sendMessage(unloadChunk());
				}
			} else {
				sender.sendMessage(getPluginInfo());
			}
		} else if (args[0].equals("ips")) {
			OptionParser parser = new OptionParser();
			parser.addFilter("ban", FilterMode.WITH_ARGUMENT);
			parser.addFilter("unban", FilterMode.WITH_ARGUMENT);
			parser.addFilter("rate", FilterMode.WITH_ARGUMENT);
			parser.addFilter("time", FilterMode.WITH_ARGUMENT);
			ParsedOption option = parser.parse(Arrays.copyOfRange(args, 1, args.length));
			if (option.getSingleList().size() > 0) {
				sender.sendMessage(ChatColor.RED + "错误的参数");
			} else if (option.has("ban") && option.has("unban")) {
				sender.sendMessage(ChatColor.RED + "错误的参数");
			} else if (option.has("ban") && Bukkit.getPlayerExact(option.getString("ban")) != null) {
				int rate = 2;
				if (option.has("rate") && option.isInteger("rate")) {
					rate = option.getInteger("rate");
				} else if (option.has("rate")) {
					rate = -1;
				}
				int time = 24;
				if (option.has("time") && option.isInteger("time")) {
					time = option.getInteger("time");
				} else if (option.has("time")) {
					time = -1;
				}
				if (rate > 3 || rate < 1) {
					sender.sendMessage(ChatColor.RED + "错误的网段参数");
				} else if (time < 0) {
					sender.sendMessage(ChatColor.RED + "错误的时间参数");
				} else {
					sender.sendMessage(ban(option.getString("ban"), rate, time));
				}
			} else if (option.has("ban")) {
				sender.sendMessage(ChatColor.RED + "玩家不在线或不存在");
			} else if (option.has("unban")) {
				sender.sendMessage(unban(option.getString("unban")));
			} else {
				sender.sendMessage(getBannedSegmentInfo());
			}
		} else if (args[0].equals("system")) {
			OptionParser parser = new OptionParser();
			parser.addFilter("test", FilterMode.WITH_ARGUMENT);
			ParsedOption option = parser.parse(Arrays.copyOfRange(args, 1, args.length));
			if (option.getSingleList().size() > 0) {
				sender.sendMessage(ChatColor.RED + "错误的参数");
			} else if (option.has("test") && option.getString("test").equals("mem")) {
				sender.sendMessage(allocMemory());
			} else if (option.has("test") && option.getString("test").equals("cpu")) {
				sender.sendMessage(testProcessors());
			}
			else {
				sender.sendMessage(getSystemInfo());
			}
		}
		return true;
	}

	private String[] testProcessors() {
		List<String> strings = new ArrayList<>();
		strings.add(ChatColor.RED + "尝试进行CPU测试...");
		if (this.cpu > 0) {
			strings.add(ChatColor.RED + "已经进行过CPU测试");
			return strings.toArray(new String[] {});
		} else if (this.cpu < 0) {
			strings.add(ChatColor.RED + "正在进行CPU测试中");
			return strings.toArray(new String[] {});
		} else setMark(-1);
		int proc = Runtime.getRuntime().availableProcessors();
		int thread = proc > 4 ? 4 : proc;
		CompletionService<Integer> service = new ExecutorCompletionService<>(this.pool);
		Callable<Integer> task = new TestProcessorTask();
		for (int i = 0; i < thread; i++) {
			service.submit(task);
		}
		this.pool.execute(new CheckTestTask(service, thread));
		strings.add(ChatColor.RED + "CPU测试在后台运行");
		strings.add(ChatColor.RED + "请稍后尝试查看结果");
		return strings.toArray(new String[] {});
	}

	private class CheckTestTask implements Runnable {
		private final CompletionService<Integer> service;
		private final int thread;

		@Override
		public void run() {
			int mark = 0;
			for (int i = 0; i < this.thread; i++) {
				try {
					mark += this.service.take().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} finally {
					setMark(mark);
				}
			}
		}

		public CheckTestTask(CompletionService<Integer> service, int thread) {
			this.service = service;
			this.thread = thread;
		}
	}

	private class TestProcessorTask implements Callable<Integer> {
		@Override
		public Integer call() throws Exception {
			int count = 0;
			for (long time = System.currentTimeMillis() + 8000; System.currentTimeMillis() < time; count++) {
				pi();
			}
			return count;
		}

		private void pi() {
			for (double i = 1, pi = 0; i <= 16384; i = i + 1) {
				pi = pi + Math.pow(-1, (i + 1)) * 4 / (2 * i - 1);
			}
		}
	}

	private String[] allocMemory() {
		List<String> strings = new ArrayList<>();
		strings.add(ChatColor.RED + "尝试进行内存测试...");
		if (this.memory > 0) {
			strings.add(ChatColor.RED + "已经进行过内存测试");
		} else if (this.memory < 0) {
			strings.add(ChatColor.RED + "正在进行内存测试中");
		} else {
			this.pool.execute(new AllocMemory());
			setMemory(-1);
			strings.add(ChatColor.RED + "内存测试在后台运行");
			strings.add(ChatColor.RED + "请稍后尝试查看结果");
			strings.add(ChatColor.RED + "直接蹦服属内存虚标");
		}
		return strings.toArray(new String[] {});
	}

	private class AllocMemory implements Runnable {
		@Override
		public void run() {
			Runtime runtime = Runtime.getRuntime();
			long time = System.currentTimeMillis();
			List<byte[]> temp = new ArrayList<>();
			try {
				long j = runtime.maxMemory() - runtime.totalMemory();
				for (int i = 0, count = 0; i < j / INT_256_MB; i = i + 1) {
					if (count > 1) break;
					temp.add(new byte[INT_256_MB]);
					if (System.currentTimeMillis() - time > 256) count++;
					time = System.currentTimeMillis();
				}
			} catch (OutOfMemoryError e) {
			} finally {
				if (runtime.totalMemory() + INT_256_MB > runtime.maxMemory()) {
					setMemory(runtime.maxMemory());
				} else {
					setMemory(runtime.totalMemory());
				}
				temp.clear();
				System.gc();
			}
		}
	}

	private String[] getSystemInfo() {
		Runtime runtime = Runtime.getRuntime();
		List<String> strings = new ArrayList<>();
		long free = runtime.freeMemory();
		long used = runtime.totalMemory() - free;
		strings.add(ChatColor.GOLD + "===== 内存信息 =====");
		strings.add(ChatColor.GOLD + "实际使用: " + used / INT_MB + "MB");
		strings.add(ChatColor.GOLD + "标称可用: " + runtime.maxMemory() / INT_MB + "MB");
		if (this.memory > 0) {
			strings.add(ChatColor.GOLD + "实际可用: " + this.memory / INT_MB + "MB");
		} else {
			strings.add(ChatColor.GOLD + "实际可用: 未测试");
		}
		strings.add(ChatColor.GOLD + "===== CPU信息 =====");
		strings.add(ChatColor.GOLD + "TPS: " + getRecentTickPS());
		if (this.cpu > 0) {
			strings.add(ChatColor.GOLD + "CPU跑分: " + this.cpu);
		} else if (this.cpu < 0) {
			strings.add(ChatColor.GOLD + "CPU跑分: 测试中");
		} else {
			strings.add(ChatColor.GOLD + "CPU跑分: 未测试");
		}
		return strings.toArray(new String[] {});
	}

	private String getRecentTickPS() {
		StringBuilder builder = new StringBuilder();
		List<Double> recent = TickPerSecondManager.getManager().getTps();
		for (int i = 0; i < recent.size(); i++) {
			if (i > 0) {
				builder.append(ChatColor.WHITE);
				builder.append(",");
			}
			double last = recent.get(i);
			if (last > 15) builder.append(ChatColor.GREEN);
			else if (last > 10) builder.append(ChatColor.YELLOW);
			else builder.append(ChatColor.RED);
			builder.append(last);
		}
		return builder.toString();
	}

	private String[] getBannedSegmentInfo() {
		return BannedSegmentManager.getManager().getMessage();
	}

	private String unban(String string) {
		boolean result = BannedSegmentManager.getManager().remove(string);
		if (result) {
			BannedSegmentManager.getManager().saveLines();
			return ChatColor.GOLD + "解除封禁成功";
		}
		return ChatColor.RED + "解除封禁失败";
	}

	private String ban(String name, int rate, int time) {
		long untill = System.currentTimeMillis() + time * 3600000;
		BannedSegmentManager.getManager().createNew(Bukkit.getPlayerExact(name).getAddress().getAddress(), rate, untill);
		BannedSegmentManager.getManager().saveLines();
		filterOnline();
		return ChatColor.GOLD + "封禁IP段" + time + "小时成功";
	}

	private void filterOnline() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			filterPlayer(player);
		}
	}

	private void filterPlayer(Player player) {
		if (BannedSegmentManager.getManager().contains(player.getAddress().getAddress())) {
			player.kickPlayer("你的IP段已被服务器临时封禁");
		}
	}

	private String[] getEntityInfo(World world) {
		List<World> worlds = new ArrayList<>();
		worlds.add(world);
		return getEntityInfo(worlds);
	}

	private String[] getEntityInfo(List<World> worlds) {
		Map<EntityType, Integer> entityMap = new HashMap<>();
		int total = 0;
		List<Entity> entities = new ArrayList<>();
		for (World world : worlds) {
			entities.addAll(world.getEntities());
		}
		for (Entity entity : entities) {
			EntityType type = entity.getType();
			if (entityMap.get(type) != null) {
				int value = entityMap.remove(type);
				entityMap.put(type, value + 1);
			} else {
				entityMap.put(type, 1);
			}
			total = total + 1;
		}
		List<String> messages = new ArrayList<>();
		for (EntityType type : entityMap.keySet()) {
			messages.add(ChatColor.GOLD + type.name() + ": " + entityMap.get(type));
		}
		messages.add(ChatColor.GOLD + "TOTAL: " + total);
		int size = messages.size();
		return messages.toArray(new String[size]);
	}

	private String[] getEntityInfo() {
		return getEntityInfo(Bukkit.getWorlds());
	}

	private String[] getChunkInfo() {
		List<String> messages = new ArrayList<>();
		int total = 0;
		for (World world : Bukkit.getWorlds()) {
			int size = world.getLoadedChunks().length;
			total = total + size;
			messages.add(ChatColor.GOLD + world.getName() + ": " + size);
		}
		messages.add(ChatColor.GOLD + "Total: " + total);
		int size = messages.size();
		return messages.toArray(new String[size]);
	}

	private String unloadChunk() {
		int i = 0;
		int j = 0;
		for (World world : Bukkit.getWorlds()) {
			i = i + world.getLoadedChunks().length;
			for (Chunk chunk : world.getLoadedChunks()) {
				chunk.unload(true, true);
			}
			j = j + world.getLoadedChunks().length;
		}
		i = i - j;
		return new String(ChatColor.GOLD + "Purge chunk number: " + i);
	}

	private String purgeEntity(String typeName, int limit) {
		return purgeEntity(Bukkit.getWorlds(), typeName, limit);
	}

	private String purgeEntity(World world, String typeName, int limit) {
		List<World> worlds = new ArrayList<>();
		worlds.add(world);
		return purgeEntity(worlds, typeName, limit);
	}

	private String purgeEntity(List<World> worlds, String typeName, int limit) {
		if (typeName.equals("PLAYER")) { return new String(ChatColor.GOLD + "You can not purge player entity"); }
		int total = 0;
		List<Entity> entities = new ArrayList<>();
		for (World world : worlds) {
			entities.addAll(world.getEntities());
		}
		for (Entity entity : entities) {
			if (purgeEntityFilter(entity, typeName, limit)) {
				total = total + 1;
				entity.remove();
			}
		}
		return new String(ChatColor.GOLD + "Purge entity " + typeName + " number: " + total);
	}

	private boolean purgeEntityFilter(Entity entity, String type, int limit) {
		if (entity.getType().name().equals("PLAYER")) { return false; }
		if (type.equals("all") || entity.getType().name().equals(type)) {
			int rate = getNearbySameTypeNumber(entity);
			if (rate >= limit) { return true; }
		}
		return false;
	}

	private int getNearbySameTypeNumber(Entity entity) {
		int rate = 0;
		for (Entity near : entity.getNearbyEntities(16, 16, 16)) {
			if (near.getType().equals(entity.getType())) {
				rate = rate + 1;
			}
		}
		return rate;
	}

	public void setMark(long cpu) {
		this.cpu = cpu;
	}

	public void setMemory(long memory) {
		this.memory = memory;
	}
}
