package it.mat.unical.puzzlebobble.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.entities.Cannon;
import it.mat.unical.puzzlebobble.entities.Sphere;
import it.mat.unical.puzzlebobble.helpers.SettingsHelper;
import it.mat.unical.puzzlebobble.solver.Solver;
import it.mat.unical.puzzlebobble.stages.StagePlay;

public class Simulator extends Thread {

	private Cannon cannon;
	private float xcannon, ycannon;
	private Sphere currSphere;
	private List<Sphere> spheres;

	private final static float DELTA = 5f;

	public Simulator(final List<Sphere> game_spheres, final Sphere curr_sphere) {
		cannon = null;
		currSphere = null;
		spheres = null;
		initObjs(game_spheres, curr_sphere);
	}

	private void initObjs(final List<Sphere> game_spheres, final Sphere curr_sphere) {

		initCannon();
		spheres = new ArrayList<Sphere>();
		for (final Sphere elements : game_spheres) {
			Sphere s = new Sphere(elements.color(), elements.gridPosition);
			spheres.add(s);
		}
		currSphere = new Sphere(curr_sphere.color(), curr_sphere.gridPosition);
		resetSphere();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		findStartingPositions();
		stop();
	}

	@SuppressWarnings("static-access")
	private void findStartingPositions() {

		ArrayList<Vector2> pos = new ArrayList<Vector2>();
		ArrayList<Float> points = new ArrayList<Float>();

		while (xcannon <= 420) {
			cannon.simulationTarget(xcannon, ycannon);
			cannon.shoot(currSphere);
			while (true) {
				currSphere.update(spheres, DELTA);
				if (currSphere.state() == Sphere.State.Stopped) {
					currSphere.fixPosition(false);
//					System.out.println(cannon.getTargetVector() +" // " + currSphere.gridPosition());
					pos.add(currSphere.gridPosition());
					points.add(cannon.getTargetVector().x);
					resetSphere();
					break;
				}
			}
			xcannon += 1.5f;
		}

//		for (int i = 0; i < pos.size(); i++) {
//			System.out.println(pos.get(i) + " " + points.get(i));
//		}

		final HashMap<Vector2, Float> newPositions = getOrderedValues(pos, points);
		for (final Iterator<Map.Entry<Vector2, Float>> it = newPositions.entrySet().iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		final Vector2 sphereVec = Solver.solve(spheres, newPositions, currSphere);
		float cannonPos = newPositions.get(sphereVec);
		System.out.println("Posizioni: " + cannonPos + " // " + sphereVec);

		StagePlay.stagePlay.setCannonTarget(cannonPos - 1f);

		try {
			this.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		StagePlay.stagePlay.setPlayingSate();

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
		currSphere.setPosition((Game.GAME_WIDTH / 2) - (currSphere.getWidth() / 2) - 15, 80);
		currSphere.setState(Sphere.State.Ready);
	}

//	private final HashMap<Vector2,Float> removeDuplicate(HashMap<Float, Vector2> positions,final LinkedList<Vector2> uniquePos) {
//		
//		HashMap<Vector2,Float> newPos = new HashMap<Vector2, Float>();
//		
//		System.out.println("elementi mappa prima rimozione: " + positions.size());
//		for(Iterator<Map.Entry<Float,Vector2>> it = positions.entrySet().iterator(); it.hasNext(); ) {
//		      System.out.println(it.next());
//		}
//
//		for(final Vector2 toCheck : uniquePos) {
//			float min = 600f;
//			for(Iterator<Map.Entry<Float, Vector2>> it = positions.entrySet().iterator(); it.hasNext(); ) {
//			      Map.Entry<Float, Vector2> entry = it.next();
//			      if(toCheck.equals(entry.getValue())){
//			    	  float key = entry.getKey();
//			    	  if(key < min) {
//			    		  min = key;
//			    		  newPos.put(entry.getValue(), entry.getKey());
//			    	  }else {
//			    		  
//			    		  it.remove();
//			    	  }
//			      }
//			 }
//		}
//		
////		System.out.println("dimensione della mappa dopo la rimoz: " + positions.size());
////		for(Iterator<Map.Entry<Vector2, Float>> it = newPos.entrySet().iterator(); it.hasNext(); ) {
////		      System.out.println(it.next());
////		}
//
//		return newPos; 
//	}

//	private final HashMap<Vector2,Float> getOrderedValues(ArrayList<Vector2> positions, ArrayList<Float> points){
//		
//		HashMap<Vector2, Float> posMap = new HashMap<Vector2, Float>();
//		float value = 600f;
//
//		for(int i = 0; i < positions.size(); i++) {
//			Vector2 currentPos = positions.get(i);
//			if(posMap.containsKey(currentPos))
//				continue;
//			int count = 1;
//			value = points.get(i);
//			for(int j = i+1; j < positions.size(); j++) {
//				if(currentPos.equals(positions.get(j))) {
//					if(count <= 4) {
//						value += points.get(j);
//						count ++;
//					}
//					else break;
//				}
//			}
//			posMap.put(currentPos, value/count);
//		}
//		
//		
//		return posMap;
//	}

	private final HashMap<Vector2, Float> getOrderedValues(ArrayList<Vector2> positions, ArrayList<Float> points) {

		HashMap<Vector2, Float> posMap = new HashMap<Vector2, Float>();
		for (int i = 0; i < positions.size(); i++) {
			Vector2 currentPos = positions.get(i);
			if (posMap.containsKey(currentPos))
				continue;
			ArrayList<Float> currVal = new ArrayList<Float>();
			for(int j = i+1; j < positions.size(); j++) {
				if(currentPos.equals(positions.get(j))) {
					currVal.add(points.get(j));
				}
			}
			
			int pos = 0;
			float value = 0f;
			if(currVal.size() > 0) {
				Collections.sort(currVal);
				pos = currVal.size()/2;
				value = currVal.get(pos);
			}
			else {
				value = points.get(i);
			}
			posMap.put(currentPos, value + 0.95f);
		}

		return posMap;
	}

//	private final LinkedList<Vector2> findPossiblePositions() {
//		LinkedList<Vector2> uniquePos = new LinkedList<Vector2>();
//		LinkedList<Vector2> possPositions = new LinkedList<Vector2>();
//		for (final Sphere s : spheres) {
//			checkNeighbors(s, possPositions);
//		}
//
//		Iterator<Vector2> it = possPositions.iterator();
//		while(it.hasNext()) {
//			Vector2 curr = it.next();
//			if(curr.x >= 8 || curr.x <= -1 || curr.y >= 1) {
//				it.remove();
//			}
//		}
//		
//		
//		for(final Vector2 v : possPositions) {
//			if(!uniquePos.contains(v)) {
//				uniquePos.add(v);
//			}
//		}
//		
////		System.out.println("Posizioni vuote..");
////		for(Vector2 pos: uniquePos) {
////			System.out.println(pos);
////		}
//		
//		return uniquePos;
//	}

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

}
