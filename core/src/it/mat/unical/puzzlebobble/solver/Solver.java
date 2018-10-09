package it.mat.unical.puzzlebobble.solver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.entities.Sphere;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv.desktop.DLVDesktopService;

public class Solver {

	private static String encodingResource = "encodings/puzzlebobble";
	private static Handler handler = null;
	private static InputProgram facts = null;

	public static Vector2 solve(List<Sphere> spheres, HashMap<Vector2, Float> positions, Sphere currentSphere) {
		
		System.out.println("Dim: " + spheres.size() + " // " + positions.size());
		
		handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
		facts = new ASPInputProgram();
		
		try {
			int x,y,col;
			for (Sphere s : spheres) {
				x = (int) s.gridPosition().x;
				y = ((int) s.gridPosition().y) * - 1; //asse y cambiata di segno perchè negativa
				col = s.getColorId();
				facts.addObjectInput(new SphereConfig(x, y, col));
			}
		
			for(Iterator<Map.Entry<Vector2, Float>> it = positions.entrySet().iterator(); it.hasNext(); ) {
			      Map.Entry<Vector2, Float> entry = it.next();
			      x = (int) entry.getKey().x;
			      y = ((int) entry.getKey().y) * - 1; //asse y cambiata di segno perchè negativa
			      facts.addObjectInput(new PositionConfig(x,y,entry.getValue()));
			}
			
			facts.addObjectInput(new DLVSphere(currentSphere.gridPosition.x, currentSphere.gridPosition.y, currentSphere.getColorId()));
		} catch (Exception e) {
			System.err.println("Error adding facts");
			return null;
		}

		InputProgram encoding = new ASPInputProgram();
		encoding.addFilesPath(encodingResource);
		handler.addProgram(encoding);
		handler.addProgram(facts);
		Output o = handler.startSync();
		AnswerSets answers = (AnswerSets) o;

		System.out.println("Size: " + answers.getAnswersets().size());
		for (AnswerSet a : answers.getAnswersets()) {
//			System.out.println(a);
			try {
				for (String obj : a.getAnswerSet()) {
					System.out.println(obj);
					if(obj.startsWith("inGrid")) {
						return parseRow(obj);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		return null;	
		
	}
	
	private static Vector2 parseRow(final String s) {
		 //inGrid(x,y)
		String[] values = (s.substring(7, 10)).split(",");
		int x = Integer.parseInt(values[0]);
		int y = (Integer.parseInt(values[1])) * -1;
		
//		System.out.println(x +" // " + y);
		return new Vector2(x, y);
		
	}
	
	public static Handler getHandler(boolean action) {
		if (handler == null || action) {
			handler = new DesktopHandler(new DLVDesktopService("lib/dlv.mingw.exe"));
		}
		return handler;
	}
	
	public static void main(String[] args) {
		Solver.solve(null, null, null);
	}

}
