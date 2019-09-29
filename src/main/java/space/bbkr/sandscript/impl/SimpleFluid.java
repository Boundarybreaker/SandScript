package space.bbkr.sandscript.impl;

import com.hrznstudio.sandbox.api.Registries;
import com.hrznstudio.sandbox.api.block.Block;
import com.hrznstudio.sandbox.api.block.FluidBlock;
import com.hrznstudio.sandbox.api.block.IBlock;
import com.hrznstudio.sandbox.api.block.Material;
import com.hrznstudio.sandbox.api.fluid.Fluid;
import com.hrznstudio.sandbox.api.fluid.IFluid;
import com.hrznstudio.sandbox.api.item.BucketItem;
import com.hrznstudio.sandbox.api.item.IItem;
import com.hrznstudio.sandbox.api.item.Item;
import com.hrznstudio.sandbox.api.state.BlockState;
import com.hrznstudio.sandbox.api.state.FluidState;
import com.hrznstudio.sandbox.api.state.Properties;
import com.hrznstudio.sandbox.api.state.StateFactory;
import com.hrznstudio.sandbox.api.util.Identity;

import java.util.function.Supplier;

public class SimpleFluid extends Fluid {
	protected Identity id;
	protected IFluid flowing;
	protected IBlock block;
	protected IItem bucket;

	private boolean isInfinite;

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
	public IFluid asStill() {
		return this;
	}

	@Override
	public IFluid asFlowing() {
		return flowing;
	}

	Supplier<IFluid> getFlowing() {
		return () -> new Flowing(id, isInfinite);
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	public IItem asItem() {
		return bucket;
	}


	public class Flowing extends SimpleFluid {
		public Flowing(Identity id, boolean isInfinite) {
			super(id, isInfinite, false);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}

		@Override
		public void appendProperties(StateFactory.Builder<IFluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(Properties.FLUID_LEVEL);
		}
	}
}
