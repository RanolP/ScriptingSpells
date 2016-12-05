package me.ranol.scriptingspells.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import me.ranol.scriptingspells.api.Spell;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.api.docs.SpigotDoc;
import me.ranol.scriptingspells.api.docs.ValueList;

public class DocExporter {
	public static JsonObject export(Class<? extends Spell> clazz) {
		JsonObject o = new JsonObject();
		SpellDocs doc = clazz.getAnnotation(SpellDocs.class);
		if (doc != null) {
			o.add("doc", new JsonPrimitive(doc.value()));
		}
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			OptionDocs odoc = f.getAnnotation(OptionDocs.class);
			SpellOption opt = f.getAnnotation(SpellOption.class);
			if (odoc != null) {
				// Documented
				JsonObject option = new JsonObject();
				option.add("doc", new JsonPrimitive(odoc.value()));
				ValueList vlist = f.getAnnotation(ValueList.class);
				JsonArray values = new JsonArray();
				if (vlist != null) {
					for (String s : vlist.value()) {
						values.add(new JsonPrimitive(s));
					}
				}
				option.add("values", values);
				SpigotDoc sdoc = f.getAnnotation(SpigotDoc.class);
				if (sdoc != null) {
					option.add("spigotDoc", new JsonPrimitive(sdoc.value()));
				}
				o.add(opt == null ? f.getName() : opt.value(), option);
			}
		}
		return o;
	}

	public static void exportAll(File saveDir) {
		List<Class<? extends Spell>> spellClasses = loadSpellClasses();
		JsonObject o = new JsonObject();
		for (Class<? extends Spell> clazz : spellClasses) {
			o.add(clazz.getName().replace("me.ranol.scriptingspells.spells", ""), export(clazz));
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String raw = gson.toJson(o);
		try {
			Files.deleteIfExists(saveDir.toPath());
			saveDir.getParentFile().mkdirs();
			Files.write(saveDir.toPath(), raw.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Class<? extends Spell>> loadSpellClasses() {
		List<Class<? extends Spell>> list = new ArrayList<>();
		list.addAll(loadClassByPackage("me.ranol.scriptingspells.spells", Spell.class));
		return list;
	}

	private static <T> List<Class<T>> loadClassByPackage(String pack, Class<T> instance) {
		List<Class<T>> result = new ArrayList<>();
		URL url = DocExporter.class.getProtectionDomain().getCodeSource().getLocation();
		String real;
		try {
			real = new URI(url.toString()).getPath();
		} catch (Exception e) {
			real = url.getFile();
		}
		try (ZipInputStream jarStream = new ZipInputStream(new FileInputStream(real))) {
			for (ZipEntry item = jarStream.getNextEntry(); item != null; item = jarStream.getNextEntry()) {
				if (item.isDirectory())
					continue;
				String className = item.getName().replace('/', '.');
				className = className.substring(0, className.length() - ".class".length());
				try {
					if (className.indexOf('$') != -1) {
						continue;
					}
					Class<?> c = Class.forName(className);
					if (!c.getPackage().getName().startsWith(pack))
						continue;
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
