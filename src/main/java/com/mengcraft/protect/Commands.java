package com.mengcraft.protect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mengcraft.common.util.OptionParser;
import com.mengcraft.common.util.OptionParser.FilterMode;
import com.mengcraft.common.util.OptionParser.ParsedOption;
import com.mengcraft.protect.manager.EntityManager;
import com.mengcraft.protect.manager.PlayerRecordManager;
import com.mengcraft.protect.manager.SegmentManager;
import com.mengcraft.protect.manager.TickManager;
import com.mengcraft.protect.util.TimeUtils;

public class Commands implements CommandExecutor {

	private final ProtectPlugin plugin;

	private final static int SIZE_MB_INT = 1048576;
	private final static byte BYTE_ZERO = 0;
	private long memory = 0L;
	private long cpu = 0L;
	private final ExecutorService pool = Executors.newCachedThreadPool();

	public Commands(ProtectPlugin protect) {
		this.plugin = protect;
	}

	private String[] getPluginInfo() {
		String[] strings = new String[] {
				ChatColor.GOLD + "/protect entity [world STRING]",
				ChatColor.GOLD + "/protect entity purge <TYPE|all> [world STR] [rate INT]",
				ChatColor.GOLD + "/protect chunk",
				ChatColor.GOLD + "/protect chunk unload",
				ChatColor.GOLD + "/protect ips",
				ChatColor.GOLD + "/protect ips ban PLAYER [rate INT] [time INT_DAY]",
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
				if (rate < -1) {
					sender.sendMessage(ChatColor.RED + "不正确的限制值");
				} else if (option.has("world") && Bukkit.getWorld(option.getString("world")) != null) {
					sender.sendMessage(purgeEntity(Bukkit.getWorld(option.getString("world")), option.getString("purge"), rate));
				} else if (option.has("world")) {
					sender.sendMessage(ChatColor.RED + "错误的世界名");
				} else {
					sender.sendMessage(purgeEntity(option.getString("purge"), rate));
				}
			} else if (option.has("world") && Bukkit.getWorld(option.getString("world")) != null) {
				sender.sendMessage(getEntityInfo(this.plugin.getServer().getWorld(option.getString("world"))));
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
			} else if (option.has("ban") && PlayerRecordManager.getManager().hasPlayer(option.getString("ban"))) {
				int rate = 2;
				if (option.has("rate") && option.isInteger("rate")) {
					rate = option.getInteger("rate");
				} else if (option.has("rate")) {
					rate = -1;
				}
				int time = 7;
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
				sender.sendMessage(ChatColor.RED + "玩家不存在");
			} else if (option.has("unban")) {
				sender.sendMessage(unban(option.getString("unban")));
			} else {
				sender.sendMessage(getBannedInfo());
			}
		} else if (args[0].equals("system")) {
			OptionParser parser = new OptionParser();
			parser.addFilter("test", FilterMode.WITH_ARGUMENT);
			ParsedOption option = parser.parse(Arrays.copyOfRange(args, 1, args.length));
			if (option.getSingleList().size() > 0) {
				sender.sendMessage(ChatColor.RED + "错误的参数");
			} else if (option.has("test") && option.getString("test").equals("mem")) {
				sender.sendMessage(testMemory());
			} else if (option.has("test") && option.getString("test").equals("cpu")) {
				sender.sendMessage(testProcessors());
			}
			else {
				sender.sendMessage(getSystemInfo());
			}
		}
		return true;
	}

	private String[] getEntityInfo() {
		List<World> worlds = this.plugin.getServer().getWorlds();
		return getEntityInfo(EntityManager.getManager().getEntityInfo(worlds));
	}

	private String[] getEntityInfo(World world) {
		return getEntityInfo(EntityManager.getManager().getEntityInfo(world));
	}

	private String[] getEntityInfo(Map<String, Integer> map) {
		List<String> list = new ArrayList<>();
		int count = 0;
		for (Entry<String, Integer> entry : map.entrySet()) {
			list.add(ChatColor.GOLD + entry.getKey() + ": " + entry.getValue());
			count += entry.getValue();
		}
		list.add(ChatColor.RED + "TATOL: " + count);
		return list.toArray(new String[] {});
	}

	private String purgeEntity(String type, int rate) {
		List<World> worlds = this.plugin.getServer().getWorlds();
		int count = EntityManager.getManager().purgeEntity(worlds, type.toUpperCase(), rate);
		return ChatColor.GOLD + "Purge done: " + count;
	}

	private String purgeEntity(World world, String type, int rate) {
		int count = EntityManager.getManager().purgeEntity(world, type.toUpperCase(), rate);
		return ChatColor.GOLD + "Purge done: " + count;
	}

	private String[] testMemory() {
		List<String> strings = new ArrayList<>();
		strings.add(ChatColor.RED + "尝试进行内存测试...");
		if (this.cpu < 0) {
			strings.add(ChatColor.RED + "请等待CPU测试完毕");
		} else if (this.memory > 0) {
			strings.add(ChatColor.RED + "已经进行过内存测试");
		} else if (this.memory < 0) {
			strings.add(ChatColor.RED + "正在进行内存测试中");
		} else {
			this.memory = -1;
			this.pool.execute(new TestMemoryTask());
			strings.add(ChatColor.RED + "内存测试在后台运行");
			strings.add(ChatColor.RED + "请稍后尝试查看结果");
		}
		return strings.toArray(new String[] {});
	}

	private class TestMemoryTask implements Runnable {
		@Override
		public void run() {
			int count = 0;
			for (long time = System.currentTimeMillis() + 16000; System.currentTimeMillis() < time; count++) {
				act();
			}
			setMemory(count / 8);
		}

		private void act() {
			Arrays.fill(new byte[SIZE_MB_INT], BYTE_ZERO);
		}
	}

	private String[] testProcessors() {
		List<String> strings = new ArrayList<>();
		strings.add(ChatColor.RED + "尝试进行CPU测试...");
		if (this.memory < 0) {
			strings.add(ChatColor.RED + "请等待内存测试完毕");
		} else if (this.cpu > 0) {
			strings.add(ChatColor.RED + "已经进行过CPU测试");
			return strings.toArray(new String[] {});
		} else if (this.cpu < 0) {
			strings.add(ChatColor.RED + "正在进行CPU测试中");
			return strings.toArray(new String[] {});
		} else {
			this.cpu = -1;
			this.pool.execute(new TestProcessorTask());
			this.pool.execute(new TestProcessorTask());
			strings.add(ChatColor.RED + "CPU测试在后台运行");
			strings.add(ChatColor.RED + "请稍后尝试查看结果");
		}
		return strings.toArray(new String[] {});
	}

	private class TestProcessorTask implements Runnable {
		@Override
		public void run() {
			int count = 0;
			for (long time = System.currentTimeMillis() + 16000; System.currentTimeMillis() < time; count++) {
				pi();
			}
			setProcessor(count);
		}

		private void pi() {
			for (double i = 1, pi = 0; i <= 16384; i = i + 1) {
				pi = pi + Math.pow(-1, (i + 1)) * 4 / (2 * i - 1);
			}
		}
	}

	private String[] getSystemInfo() {
		Runtime runtime = Runtime.getRuntime();
		List<String> strings = new ArrayList<>();
		long free = runtime.freeMemory();
		long used = runtime.totalMemory() - free;
		strings.add(ChatColor.GOLD + "===== 内存信息 =====");
		strings.add(ChatColor.GOLD + "已用内存: " + used / SIZE_MB_INT + "MB");
		strings.add(ChatColor.GOLD + "最大内存: " + runtime.maxMemory() / SIZE_MB_INT + "MB");
		if (this.memory > 0) {
			strings.add(ChatColor.GOLD + "内存跑分: " + this.memory);
		} else if (this.memory < 0) {
			strings.add(ChatColor.GOLD + "内存跑分: 测试中");
		} else {
			strings.add(ChatColor.GOLD + "内存跑分: 未测试");
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
		File file = new File(".");
		strings.add(ChatColor.GOLD + "===== 硬盘信息 =====");
		strings.add(ChatColor.GOLD + "空闲空间: " + file.getFreeSpace() / SIZE_MB_INT + "MB");
		strings.add(ChatColor.GOLD + "硬盘跑分: 待更新");
		return strings.toArray(new String[] {});
	}

	private String getRecentTickPS() {
		StringBuilder builder = new StringBuilder();
		List<Double> recent = TickManager.getManager().getTps();
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

	private String[] getBannedInfo() {
		return SegmentManager.getManager().getMessage();
	}

	private String unban(String string) {
		boolean result = SegmentManager.getManager().remove(string);
		if (result) {
			SegmentManager.getManager().saveLines();
			return ChatColor.GOLD + "解除封禁成功";
		}
		return ChatColor.RED + "解除封禁失败";
	}

	private String ban(String name, int rate, int time) {
		long until = System.currentTimeMillis() + time * TimeUtils.TIME_DAY;
		String addr = PlayerRecordManager.getManager().getAddress(name);
		SegmentManager.getManager().createRecord(addr, rate, until);
		SegmentManager.getManager().saveLines();
		filterOnline();
		return ChatColor.GOLD + "封禁IP段" + time + "天成功";
	}

	private void filterOnline() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			filterPlayer(player);
		}
	}

	private void filterPlayer(Player player) {
		if (SegmentManager.getManager().contains(player.getAddress().getAddress())) {
			player.kickPlayer("你的IP段已被服务器临时封禁");
		}
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

	private synchronized void setProcessor(long cpu) {
		this.cpu += cpu;
	}

	private void setMemory(long memory) {
		this.memory = memory;
	}
}
