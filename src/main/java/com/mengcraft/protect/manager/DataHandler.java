package com.mengcraft.protect.manager;


public class DataHandler {
	private final static DataHandler HANDLER = new DataHandler();
	
	public static DataHandler getHandler() {
		return HANDLER;
	}

}
