package me.ranol.scriptingspells.api.defaultparser;

import java.util.List;

import me.ranol.scriptingspells.api.CastItem;
import me.ranol.scriptingspells.api.item.ItemManagement;
import me.ranol.scriptingspells.api.parser.StringListParser;

public class CastItemParser extends StringListParser<CastItem[]> {

	@Override
	public CastItem[] parse(List<String> object) {
		CastItem[] result = new CastItem[object.size()];
		int index = 0;
		for (String s : object) {
			result[index++] = ItemManagement.parseItem(s);
		}
		return result;
	}

	@Override
	public String options() {
		return "등록된 아이템들";
	}

}
