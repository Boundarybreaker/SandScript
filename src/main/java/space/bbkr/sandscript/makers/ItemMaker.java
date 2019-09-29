package space.bbkr.sandscript.makers;

import org.sandboxpowered.sandbox.api.block.BaseBlock;
import org.sandboxpowered.sandbox.api.item.BaseBlockItem;
import org.sandboxpowered.sandbox.api.item.BaseItem;

/**
 * Util class for making items from script packs.
 */
public class ItemMaker {
	public static final ItemMaker INSTANCE = new ItemMaker();

	public BaseItem of() {
		return new BaseItem(new BaseItem.Settings());
	}

	public BaseItem of(BaseItem.Settings settings) {
		return new BaseItem(settings);
	}

	public BaseItem ofBlock(BaseBlock block) {
		return new BaseBlockItem(block);
	}

	public BaseItem.Settings Settings() {
		return new BaseItem.Settings();
	}
}
