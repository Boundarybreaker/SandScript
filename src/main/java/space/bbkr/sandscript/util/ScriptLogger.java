package space.bbkr.sandscript.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptLogger {
	private String prefix;
	private Logger log;

	public ScriptLogger(String prefix) {
		String toUse = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);
		this.prefix = "SandScript|" + toUse;
		this.log = LogManager.getFormatterLogger("SandScript|" + toUse);
	}

	public ScriptLogger() {
		this.prefix = "SandScript";
		this.log = LogManager.getFormatterLogger("SandScript");
	}

	public void info(String s) {
		log.info(getPrefix() + s);
	}

	public void info(String s, Object... objs) {
		log.info(getPrefix() + s, objs);
	}

	public void error(String s) {
		log.error(getPrefix() + s);
	}

	public void error(String s, Throwable e) {
		log.error(getPrefix() + s, e);
	}

	public void error(String s, Object... objs) {
		log.error(getPrefix() + s, objs);
	}

	//TODO: config for debug mode
	public void debug(String s) {
		log.info(getDebugPrefix() + s);
	}

	public void debug(String s, Object... objs) {
		log.info(getDebugPrefix() + s, objs);
	}

	private String getPrefix() {
		return "[" + prefix + "] ";
	}

	private String getDebugPrefix() {
		return "[" + prefix + " Debug] ";
	}
}
