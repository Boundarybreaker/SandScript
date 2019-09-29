package space.bbkr.sandscript.makers;

import com.hrznstudio.sandbox.api.block.Block;
import com.hrznstudio.sandbox.api.item.BlockItem;
import com.hrznstudio.sandbox.api.item.Item;

/**
 * Util class for making items from script packs.
 */
public class ItemMaker {
	public static final ItemMaker INSTANCE = new ItemMaker();

	public Item of() {
		return new Item(new Item.Settings());
	}

	public Item of(Item.Settings settings) {
		return new Item(settings);
	}

	public Item ofBlock(Block block) {
		return new BlockItem(block);
	}

	public Item.Settings Settings() {
		return new Item.Settings();
	}
}
