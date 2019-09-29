package space.bbkr.sandscript.makers;

import org.sandboxpowered.sandbox.api.block.BaseBlock;
import org.sandboxpowered.sandbox.api.block.Block;
import org.sandboxpowered.sandbox.api.block.Material;

/**
 * Util class for making blocks from script packs.
 */
public class BlockMaker {
	public static final BlockMaker INSTANCE = new BlockMaker();

	public Block of(String material) {
		return new BaseBlock(new Block.Settings(Material.getMaterial(material.toUpperCase())));
	}
}
