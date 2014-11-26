package com.mengcraft.protect;

import java.io.File;

public class Test {
	public static void main(String[] args) {
		File file = new File(".");
		System.out.println(file.getFreeSpace()/1024/1024/1024);
	}
}
