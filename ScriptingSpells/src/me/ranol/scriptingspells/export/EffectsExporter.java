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
import me.ranol.scriptingspells.api.docs.ClassDocument;
import me.ranol.scriptingspells.api.docs.ConfigDocument;
import me.ranol.scriptingspells.api.docs.LinkDocument;
import me.ranol.scriptingspells.api.docs.ValueList;
import me.ranol.scriptingspells.api.effects.SpellEffect;

public class EffectsExporter extends Exporter<SpellEffect> {

	@Override
	public JsonObject export(Class<? extends SpellEffect> clazz) {
		JsonObject o = new JsonObject();
		ClassDocument doc = clazz.getAnnotation(ClassDocument.class);
		if (doc != null) {
			o.add("doc", new JsonPrimitive(doc.value()));
		}
		JsonArray superClasses = new JsonArray();
		Class<?> sup = clazz;
		while (SpellEffect.class.isAssignableFrom(sup.getSuperclass())) {
			superClasses.add(
					new JsonPrimitive(
							SpellEffect.nameByClass((Class<? extends SpellEffect>) (sup = sup.getSuperclass()))));
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
					option.add("linkDocs", new JsonPrimitive(sdoc.value()));
				}
				o.add(opt == null ? f.getName() : opt.value(), option);
			}
		}
		return o;
	}

	@Override
	public void exportAt(File f) {
		List<Class<? extends SpellEffect>> spellClasses = loadEffectClasses();
		spellClasses.add(SpellEffect.class);
		JsonObject o = new JsonObject();
		for (Class<? extends SpellEffect> clazz : spellClasses) {
			o.add(SpellEffect.nameByClass(clazz), export(clazz));
		}
		Gson gson = new GsonBuilder().setPrettyPrinting()
			.create();
		String raw = gson.toJson(o);
		try {
			Files.deleteIfExists(f.toPath());
			f.getParentFile()
				.mkdirs();
			Files.write(f.toPath(), raw.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Class<? extends SpellEffect>> loadEffectClasses() {
		List<Class<? extends SpellEffect>> list = new ArrayList<>();
		list.addAll(loadClassByPackage("me.ranol.scriptingspells.api.effects", SpellEffect.class));
		return list;
	}

}
