package it.mat.unical.puzzlebobble.solver;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("position")
public class PositionConfig {
	
	@Param(0)
	private int x;
	@Param(1)
	private int y;
	@Param(2)
	private float pos;
	
	/**
	 * @param x position x 
	 * @param y position y
	 * @param pos position of the cannon to shoot the right ball
	 */
	public PositionConfig(int x, int y, float pos) {
		this.x = x;
		this.y = y;
		this.pos = pos;
	}
	
	public PositionConfig() {
	}
	
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

	public float getPos() {
		return pos;
	}

	public void setPos(float pos) {
		this.pos = pos;
	}


}
