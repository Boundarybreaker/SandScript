package space.bbkr.sandscript.helper;


import org.sandboxpowered.sandbox.api.Registries;
import org.sandboxpowered.sandbox.api.block.BaseBlock;
import org.sandboxpowered.sandbox.api.block.Block;
import org.sandboxpowered.sandbox.api.block.Material;
import org.sandboxpowered.sandbox.api.block.entity.BlockEntity;
import org.sandboxpowered.sandbox.api.container.ContainerFactory;
import org.sandboxpowered.sandbox.api.enchant.Enchantment;
import org.sandboxpowered.sandbox.api.fluid.Fluid;
import org.sandboxpowered.sandbox.api.item.BaseBlockItem;
import org.sandboxpowered.sandbox.api.item.BlockItem;
import org.sandboxpowered.sandbox.api.item.Item;
import org.sandboxpowered.sandbox.api.util.Identity;
import space.bbkr.sandscript.ScriptManager;
import space.bbkr.sandscript.impl.ScriptedBlock;
import space.bbkr.sandscript.impl.ScriptedEnchantment;
import space.bbkr.sandscript.impl.ScriptedFluid;
import space.bbkr.sandscript.impl.ScriptedItem;

import java.util.List;

/**
 * Helper to easily register various objects into Sandbox's built-in registries.
 */
public class RegistryHelper {
	private String namespace;

	public RegistryHelper(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Register a new block.
	 * @param name The name of this block. Namespace will be automatically added.
	 * @param block The block to register.
	 * @return The block that's been successfully registered.
	 */
	public Block block(String name, Block block) {
		Registries.BLOCK.register(Identity.of(namespace, name), block);
		return block;
	}

	public Block blockOf(String name, String material) {
		List<Identity> scripts = ScriptManager.INSTANCE.getScriptsAt("block/" + name);
		if (scripts.size() > 1) throw new IllegalArgumentException("Cannot have multiple script files for a block!");
		if (scripts.size() == 0) throw new IllegalArgumentException("Could not find any scripts for block ID " + namespace + ":" + name + "!");
		Material mat = Material.getMaterial(material.toUpperCase());
		Block block = new ScriptedBlock(scripts.get(0), new Block.Settings(mat));
		return block(name, block);
	}

	/**
	 * Register a new item.
	 * @param name The name of this item. Namespace will be automatically added.
	 * @param item The item to register.
	 * @return The block that's been successfully registered.
	 */
	public Item item(String name, Item item) {
		Registries.ITEM.register(Identity.of(namespace, name), item);
		return item;
	}

	/**
	 * Register a new block item.
	 * @param block The block to get an item for.
	 * @return The item form of that block.
	 */
	public Item itemOf(BaseBlock block) {
		Identity id = Registries.BLOCK.getIdentity(block);
		Item item = new BaseBlockItem(block);
		Registries.ITEM.register(id, item);
		return item;
	}

	public Item itemOf(String name, Item.Settings settings) {
		List<Identity> scripts = ScriptManager.INSTANCE.getScriptsAt("item/" + name);
		if (scripts.size() > 1) throw new IllegalArgumentException("Cannot have multiple script files for an item!");
		if (scripts.size() == 0) throw new IllegalArgumentException("Could not find any scripts for item ID " + namespace + ":" + name + "!");
		Item item = new ScriptedItem(scripts.get(0), settings);
		return item(name, item);
	}

	/**
	 * Register a new fluid.
	 * @param name The name of this fluid. Namespace will be automatically added.
	 * @param fluid The fluid to register.
	 * @return The fluid that's been successfully registered.
	 */
	public Fluid fluid(String name, Fluid fluid) {
		Registries.FLUID.register(Identity.of(namespace, name), fluid);
		return fluid;
	}

	public Fluid fluidOf(String name) {
		List<Identity> scripts = ScriptManager.INSTANCE.getScriptsAt("fluid/" + name);
		if (scripts.size() > 1) throw new IllegalArgumentException("Cannot have multiple script files for a fluid!");
		if (scripts.size() == 0) throw new IllegalArgumentException("Could not find any scripts for fluid ID " + namespace + ":" + name + "!");
		Identity id = Identity.of(namespace, name);
		Fluid fluid = new ScriptedFluid(id, scripts.get(1), true);
		Registries.FLUID.register(id, fluid);
		return fluid;
	}

	/**
	 * Register a new block entity type.
	 * @param name The name of this block entity. Namespace will be automatically added.
	 * @param be The BE type to register.
	 * @return The BE type that's been successfully registered.
	 */
	public BlockEntity.Type blockEntity(String name, BlockEntity.Type be) {
		Registries.BLOCK_ENTITY.register(Identity.of(namespace, name), be);
		return be;
	}

	/**
	 * Register a new enchantment.
	 * @param name The name of this enchantment. Namespace will be automatically added.
	 * @param enchantment The enchantment to register.
	 * @return The enchantment that's been successfully registered.
	 */
	public Enchantment enchantment(String name, Enchantment enchantment) {
		Registries.ENCHANTMENT.register(Identity.of(namespace, name), enchantment);
		return enchantment;
	}

	public Enchantment enchantmentOf(String name) {
		List<Identity> scripts = ScriptManager.INSTANCE.getScriptsAt("enchantment/" + name);
		if (scripts.size() > 1) throw new IllegalArgumentException("Cannot have multiple script files for a enchantment!");
		if (scripts.size() == 0) throw new IllegalArgumentException("Could not find any scripts for enchantment ID " + namespace + ":" + name + "!");
		Identity id = Identity.of(namespace, name);
		Enchantment enchant = new ScriptedEnchantment(id);
		Registries.ENCHANTMENT.register(id, enchant);
		return enchant;
	}

	/**
	 * Register a new container factory.
	 * @param name The name of this container factory. Namespace will be automatically added.
	 * @param container The container factory to register.
	 * @return The container factory that's been successfully registered.
	 */
	public ContainerFactory container(String name, ContainerFactory container) {
		Registries.CONTAINER.register(Identity.of(namespace, name), container);
		return container;
	}
}
