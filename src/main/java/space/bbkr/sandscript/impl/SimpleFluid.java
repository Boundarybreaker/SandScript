package space.bbkr.sandscript.impl;

import org.sandboxpowered.sandbox.api.Registries;
import org.sandboxpowered.sandbox.api.block.Block;
import org.sandboxpowered.sandbox.api.block.FluidBlock;
import org.sandboxpowered.sandbox.api.block.Material;
import org.sandboxpowered.sandbox.api.fluid.BaseFluid;
import org.sandboxpowered.sandbox.api.fluid.Fluid;
import org.sandboxpowered.sandbox.api.item.BucketItem;
import org.sandboxpowered.sandbox.api.item.Item;
import org.sandboxpowered.sandbox.api.state.BlockState;
import org.sandboxpowered.sandbox.api.state.FluidState;
import org.sandboxpowered.sandbox.api.state.Properties;
import org.sandboxpowered.sandbox.api.state.StateFactory;
import org.sandboxpowered.sandbox.api.util.Identity;

import java.util.function.Supplier;

public class SimpleFluid extends BaseFluid {
	protected Identity id;
	protected Fluid flowing;
	protected Block block;
	protected Item bucket;

	private boolean isInfinite;

	public SimpleFluid(Identity id, boolean isInfinite) {
		this(id, isInfinite, true);
	}

	public SimpleFluid(Identity id, boolean isInfinite, boolean isBase) {
		this.id = id;
		if (isBase) {
			Identity flowingId = Identity.of(id.getNamespace(), "flowing_" + id.getPath());
			this.flowing = getFlowing().get();
			Registries.FLUID.register(flowingId, flowing);
			this.block = new FluidBlock(new Block.Settings(Material.WATER), this);
			Registries.BLOCK.register(id, block);
			Identity bucketId = Identity.of(id.getNamespace(), id.getPath() + "_bucket");
			this.bucket = new BucketItem(new Item.Settings().setStackSize(1), this);
			Registries.ITEM.register(bucketId, bucket);
		}

		this.isInfinite = isInfinite;
	}

	@Override
	public Identity getTexturePath(boolean flowing) {
		return Identity.of(id.getNamespace(), "block/" + id.getPath() + "_" + (flowing ? "flow" : "still"));
	}

	@Override
	public boolean isStill(FluidState state) {
		return true;
	}

	@Override
	public BlockState asBlockState(FluidState state) {
		return block.getBaseState().with(Properties.FLUID_BLOCK_LEVEL, getBlockstateLevel(state));
	}

	@Override
	public Fluid asStill() {
		return this;
	}

	@Override
	public Fluid asFlowing() {
		return flowing;
	}

	Supplier<Fluid> getFlowing() {
		return () -> new Flowing(id, isInfinite);
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	public Item asBucket() {
		return bucket;
	}

	public static class Flowing extends SimpleFluid {
		public Flowing(Identity id, boolean isInfinite) {
			super(id, isInfinite, false);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}

		@Override
		public void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(Properties.FLUID_LEVEL);
		}
	}
}
