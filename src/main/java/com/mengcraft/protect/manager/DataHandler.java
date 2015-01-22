package com.mengcraft.protect.manager;

import com.mengcraft.protect.util.StringMap;

public class DataHandler {
	private final static DataHandler HANDLER = new DataHandler();
	
	private final StringMap<Integer> addrCount = new StringMap<>();

	public StringMap<Integer> getAddrCount() {
		return this.addrCount;
	}

	public static DataHandler getHandler() {
		return HANDLER;
	}

}
