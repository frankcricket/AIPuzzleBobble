package it.mat.unical.puzzlebobble.solver;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("nextsphere")
public class NextSphere{
	
	@Param(0)
	int color;
	
	public NextSphere() {
	}

	public NextSphere(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
}
