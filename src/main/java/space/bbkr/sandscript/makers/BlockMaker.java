package space.bbkr.sandscript.makers;

import com.hrznstudio.sandbox.api.block.Block;
import com.hrznstudio.sandbox.api.block.Material;
import space.bbkr.sandscript.impl.SimpleBlock;

/**
 * Util class for making blocks from script packs.
 */
public class BlockMaker {
	public static final BlockMaker INSTANCE = new BlockMaker();

	public Block of(String material) {
		return new SimpleBlock(new Block.Settings(Material.getMaterial(material.toUpperCase())));
	}
}
