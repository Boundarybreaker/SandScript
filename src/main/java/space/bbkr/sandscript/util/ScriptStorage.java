package space.bbkr.sandscript.util;

import org.sandboxpowered.sandbox.api.util.Identity;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared object storage for scripts, due to limitations of JSR223.
 */
public class ScriptStorage {
	public static Map<String, ScriptStorage> ALL_STORAGE = new HashMap<>();

	private Map<String, Object> storage = new HashMap<>();

	private ScriptStorage() {}

	/**
	 * @param namespace The namespace to get a storage for.
	 * @return The storage for that namespace. Will create one if it doesn't exist.
	 */
	public static ScriptStorage of(String namespace) {
		if (!ALL_STORAGE.containsKey(namespace)) ALL_STORAGE.put(namespace, new ScriptStorage());
		return ALL_STORAGE.get(namespace);
	}

	/**
	 * Get a script storage value from Java.
	 * @param id The namespae and key to get the stored object of.
	 * @return The stored object, or null if it doesn't exist.
	 */
	public static Object get(Identity id) {
		return of(id.getNamespace()).get(id.getPath());
	}

	/**
	 * @param key The key of the object to get.
	 * @return The stored object, or null if it doesn't exist.
	 */
	public Object get(String key) {
		return storage.get(key);
	}

	/**
	 * @param key The key to store this object at.
	 * @param value The object to store.
	 */
	public void put(String key, Object value) {
		storage.put(key, value);
	}
}
