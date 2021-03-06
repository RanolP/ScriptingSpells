package me.ranol.scriptingspells.export;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import me.ranol.scriptingspells.api.ConfigOption;
import me.ranol.scriptingspells.api.Spell;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.LinkDocument;
import me.ranol.scriptingspells.api.docs.ValueList;

public class DocExporter extends Exporter<Spell> {
	public JsonObject export(Class<? extends Spell> clazz) {
		JsonObject o = new JsonObject();
		ClassDocument doc = clazz.getAnnotation(ClassDocument.class);
		if (doc != null) {
			o.add("doc", new JsonPrimitive(doc.value()));
		}
		JsonArray superClasses = new JsonArray();
		Class<?> sup = clazz;
		while (Spell.class.isAssignableFrom(sup.getSuperclass())) {
			superClasses.add(new JsonPrimitive((sup = sup.getSuperclass()).getName()
				.replace("me.ranol.scriptingspells.spells", "")));
		}
		o.add("super", superClasses);
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			ConfigDocument odoc = f.getAnnotation(ConfigDocument.class);
			ConfigOption opt = f.getAnnotation(ConfigOption.class);
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
				LinkDocument sdoc = f.getAnnotation(LinkDocument.class);
				if (sdoc != null) {
					option.add("spigotDoc", new JsonPrimitive(sdoc.value()));
				}
				o.add(opt == null ? f.getName() : opt.value(), option);
			}
		}
		return o;
	}

	public void exportAt(File saveDir) {
		List<Class<? extends Spell>> spellClasses = loadSpellClasses();
		spellClasses.add(Spell.class);
		JsonObject o = new JsonObject();
		for (Class<? extends Spell> clazz : spellClasses) {
			o.add(clazz.getName()
				.replace("me.ranol.scriptingspells.spells", ""), export(clazz));
		}
		Gson gson = new GsonBuilder().setPrettyPrinting()
			.create();
		String raw = gson.toJson(o);
		try {
			Files.deleteIfExists(saveDir.toPath());
			saveDir.getParentFile()
				.mkdirs();
			Files.write(saveDir.toPath(), raw.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Class<? extends Spell>> loadSpellClasses() {
		List<Class<? extends Spell>> list = new ArrayList<>();
		list.addAll(loadClassByPackage("me.ranol.scriptingspells.spells", Spell.class));
		return list;
	}

}
