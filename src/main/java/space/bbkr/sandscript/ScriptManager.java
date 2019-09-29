package space.bbkr.sandscript;

import org.sandboxpowered.sandbox.api.util.Identity;
import space.bbkr.sandscript.util.ScriptIdentity;
import space.bbkr.sandscript.util.ScriptLogger;
import space.bbkr.sandscript.util.ScriptStorage;

import javax.annotation.Nullable;
import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Manager for all scripts added through script packs.
 */
public class ScriptManager {
	//TODO: set up a system like resource/data packs where you go `~/scriptpacks/<pack>/scripts` instead
	private final Path scriptStorage = new File("scripts").toPath();
	public final ScriptEngineManager SCRIPT_MANAGER = new ScriptEngineManager();
	private final ScriptLogger logger = new ScriptLogger();

	//TODO: learn how to do trees?
	private Map<Identity, String> scripts;

	public static final ScriptManager INSTANCE = new ScriptManager();

	public ScriptManager() {
		scripts = populateScripts();
	}

	public String getRawScript(Identity id) {
		return scripts.get(id);
	}

	public List<Identity> getScriptsAt(String subfolder) {
		List<Identity> ret = new ArrayList<>();
		for (Identity scriptId : scripts.keySet()) {
			if (scriptId.getPath().startsWith(subfolder)) {
				ret.add(scriptId);
			}
		}
		return ret;
	}

	public void runScripts(List<Identity> scripts, BiConsumer<Identity, ScriptContext> builder) {
		for (Identity id : scripts) {
			runScript(id, builder);
		}
	}

	public void runScript(Identity id, BiConsumer<Identity, ScriptContext> builder) {
		String function = "";
		if (id instanceof ScriptIdentity) {
			function = ((ScriptIdentity)id).getFunction();
		}
		String script = scripts.get(id);
		String extension = id.getPath().substring(id.getPath().lastIndexOf('.') + 1);
		ScriptEngine engine = SCRIPT_MANAGER.getEngineByExtension(extension);
		if (engine == null) {
			logger.error("Could not find engine for extension: " + extension);
			return;
		}
		try {
			ScriptContext ctx = engine.getContext();
			builder.accept(id, ctx);
			ctx.setAttribute("storage", ScriptStorage.of(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			ctx.setAttribute("log", new ScriptLogger(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			engine.eval(script);
			if (!function.equals("")) {
				Invocable invocable = (Invocable)engine;
				invocable.invokeFunction(function);
			}
		} catch (ScriptException | NoSuchMethodException e) {
			logger.error("Error executing script %s: %s", id.toString(), e.getMessage());
		}
	}

	public Object runFunction(ScriptIdentity id, BiConsumer<Identity, ScriptContext> builder) {
		return runFunction(id, builder, null);
	}

	public <T> Object runFunction(ScriptIdentity id, BiConsumer<Identity, ScriptContext> builder, @Nullable T funcArg) {
		String script = scripts.get(id);
		String extension = id.getPath().substring(id.getPath().lastIndexOf('.') + 1);
		ScriptEngine engine = SCRIPT_MANAGER.getEngineByExtension(extension);
		if (engine == null) {
			logger.error("Could not find engine for extension: " + extension);
			return null;
		}
		try {
			ScriptContext ctx = engine.getContext();
			builder.accept(id, ctx);
			ctx.setAttribute("storage", ScriptStorage.of(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			ctx.setAttribute("log", new ScriptLogger(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
			engine.eval(script); //TODO: is this necessary?
			if (id.hasFunction()) {
				Invocable invocable = (Invocable)engine;
				if (funcArg == null) {
					return invocable.invokeFunction(id.getFunction());
				} else {
					return invocable.invokeFunction(id.getFunction(), funcArg);
				}
			}
			else throw new IllegalArgumentException("Must have a function to run!");
		} catch (ScriptException | NoSuchMethodException e) {
			logger.error("Error executing script %s: %s", id.toString(), e.getMessage());
			return null;
		}
	}

	private Map<Identity, String> populateScripts() {
		//TODO: clean up
		Map<Identity, String> ret = new HashMap<>();
		File allScripts = scriptStorage.toFile();
		if (!allScripts.isDirectory()) allScripts.mkdirs();
		try(Stream<Path> namespaces = Files.walk(scriptStorage, 1)) {
			namespaces.forEach(path -> {
				if (path.toString().equals(scriptStorage.toString())) return;
				String namespace = path.toString().substring(path.toString().lastIndexOf('/')+1);
				if (path.toFile().isDirectory()) {
					try (Stream<Path> children = Files.walk(path)){
						children.forEach(child -> {
							if (!child.toFile().isDirectory()) {
								synchronized(this) {
									try {
										String scriptPath = child.toString().substring(child.toString().indexOf(namespace)+namespace.length()+1);
										String contents = new String(Files.readAllBytes(child), StandardCharsets.UTF_8);
										ret.put(Identity.of(namespace, scriptPath), contents);
									} catch (IOException e) {
										logger.error("Encountered IO exception while walking files in %s: %s", path.toString(), e.getMessage());
									}
								}
							}
						});
					} catch (IOException e) {
						logger.error("Encountered IO exception while searching namespace %s: %s", namespace, e.getMessage());
					}
				} else {
					logger.error("File %s is misplaced; `scripts` may only contain directories", path.toString());
				}
			});
		} catch (IOException e) {
			logger.error("Encountered IO Exception when walking scripts folder: %s", e.getMessage());
		}
		return ret;
	}
}
