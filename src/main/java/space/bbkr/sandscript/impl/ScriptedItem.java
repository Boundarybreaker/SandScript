package space.bbkr.sandscript.impl;

import com.hrznstudio.sandbox.api.block.IBlock;
import com.hrznstudio.sandbox.api.component.Component;
import com.hrznstudio.sandbox.api.item.Item;
import com.hrznstudio.sandbox.api.item.ItemStack;
import com.hrznstudio.sandbox.api.util.Identity;
import com.hrznstudio.sandbox.api.util.InteractionResult;
import com.hrznstudio.sandbox.api.util.Mono;
import com.hrznstudio.sandbox.api.util.math.Position;
import com.hrznstudio.sandbox.api.util.text.Text;
import com.hrznstudio.sandbox.api.world.World;
import space.bbkr.sandscript.ScriptManager;
import space.bbkr.sandscript.helper.TextHelper;
import space.bbkr.sandscript.util.ScriptLogger;
import space.bbkr.sandscript.util.ScriptStorage;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

public class ScriptedItem extends Item {
	private Identity id;
	private String script;
	private ScriptEngine engine;
	private Invocable runner;
	private ScriptLogger logger;

	public ScriptedItem(Identity id, Settings settings) {
		super(settings);
		this.id = id;
		this.script = ScriptManager.INSTANCE.getRawScript(id);
		this.engine = init();
		if (this.engine instanceof Invocable) {
			this.runner = (Invocable)engine;
		} else throw new IllegalArgumentException("Script engine " + engine.getFactory().getEngineName() + " is not invocable! This cannot be used for making items!");
		this.logger = new ScriptLogger(id.getNamespace());
	}

	@Override
	public InteractionResult onItemUsed(World world, Position pos, ItemStack stack) {
		try {
			Object result = runner.invokeFunction("onItemUsed", world, pos, stack);
			if (result instanceof InteractionResult) return (InteractionResult) result;
			else if (result instanceof String) {
				switch (((String) result).toLowerCase()) {
					case "success":
						return InteractionResult.SUCCESS;
					case "ignore":
						return InteractionResult.IGNORE;
					case "failure":
						return InteractionResult.FAILURE;
					default:
						throw new IllegalArgumentException("Bad return value for onItemUsed in " + id.toString() + ": must be success, ignore, or failure");
				}
			} else
				throw new IllegalArgumentException("Bad return value for onItemUsed in " + id.toString() + ": must return an InteractionResult or String");
		} catch (ScriptException e) {
			logger.error("Cannot calculate onItemUsed for %s: %s", id.toString(), e.getMessage());
			return InteractionResult.IGNORE;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onItemUsed in %s, calling super", id.toString());
			return super.onItemUsed(world, pos, stack);
		}
	}

	@Override
	public void appendTooltipText(ItemStack stack, @Nullable World world, List<Text> tooltip, boolean advanced) {
		try {
			runner.invokeFunction("appendTooltipText", stack, world, tooltip, advanced);
		} catch (ScriptException e) {
			logger.error("Cannot calculate appendTooltipText for %s: %s", id.toString(), e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for appendToolTipText in %s, ignoring", id.toString());
		}
	}

	@Override
	public <X> Mono<X> getComponent(Component<X> component) {
		try {
			Object result = runner.invokeFunction("getComponent", component);
			if (result instanceof Mono) return (Mono) result;
			else if (result != null) return (Mono<X>) Mono.of(result);
			else return Mono.empty();
		} catch (ScriptException e) {
			logger.error("Cannot calculate getComponent(component) for %s: %s", id.toString(), e.getMessage());
			return Mono.empty();
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getComponent(component) in %s, calling super", id.toString());
			return super.getComponent(component);
		}
	}

	@Override
	public <X> Mono<X> getComponent(Component<X> component, Mono<ItemStack> stack) {
		try {
			Object result = runner.invokeFunction("getComponent", component, stack);
			if (result instanceof Mono) return (Mono)result;
			else if (result != null) return (Mono<X>) Mono.of(result);
			else return Mono.empty();
		} catch (ScriptException e) {
			logger.error("Cannot calculate getComponent(component, mono<stack>) for %s: %s", id.toString(), e.getMessage());
			return Mono.empty();
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getComponent(component, mono<stack>) in %s, calling super", id.toString());
			return super.getComponent(component, stack);
		}
	}

	@Override
	public <X> Mono<X> getComponent(Component<X> component, ItemStack stack) {
		try {
			Object result = runner.invokeFunction("getComponent", component, stack);
			if (result instanceof Mono) return (Mono) result;
			else if (result != null) return (Mono<X>) Mono.of(result);
			else return Mono.empty();
		} catch (ScriptException e) {
			logger.error("Cannot calculate getComponent(component, stack) for %s: %s", id.toString(), e.getMessage());
			return Mono.empty();
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getComponent(component, stack) in %s, calling super", id.toString());
			return super.getComponent(component, stack);
		}
	}

	private ScriptEngine init() {
		String extension = id.getPath().substring(id.getPath().lastIndexOf('.') + 1);
		ScriptEngine engine = ScriptManager.INSTANCE.SCRIPT_MANAGER.getEngineByExtension(extension);
		if (engine == null) {
			logger.error("Could not find engine for extension: " + extension);
			return null;
		}
		try {
			ScriptContext ctx = engine.getContext();
			ctx.setAttribute("storage", ScriptStorage.of(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			ctx.setAttribute("log", new ScriptLogger(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			ctx.setAttribute("Text", TextHelper.INSTANCE, ScriptContext.ENGINE_SCOPE);
			engine.eval(script);
			return engine;
		} catch (ScriptException e) {
			logger.error("Error initializing item script %s: %s", id.toString(), e.getMessage());
			return null;
		}
	}
}
