package space.bbkr.sandscript.util;

import com.hrznstudio.sandbox.api.util.Identity;

/**
 * Identity which also contains the option for a script function.
 * Formatted as "namespace:path#function".
 */
public class ScriptIdentity implements Identity {
	private Identity id;
	private String function;

	private ScriptIdentity(Identity id, String function) {
		this.id = id;
		this.function = function;
	}

	public static ScriptIdentity of(Identity id, String function) {
		return new ScriptIdentity(id, function);
	}

	public static ScriptIdentity of(String id) {
		String mainId;
		String function = "";
		if (id.contains("#")) {
			String[] split = id.split("#");
			mainId = split[0];
			function = split[1];
		} else {
			mainId = id;
		}
		String[] idSplit = mainId.split(":");
		return new ScriptIdentity(Identity.of(idSplit[0], idSplit[1]), function);
	}

	@Override
	public String getNamespace() {
		return id.getNamespace();
	}

	@Override
	public String getPath() {
		return id.getPath();
	}

	public String getFunction() {
		return function;
	}

	public boolean hasFunction() {
		return !function.equals("");
	}

	@Override
	public String toString() {
		String mainId = id.toString();
		if (!hasFunction()) return mainId;
		else return mainId + "#" + function;
	}
}
