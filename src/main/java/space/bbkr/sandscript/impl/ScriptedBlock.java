package space.bbkr.sandscript.impl;

import org.sandboxpowered.sandbox.api.block.BaseBlock;
import org.sandboxpowered.sandbox.api.block.Block;
import org.sandboxpowered.sandbox.api.block.Material;
import org.sandboxpowered.sandbox.api.component.Component;
import org.sandboxpowered.sandbox.api.entity.Entity;
import org.sandboxpowered.sandbox.api.entity.player.Hand;
import org.sandboxpowered.sandbox.api.entity.player.PlayerEntity;
import org.sandboxpowered.sandbox.api.item.ItemStack;
import org.sandboxpowered.sandbox.api.state.BlockState;
import org.sandboxpowered.sandbox.api.state.StateFactory;
import org.sandboxpowered.sandbox.api.util.*;
import org.sandboxpowered.sandbox.api.util.math.Position;
import org.sandboxpowered.sandbox.api.util.math.Vec3f;
import org.sandboxpowered.sandbox.api.world.World;
import org.sandboxpowered.sandbox.api.world.WorldReader;
import space.bbkr.sandscript.ScriptManager;
import space.bbkr.sandscript.helper.TextHelper;
import space.bbkr.sandscript.util.ScriptLogger;
import space.bbkr.sandscript.util.ScriptStorage;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptedBlock extends BaseBlock {
	private Identity id;
	private String script;
	private ScriptEngine engine;
	private Invocable runner;
	private ScriptLogger logger;

	public ScriptedBlock(Identity id, Settings settings) {
		super(settings);
		this.id = id;
		this.script = ScriptManager.INSTANCE.getRawScript(id);
		this.engine = init();
		if (this.engine instanceof Invocable) {
			this.runner = (Invocable)engine;
		} else throw new IllegalArgumentException("Script engine " + engine.getFactory().getEngineName() + " is not invocable! This cannot be used for making blocks!");
		this.logger = new ScriptLogger(id.getNamespace());
	}

	@Override
	public InteractionResult onBlockUsed(World world, Position pos, BlockState state, PlayerEntity player, Hand hand, Direction side, Vec3f hit) {
		try {
			Object result = runner.invokeFunction("onBlockUsed", world, pos, state, player, hand, side, hit);
			if (result instanceof InteractionResult) return (InteractionResult) result;
			else if (result instanceof String) {
				switch(((String)result).toLowerCase()) {
					case "success":
						return InteractionResult.SUCCESS;
					case "ignore":
						return InteractionResult.IGNORE;
					case "failure":
						return InteractionResult.FAILURE;
					default:
						throw new IllegalArgumentException("Bad return value for onBlockUsed in " + id.toString() + ": must be success, ignore, or failure");
				}
			}
			else throw new IllegalArgumentException("Bad return value for onBlockUsed in " + id.toString() + ": must return an InteractionResult or String");
		} catch (ScriptException e) {
			logger.error("Cannot calculate onBlockUsed for %s: %s", id.toString(), e.getMessage());
			return InteractionResult.IGNORE;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onBlockUsed in %s, calling super", id.toString());
			return super.onBlockUsed(world, pos, state, player, hand, side, hit);
		}
	}

	@Override
	public InteractionResult onBlockClicked(World world, Position pos, BlockState state, PlayerEntity player) {
		try {
			Object result = runner.invokeFunction("onBlockClicked", world, pos, state, player);
			if (result instanceof InteractionResult) return (InteractionResult) result;
			else if (result instanceof String) {
				switch(((String)result).toLowerCase()) {
					case "success":
						return InteractionResult.SUCCESS;
					case "ignore":
						return InteractionResult.IGNORE;
					case "failure":
						return InteractionResult.FAILURE;
					default:
						throw new IllegalArgumentException("Bad return value for onBlockClicked in " + id.toString() + ": must be success, ignore, or failure");
				}
			}
			else throw new IllegalArgumentException("Bad return value for onBlockClicked in " + id.toString() + ": must return an InteractionResult or String");
		} catch (ScriptException e) {
			logger.error("Cannot calculate onBlockClicked for %s: %s", id.toString(), e.getMessage());
			return InteractionResult.IGNORE;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onBlockClicked in %s, calling super", id.toString());
			return super.onBlockClicked(world, pos, state, player);
		}
	}

	@Override
	public void onBlockBroken(World world, Position pos, BlockState state) {
		try {
			runner.invokeFunction("onBlockBroken", world, pos, state);
		} catch (ScriptException e) {
			logger.error("Cannot calculate onBlockBroken for %s: %s", id.toString(), e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onBlockBroken in %s, ignoring", id.toString());
		}
	}

	@Override
	public void onBlockPlaced(World world, Position pos, BlockState state, Entity placer, ItemStack stack) {
		try {
			runner.invokeFunction("onBlockPlaced", world, pos, state, placer, stack);
		} catch (ScriptException e) {
			logger.error("Cannot calculate onBlockPlaced for %s: %s", id.toString(), e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onBlockPlaced in %s, ignoring", id.toString());
		}
	}

	@Override
	public BlockState updateOnNeighborChanged(BlockState state, Direction dir, BlockState neighborState, World world, Position pos, Position neighborPos) {
		try {
			Object ret = runner.invokeFunction("updateOnNeighborChanged", state, dir, neighborState, world, pos, neighborPos);
			if (ret instanceof BlockState) return (BlockState)ret;
			else throw new IllegalArgumentException("Bad return value for updateOnNeighborChanged in " + id.toString() + ": must be a BlockState");
		} catch (ScriptException e) {
			logger.error("Cannot calculate updateOnNeighborChanged for %s: %s", id.toString(), e.getMessage());
			return state;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for updateOnNeighborChanged in %s, calling super", id.toString());
			return super.updateOnNeighborChanged(state, dir, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public ItemStack getPickStack(WorldReader reader, Position pos, BlockState state) {
		try {
			Object ret = runner.invokeFunction("getPickStack", reader, pos, state);
			if (ret instanceof ItemStack) return (ItemStack) ret;
			else throw new IllegalArgumentException("Bad return value for getPickStack in " + id.toString() + ": must be an ItemStack");
		} catch (ScriptException e) {
			logger.error("Cannot calculate getPickStack for %s: %s", id.toString(), e.getMessage());
			return ItemStack.empty();
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getPickStack in %s, calling super", id.toString());
			return super.getPickStack(reader, pos, state);
		}
	}

	@Override
	public boolean hasBlockEntity() {
		try {
			Object ret = runner.invokeFunction("hasBlockEntity");
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for hasBlockEntity in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate hasBlockEntity for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for hasBlockEntity in %s, calling super", id.toString());
			return super.hasBlockEntity();
		}
	}

	@Override
	public boolean isNaturalDirt() {
		try {
			Object ret = runner.invokeFunction("isNaturalDirt");
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for isNaturalDirt in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate isNaturalDirt for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for isNaturalDirt in %s, calling super", id.toString());
			return super.isNaturalDirt();
		}
	}

	@Override
	public boolean isNaturalStone() {
		try {
			Object ret = runner.invokeFunction("isNaturalStone");
			if (ret instanceof Boolean) return (Boolean)ret;
			else throw new IllegalArgumentException("Bad return value for isNaturalStone in " + id.toString() + ": must be a boolean");
		} catch (ScriptException e) {
			logger.error("Cannot calculate isNaturalStone for %s: %s", id.toString(), e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for isNaturalStone in %s, calling super", id.toString());
			return super.isNaturalStone();
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		try {
			Object ret = runner.invokeFunction("rotate", rotation);
			if (ret instanceof BlockState) return (BlockState)ret;
			else throw new IllegalArgumentException("Bad return value for rotate in " + id.toString() + ": must be a BlockState");
		} catch (ScriptException e) {
			logger.error("Cannot calculate rotate for %s: %s", id.toString(), e.getMessage());
			return state;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for rotate in %s, calling super", id.toString());
			return super.rotate(state, rotation);
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		try {
			Object ret = runner.invokeFunction("mirror", mirror);
			if (ret instanceof BlockState) return (BlockState)ret;
			else throw new IllegalArgumentException("Bad return value for mirror in " + id.toString() + ": must be a BlockState");
		} catch (ScriptException e) {
			logger.error("Cannot calculate mirror for %s: %s", id.toString(), e.getMessage());
			return state;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for mirror in %s, calling super", id.toString());
			return super.mirror(state, mirror);
		}
	}

	@Override
	public void onEntityWalk(World world, Position pos, Entity entity) {
		try {
			runner.invokeFunction("onEntityWalk", world, pos, entity);
		} catch (ScriptException e) {
			logger.error("Cannot calculate onEntityWalk for %s: %s", id.toString(), e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for onEntityWalk in %s, calling super", id.toString());
			super.onEntityWalk(world, pos, entity);
		}
	}

	@Override
	public void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		try {
			runner.invokeFunction("appendProperties", builder);
		} catch (ScriptException e) {
			logger.error("Cannot calculate appendProperties for %s: %s", id.toString(), e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for appendProperties in %s, ignoring", id.toString());
		}
	}

	@Override
	public <X> Mono<X> getComponent(WorldReader world, Position position, BlockState state, Component<X> component, Mono<Direction> side) {
		try {
			Object result = runner.invokeFunction("getComponent", world, position, state, component, side);
			if (result instanceof Mono) return (Mono) result;
			else if (result != null) return (Mono<X>) Mono.of(result);
			else return Mono.empty();
		} catch (ScriptException e) {
			logger.error("Cannot calculate getComponent for %s: %s", id.toString(), e.getMessage());
			return Mono.empty();
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getComponent in %s, calling super", id.toString());
			return super.getComponent(world, position, state, component, side);
		}
	}

	@Override
	public Material.PistonInteraction getPistonInteraction(BlockState state) {
		try {
			Object result = runner.invokeFunction("getPistonInteraction", state);
			if (result instanceof Material.PistonInteraction) return (Material.PistonInteraction)result;
			else if (result instanceof String) {
				switch(((String)result).toLowerCase()) {
					case "normal":
						return Material.PistonInteraction.NORMAL;
					case "destroy":
						return Material.PistonInteraction.DESTROY;
					case "block":
						return Material.PistonInteraction.BLOCK;
					case "ignore":
						return Material.PistonInteraction.IGNORE;
					case "push_only":
						return Material.PistonInteraction.PUSH_ONLY;
					default:
						throw new IllegalArgumentException("Bad return value for getPistonInteraction in " + id.toString() + ": must be normal, destroy, block, ignore, or push_only");
				}
			} else throw new IllegalArgumentException("Bad return value for getPistonInteraction in " + id.toString() + ": must be a PistonInteraction or a String");
		} catch (ScriptException e) {
			logger.error("Cannot calculate getPistonInteraction for %s: %s", id.toString(), e.getMessage());
			return Material.PistonInteraction.NORMAL;
		} catch (NoSuchMethodException e) {
			logger.debug("No function found for getPistonInteraction in %s, calling super", id.toString());
			return super.getPistonInteraction(state);
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
			logger.error("Error initializing block script %s: %s", id.toString(), e.getMessage());
			return null;
		}
	}
}
