package script;

import game.Game;

import javax.script.CompiledScript;

import manager.ScriptManager;

public class GameScript {

	/**
	 * @uml.property  name="script"
	 * @uml.associationEnd  
	 */
	public CompiledScript script;

	public GameScript(String s) {
		((ScriptManager)Game.INSTANCE.getManager("script")).update(s, this);
	}

}
