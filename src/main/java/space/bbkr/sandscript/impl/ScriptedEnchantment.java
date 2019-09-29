package space.bbkr.sandscript.impl;

import org.sandboxpowered.sandbox.api.item.ItemStack;
import org.sandboxpowered.sandbox.api.util.Identity;
import org.sandboxpowered.sandbox.api.enchant.BaseEnchantment;
import space.bbkr.sandscript.ScriptManager;
import space.bbkr.sandscript.util.ScriptLogger;
import space.bbkr.sandscript.util.ScriptStorage;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptedEnchantment extends BaseEnchantment {
	private Identity id;
	private String script;
	private ScriptEngine engine;
	private Invocable runner;
	private ScriptLogger logger;

	public ScriptedEnchantment(Identity id) {
		this.id = id;
		this.script = ScriptManager.INSTANCE.getRawScript(id);
		this.engine = init();
		if (this.engine instanceof Invocable) {
			this.runner = (Invocable)engine;
		} else throw new IllegalArgumentException("Script engine " + engine.getFactory().getEngineName() + " is not invocable! This cannot be used for making enchantments!");
		this.logger = new ScriptLogger(id.getNamespace());
	}

	@Override
	public int getMinimumLevel() {
		try {
			Object ret = runner.invokeFunction("getMinimumLevel");
			if (ret instanceof Integer) return (Integer)ret;
			else throw new IllegalArgumentException("Bad return value for getMinimumLevel in " + id.toString() + ": must be an int");
		} catch (ScriptException e) {
			logger.error("Cannot calculate getMinimumLevel for %s: %s", id.toString(), e.getMessage());
			return 1;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getMinimulLevel in %s, returning 1", id.toString());
			return 1;
		}
	}

	@Override
	public int getMaximumLevel() {
		try {
			Object ret = runner.invokeFunction("getMaximumLevel");
			if (ret instanceof Integer) return (Integer)ret;
			else throw new IllegalArgumentException("Bad return value for getMaximumLevel in " + id.toString() + ": must be an int");
		} catch (ScriptException e) {
			logger.error("Cannot calculate getMaximumLevel for %s: %s", id.toString(), e.getMessage());
			return 1;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getMaximumLevel in %s, returning 1", id.toString());
			return 1;
		}
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		try {
			Object ret = runner.invokeFunction("isAcceptibleItem", stack);
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for isAcceptibleItem in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate isAcceptibleItem for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for isAcceptibleItem in %s, returning false", id.toString());
			return false;
		}
	}

	@Override
	public boolean isCurse() {
		try {
			Object ret = runner.invokeFunction("isCurse");
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for isCurse in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate isCurse for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for isCurse in %s, returning false", id.toString());
			return false;
		}
	}

	@Override
	public boolean isTreasure() {
		try {
			Object ret = runner.invokeFunction("isTreasure");
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for isTreasure in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate isBoolean for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for isBoolean in %s, returning false", id.toString());
			return false;
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
			engine.eval(script);
			return engine;
		} catch (ScriptException e) {
			logger.error("Error initializing enchantment script %s: %s", id.toString(), e.getMessage());
			return null;
		}
	}
}
