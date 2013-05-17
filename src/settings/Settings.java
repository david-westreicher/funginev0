package settings;

import java.io.File;

public class Settings {

	public static String RESSOURCE_FOLDER = "ressources" + File.separator;
	public static final String OBJECTS_XML = "objects.xml";
	public static final String MAIN_SCRIPT = "main.js";
	public static final String INIT_SCRIPT = "init.js";
	public static final int WIDTH = 640;
	public static final int HEIGHT = 800;
	public static final boolean STEREO = true;
	public static final boolean USE_FULL_SCREEN = false;
	public static final String ENGINE_FOLDER = "engine" + File.separator;
	public static final boolean VR = STEREO;
	public static boolean USE_BROWSER = false;
	public static boolean SHOW_STATUS = true;
}
