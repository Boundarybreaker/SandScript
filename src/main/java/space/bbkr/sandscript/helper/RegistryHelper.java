package space.bbkr.sandscript.helper;


import com.hrznstudio.sandbox.api.Registries;
import com.hrznstudio.sandbox.api.block.Block;
import com.hrznstudio.sandbox.api.block.IBlock;
import com.hrznstudio.sandbox.api.block.Material;
import com.hrznstudio.sandbox.api.block.entity.IBlockEntity;
import com.hrznstudio.sandbox.api.container.ContainerFactory;
import com.hrznstudio.sandbox.api.enchant.Enchantment;
import com.hrznstudio.sandbox.api.enchant.IEnchantment;
import com.hrznstudio.sandbox.api.fluid.Fluid;
import com.hrznstudio.sandbox.api.fluid.IFluid;
import com.hrznstudio.sandbox.api.item.BlockItem;
import com.hrznstudio.sandbox.api.item.IItem;
import com.hrznstudio.sandbox.api.item.Item;
import com.hrznstudio.sandbox.api.util.Identity;
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
	public IBlock block(String name, IBlock block) {
		Registries.BLOCK.register(Identity.of(namespace, name), block);
		return block;
	}

	public IBlock blockOf(String name, String material) {
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
	public IItem item(String name, IItem item) {
		Registries.ITEM.register(Identity.of(namespace, name), item);
		return item;
	}

	/**
	 * Register a new block item.
	 * @param block The block to get an item for.
	 * @return The item form of that block.
	 */
	public IItem itemOf(Block block) {
		Identity id = Registries.BLOCK.getIdentity(block);
		IItem item = new BlockItem(block);
		Registries.ITEM.register(id, item);
		return item;
	}

	public IItem itemOf(String name, IItem.Settings settings) {
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
	public IFluid fluid(String name, IFluid fluid) {
		Registries.FLUID.register(Identity.of(namespace, name), fluid);
		return fluid;
	}

	public IFluid fluidOf(String name) {
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
	public IBlockEntity.Type blockEntity(String name, IBlockEntity.Type be) {
		Registries.BLOCK_ENTITY.register(Identity.of(namespace, name), be);
		return be;
	}

	/**
	 * Register a new enchantment.
	 * @param name The name of this enchantment. Namespace will be automatically added.
	 * @param enchantment The enchantment to register.
	 * @return The enchantment that's been successfully registered.
	 */
	public IEnchantment enchantment(String name, IEnchantment enchantment) {
		Registries.ENCHANTMENT.register(Identity.of(namespace, name), enchantment);
		return enchantment;
	}

	public IEnchantment enchantmentOf(String name) {
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
