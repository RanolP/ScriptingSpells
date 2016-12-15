package me.ranol.scriptingspells.export;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonObject;

public abstract class Exporter<T> {
	public abstract JsonObject export(Class<? extends T> clazz);

	public abstract void exportAt(File f);

	public void exportAt(String dir) {
		exportAt(new File(dir));
	}

	public void exportAt(File sup, String dir) {
		exportAt(new File(sup, dir));
	}

	protected static <T> List<Class<T>> loadClassByPackage(String pack, Class<T> instance) {
		List<Class<T>> result = new ArrayList<>();
		URL url = DocExporter.class.getProtectionDomain()
			.getCodeSource()
			.getLocation();
		String real;
		try {
			real = new URI(url.toString()).getPath();
		} catch (Exception e) {
			real = url.getFile();
		}
		try (ZipInputStream jarStream = new ZipInputStream(new FileInputStream(real))) {
			for (ZipEntry item = jarStream.getNextEntry(); item != null; item = jarStream.getNextEntry()) {
				if (item.isDirectory()) continue;
				String className = item.getName()
					.replace('/', '.');
				className = className.substring(0, className.length() - ".class".length());
				try {
					if (className.indexOf('$') != -1) {
						continue;
					}
					Class<?> c = Class.forName(className);
					if (!c.getPackage()
						.getName()
						.startsWith(pack)) continue;
					if (instance.isAssignableFrom(c)) {
						result.add((Class<T>) c);
					}
				} catch (ClassNotFoundException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
