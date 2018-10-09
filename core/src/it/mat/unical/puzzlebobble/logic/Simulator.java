package it.mat.unical.puzzlebobble.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.RoundLoader;
import it.mat.unical.puzzlebobble.entities.Cannon;
import it.mat.unical.puzzlebobble.entities.Sphere;
import it.mat.unical.puzzlebobble.entities.Sphere.Colors;
import it.mat.unical.puzzlebobble.helpers.SettingsHelper;
import it.mat.unical.puzzlebobble.solver.Solver;

public class Simulator {
	
	//configurazione di base
	private Cannon cannon;
	private float xcannon, ycannon;
	private Sphere currSphere;
	private Sphere nextSphere;	
	private List<Sphere> spheres;
	private RoundLoader loader;
	
	private HashMap<Vector2, Float> positions;
	LinkedList<Vector2> uniquePos; 
	
	private final static float DELTA = 5f;
	
//	public Simulator(int round) {
//		cannon = null;
//		currSphere = null;
//		nextSphere = null;
//		spheres = null;
//		
//		initObjs(round);
//	}
	
	public Simulator(final List<Sphere> game_spheres,final Sphere curr_sphere) {
		cannon = null;
		currSphere = null;
		spheres = null;
		positions = null;
		uniquePos = null;
		initObjs(game_spheres,curr_sphere);
	}
	
	private void initObjs(final List<Sphere> game_spheres,final Sphere curr_sphere) {
		
		initCannon();
		spheres = new ArrayList<Sphere>();
		positions = new HashMap<Vector2, Float>();
		uniquePos = new LinkedList<Vector2>();
		
		for(final Sphere elements : game_spheres) {
			Sphere s = new Sphere(elements.color(), elements.gridPosition);
			spheres.add(s);		
		}
		currSphere = new Sphere(curr_sphere.color(), curr_sphere.gridPosition);
		resetSphere();
	}

//	private void initObjs(int round) {
//		
//		initCannon();
//		
//		loader = new RoundLoader();
//		spheres = loader.loadRound(round);
//		currSphere = new Sphere(this.loader.loadRoundColor(round));
//		currSphere.setPosition((Game.GAME_WIDTH / 2) - (currSphere.getWidth() / 2) - 15
//										, 80);
//		nextSphere = new Sphere(this.ingameColor());
//		nextSphere.setPosition((Game.GAME_WIDTH / 2) - (this.currSphere.getWidth() / 2) - 30 , 10);
//		
//		positions = new HashMap<Vector2, Float>();
//		uniquePos = new LinkedList<Vector2>();
//		
//		System.out.println("Fine inizializzazione");
//		
//	}
	
	public float findStartingPositions() {
		
		findPossiblePositions();
		
		while(xcannon <= 470) {
			if(currSphere.state() == Sphere.State.Stopped) {
				currSphere.fixPosition(false);
				positions.put(currSphere.gridPosition(),cannon.getTargetVector().x);
				this.cannon.target(xcannon, ycannon);
				xcannon += (DELTA + SettingsHelper.CANNON_X_SPEED);
				
				resetSphere();
			}
			
			if(cannon.update(DELTA)) {
				cannon.shoot(currSphere);
			}
			currSphere.update(spheres, DELTA);
		}
		removeDuplicate();
		Vector2 sol = Solver.solve(spheres,positions,currSphere);
		System.out.println(sol);
		float cannonPos = positions.get(sol);
		System.out.println("La pos del cannone è: " + cannonPos);

		return cannonPos;
		
	}
	
	private void initCannon() {
		cannon = new Cannon();
		cannon.setPosition(Game.GAME_WIDTH / 2, 0);
		cannon.reset();
		xcannon = SettingsHelper.START_X_CANNON_SIMULATION;
		ycannon = SettingsHelper.START_Y_CANNON_SIMULATION;
		cannon.target(xcannon, ycannon);
	}
	
	private void resetSphere() {
		currSphere.setPosition((Game.GAME_WIDTH / 2) - (currSphere.getWidth() / 2) - 15
				, 80);
		currSphere.setState(Sphere.State.Ready);
	}
	
	private void removeDuplicate() {
		for(Iterator<Map.Entry<Vector2, Float>> it = positions.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<Vector2, Float> entry = it.next();
		      boolean found = false;
		      for(Vector2 v : uniquePos) {
		    	  if(((Vector2)entry.getKey()).equals(v)) {
		    		  found = true;
		    		  break;
		    	  }
		      }
		      if(!found) {
		    	  it.remove();
		      }
		 }
		for(Iterator<Map.Entry<Vector2, Float>> it = positions.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<Vector2, Float> entry = it.next();
		     System.out.println(entry.getKey() +" "+entry.getValue());
		 }
	}
	
	private void findPossiblePositions() {
		LinkedList<Vector2> possPositions = new LinkedList<Vector2>();
		for (Sphere s : spheres) {
			checkNeighbors(s, possPositions);
		}

		Iterator<Vector2> it = possPositions.iterator();
		while(it.hasNext()) {
			Vector2 curr = it.next();
			if(curr.x >= 8 || curr.x <= -1 || curr.y >= 1) {
				it.remove();
			}
		}
		
		
		for(Vector2 v : possPositions) {
			if(!uniquePos.contains(v)) {
				uniquePos.add(v);
			}
		}
		
		System.out.println("Posizioni:");
		for(Vector2 pos: uniquePos) {
			System.out.println(pos);
		}
		
	}

	private void checkNeighbors(Sphere s, LinkedList<Vector2> possPositions) {
		Vector2 position = s.gridPosition();
		List<Sphere> neighbors = s.findNeighbors(spheres);

		Vector2 tmp = new Vector2(0.0F, 0.0F);

		tmp.x = (position.x - Math.abs(position.y) % 2.0F); // top-left
		tmp.y = (position.y + 1.0F);
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}

		tmp.x = (position.x - Math.abs(position.y) % 2.0F + 1.0F); // top-right
		tmp.y = (position.y + 1.0F);
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}

		tmp.x = (position.x - 1.0F); // left
		tmp.y = position.y;
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}

		tmp.x = (position.x + 1.0F); // right
		tmp.y = position.y;
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}

		tmp.x = (position.x - Math.abs(position.y) % 2.0F); // bottom-left
		tmp.y = (position.y - 1.0F);
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}

		tmp.x = (position.x - Math.abs(position.y) % 2.0F + 1.0F); // bottom-right
		tmp.y = (position.y - 1.0F);
		if (notCollide(tmp, neighbors)) {
			possPositions.add(new Vector2(tmp));
		}
				
	}
	
	private boolean notCollide(Vector2 v, List<Sphere> neighbors) {
		for (Sphere list : neighbors) {
			if (list.gridPosition.equals(v))
				return false;
		}
		return true;
	}
	
	//ritorna un colore random tra i colori delle sfere presenti nel gioco
	private Colors ingameColor() {
		List<Colors> colors = new ArrayList<Colors>();
		Iterator<Sphere> it = this.spheres.iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			if (!colors.contains(s.color())) {
				colors.add(s.color());
			}
		}
		return colors.get(MathUtils.random(colors.size() - 1));
	}

}
