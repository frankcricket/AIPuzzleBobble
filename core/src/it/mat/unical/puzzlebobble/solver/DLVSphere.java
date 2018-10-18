package it.mat.unical.puzzlebobble.solver;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("currentsphere")
public class DLVSphere{
	
//	@Param(0)
//	float x;
//	@Param(1)
//	float y;
	@Param(0)
	int color;
	
	
	public DLVSphere() {
	}
	
//	public DLVSphere(float x, float y, int color) {
////		this.x = x;
////		this.y = y;
//		this.color = color;
//	}
	
	public DLVSphere(int color) {
//		this.x = x;
//		this.y = y;
		this.color = color;
	}
	
//	public float getX() {
//		return x;
//	}
//	public void setX(float x) {
//		this.x = x;
//	}
//	public float getY() {
//		return y;
//	}
//	public void setY(float y) {
//		this.y = y;
//	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
}
