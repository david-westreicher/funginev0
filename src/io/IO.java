package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import settings.Settings;
import util.Log;

public class IO {

	public static BufferedReader read(String file) {
		BufferedReader in = null;
		file = Settings.RESSOURCE_FOLDER + file;
		try {
			in = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			Log.err(IO.class, "couldn't find: " + file);
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
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static <T> T[] readFromJson(String file, Type t) {
		BufferedReader br = read(file);
		if (br == null)
			return null;
		T result[] = new Gson().fromJson(br, t);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static <T> void writeToJson(String file, T[] players) {
		String json = new GsonBuilder().setPrettyPrinting().create()
				.toJson(players);
		write(file, json);
	}

	private static void write(String file, String json) {
		file = Settings.RESSOURCE_FOLDER + file;
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(json);
			out.close();
		} catch (IOException e) {
			Log.err(IO.class, "couldn't write to file " + file);
		}
	}
}
