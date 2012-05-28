package world;


public class Light extends GameObject {

	public float radius = (float) (Math.random()*2000+100);

	public Light(String name) {
		super(name);
	}

}
