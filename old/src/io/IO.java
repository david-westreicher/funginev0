package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import settings.Settings;

public class IO {

	public static BufferedReader read(String file) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(Settings.RESSOURCE_FOLDER
					+ file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}

	public static String readToString(String file) {
		BufferedReader br = read(file);
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
