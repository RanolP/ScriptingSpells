package me.ranol.scriptingspells.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

import me.ranol.scriptingspells.api.SpellCastState;
import me.ranol.scriptingspells.api.SpellOption;
import me.ranol.scriptingspells.api.SpellParser;
import me.ranol.scriptingspells.api.TargetFilter;
import me.ranol.scriptingspells.api.defaultparser.TargetParser;
import me.ranol.scriptingspells.api.docs.OptionDocs;
import me.ranol.scriptingspells.api.docs.SpellDocs;
import me.ranol.scriptingspells.api.docs.SpigotDoc;
import me.ranol.scriptingspells.api.docs.ValueList;

@SpellDocs("모든 엔티티 대상 지정 스펠의 기반이 되는 클래스입니다. 사용하지 않는 것을 추천합니다.")
public abstract class TargetedEntitySpell extends TargetedSpell {

	@SpellOption("target-self")
	@OptionDocs("시전자만을 대상으로 삼을 여부입니다.")
	protected boolean targetSelf = false;

	@SpellOption("target-message")
	@OptionDocs("스펠 발동 성공 시, 대상에게 보내는 메시지입니다.")
	protected String targetMessage = "";

	@SpellOption("can-target")
	@SpellParser(TargetParser.class)
	@OptionDocs("대상이 될 수 있는 것들의 목록입니다.")
	@ValueList({ "player", "monster", "animals", "all", "self",
			"등이 있으며, !를 붙여 부정을 할 수 있습니다. 자세한 목록은 Spigot Doc을 참고하세요." })
	@SpigotDoc("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html")
	protected TargetFilter filter = new TargetFilter();

	public TargetedEntitySpell(String name) {
		super(name);
	}

	public abstract SpellCastState castAtEntity(LivingEntity caster, LivingEntity target, float power);

	@Override
	public final SpellCastState castReal(LivingEntity entity, float power) {
		LivingEntity target = getTarget(entity, range * power);
		if (target == null) return SpellCastState.NOTARGET;
		SpellCastState state = castAtEntity(entity, target, power);
		if (!state.isSpellCancelled() && !targetMessage.isEmpty()) {
			target.sendMessage(targetMessage.replace('&', '§')
				.replace("§§", "&"));
		}
		return state;
	}

	private final LivingEntity getTarget(LivingEntity caster, double range) {
		if (targetSelf) {
			return caster;
		}
		List<Entity> near = caster.getNearbyEntities(range, range, range);
		List<LivingEntity> alive = new ArrayList<>();
		ListIterator<Entity> li = near.listIterator();
		while (li.hasNext()) {
			Entity next = li.next();
			if (next.isValid() && !next.isDead() && next instanceof LivingEntity) alive.add((LivingEntity) next);
		}
		BlockIterator it = new BlockIterator(caster, (int) range);
		while (it.hasNext()) {
			Block b = it.next();
			if (!(b.isLiquid() || b.isEmpty())) {
				break;
			}
			int x = b.getX();
			int y = b.getY();
			int z = b.getZ();
			for (LivingEntity e : alive) {
				double ex = e.getLocation()
					.getX();
				double ey = e.getLocation()
					.getY();
				double ez = e.getLocation()
					.getZ();
				if ((x - 0.75D <= ex) && (ex <= x + 1.75D) && (z - 0.75D <= ez) && (ez <= z + 1.75D) && (y - 1 <= ey)
						&& (ey <= y + 2.5D)) {
					if (filter.canTarget(caster, e)) return e;
				}
			}
		}
		return null;
	}

}
