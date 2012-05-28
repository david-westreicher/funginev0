package world;


public class Light extends GameObject {

	/**
	 * @uml.property  name="radius"
	 */
	public float radius = (float) (Math.random()*2000+100);

	public Light(String name) {
		super(name);
	}

}
