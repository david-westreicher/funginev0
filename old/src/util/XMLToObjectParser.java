package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import manager.SpriteManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import physics.PhysicsTest;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;

import rendering.AnimationRenderer;
import rendering.BoxRenderer;
import rendering.LightRenderer;
import rendering.ModelRenderer;
import rendering.SpriteRenderer;
import rendering.TestRenderer;
import rendering.VidRenderer;
import script.GameScript;
import settings.Settings;

import world.GameObjectType;

public class XMLToObjectParser extends DefaultHandler {
	private GameObjectType currentObject;
	private String currentTag;
	private Map<String, String> currentAtt = new HashMap<String, String>();

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		currentAtt.clear();
		for (int i = 0; i < atts.getLength(); i++) {
			currentAtt.put(atts.getQName(i), atts.getValue(i));
		}
		if (qName.equals("object")) {
			currentObject = new GameObjectType(currentAtt.get("name"));
			String ordering = currentAtt.get("layer");
		} else {
			currentTag = qName;
		}

	}

	public void endElement(String namespaceURI, String localName, String qName) {
		if (qName.equals("object")) {
			Log.log(this, GameObjectType.getType(currentObject.name) + " added");
		}
	}

	public void characters(char ch[], int start, int length) {
		String s = new String(ch, start, length).trim();
		String[] split = s.split(":");
		if (s.length() > 0) {
			if (currentTag.equals("name")) {
				currentObject.name = s;
			} else if (currentTag.equals("script")) {
				currentObject.script = new GameScript(s);
			} else if (currentTag.equals("physics")) {
				float size = Float.parseFloat(split[1])
						* PhysicsTest.PHYSICS_SCALE / 2;
				if (split[0].equals("box"))
					currentObject.shape = new BoxShape(new Vector3f(size, size,
							size));
				else if (split[0].equals("sphere"))
					currentObject.shape = new SphereShape(size);
			} else if (currentTag.equals("renderer")) {
				if (s.equals("simple"))
					currentObject.renderer = new TestRenderer();
				else if (s.equals("box"))
					currentObject.renderer = new BoxRenderer();
				else if (s.equals("light"))
					currentObject.renderer = new LightRenderer();
				if (split.length > 1) {
					if (split[1].equals("anim"))
						currentObject.renderer = new AnimationRenderer(split[0]);
					else if (split[1].equals("obj"))
						currentObject.renderer = new ModelRenderer(split[0]);
					else if (split[1].equals("img"))
						currentObject.renderer = new SpriteRenderer(split[0]);
					else if (split[1].equals("vid"))
						currentObject.renderer = new VidRenderer(split[0]);
				}
			} else if (currentTag.equals("val")) {
				currentObject.set(currentAtt.get("name"), s);
			}
		}
	}

	public void parse() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(Settings.RESSOURCE_FOLDER + Settings.OBJECTS_XML,
					this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
