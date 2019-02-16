package it.mat.unical.puzzlebobble.solver;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("currentsphere")
public class CurrSphere{
	
	@Param(0)
	int color;
	
	public CurrSphere() {
	}

	public CurrSphere(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
}
