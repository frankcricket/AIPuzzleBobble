package it.mat.unical.puzzlebobble.solver;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("sphere")
public class SphereConfig {
	
	@Param(0)
	private int x;
	@Param(1)
	private int y;
	@Param(2)
	private int color;
	
	/**
	 * 
	 * @param x position x of current sphere
	 * @param y position y of current sphere
	 * @param color color of current sphere
	 */
	public SphereConfig(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public SphereConfig() {}	
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	

}
