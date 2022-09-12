package sk.qbsw.sed.fw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CPageResourceConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<String> pluginScripts = new ArrayList<>();
	private final List<String> themeScripts = new ArrayList<>();
	private final List<String> jsInitializationCommands = new ArrayList<>();

	public void addPluginScript(String scriptPath) {
		pluginScripts.add(scriptPath);
	}

	public void addPluginScripts(List<String> scripts) {
		pluginScripts.addAll(scripts);
	}

	public void addInitializationCommand(String command) {
		jsInitializationCommands.add(command);
	}

	public void addThemeScript(String scriptPath) {
		themeScripts.add(scriptPath);
	}

	public void addThemeScripts(List<String> scripts) {
		themeScripts.addAll(scripts);
	}

	public List<String> getPluginScripts() {
		return pluginScripts;
	}

	public List<String> getThemeScripts() {
		return themeScripts;
	}

	public List<String> getJsInitializationCommands() {
		return jsInitializationCommands;
	}
}
