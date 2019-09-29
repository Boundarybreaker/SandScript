package space.bbkr.sandscript.makers;

import org.sandboxpowered.sandbox.api.fluid.Fluid;
import space.bbkr.sandscript.impl.SimpleFluid;
import space.bbkr.sandscript.util.ScriptIdentity;

/**
 * Util class for creating fluids from script packs.
 */
public class FluidMaker {
	public static final FluidMaker INSTANCE = new FluidMaker();

	public Fluid of(String texId, boolean isInfinite) {
		return new SimpleFluid(ScriptIdentity.of(texId), isInfinite);
	}
}
