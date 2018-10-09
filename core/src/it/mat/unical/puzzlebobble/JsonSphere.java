package it.mat.unical.puzzlebobble;
 
import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.entities.Sphere.Colors;

public class JsonSphere {
	public Vector2 gridPosition;
	public Colors color;

	public JsonSphere() {
	}
	
	public JsonSphere(Colors color, Vector2 position)
	{
		this.gridPosition = position;
		this.color = color;
	}
}
