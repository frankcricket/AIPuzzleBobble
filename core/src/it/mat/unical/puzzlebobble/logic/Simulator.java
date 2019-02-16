package it.mat.unical.puzzlebobble.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	private Sphere nextSphere;
	private List<Sphere> spheres;

	private final static float DELTA = 5f;

	public Simulator(final List<Sphere> game_spheres, final Sphere curr_sphere, final Sphere next_sphere) {
		cannon = null;
		currSphere = null;
		nextSphere = null;
		spheres = null;
		initObjs(game_spheres, curr_sphere,next_sphere);
	}

	private void initObjs(final List<Sphere> game_spheres, final Sphere curr_sphere, final Sphere next_sphere) {

		initCannon();
		spheres = new ArrayList<Sphere>();
		for (final Sphere elements : game_spheres) {
			Sphere s = new Sphere(elements.color(), elements.gridPosition);
			spheres.add(s);
		}
		currSphere = new Sphere(curr_sphere.color(), curr_sphere.gridPosition);
		nextSphere = new Sphere(next_sphere.color(), next_sphere.gridPosition);
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
			xcannon += .5f;
		}

		final HashMap<Vector2, Float> newPositions = getOrderedValues(pos, points);

		final Vector2 sphereVec = Solver.solve(spheres, newPositions, currSphere,nextSphere);
		float cannonPos = newPositions.get(sphereVec);
		System.out.println("Posizioni: " + cannonPos + " // " + sphereVec);

		StagePlay.stagePlay.setCannonTarget(cannonPos);

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


	private final HashMap<Vector2, Float> getOrderedValues(ArrayList<Vector2> positions, ArrayList<Float> points) {

		HashMap<Vector2, Float> posMap = new HashMap<Vector2, Float>();
		for (int i = 0; i < positions.size(); i++) {
			Vector2 currentPos = positions.get(i);
			if (posMap.containsKey(currentPos))
				continue;
			ArrayList<Float> currVal = new ArrayList<Float>();
			currVal.add(points.get(i));
			for(int j = i+1; j < positions.size(); j++) {
				if(currentPos.equals(positions.get(j))) {
					currVal.add(points.get(j));
				}
			}
			
			int pos = 0;
			float value = 0f;
			int size = currVal.size();
			if(size > 0) {
				Collections.sort(currVal);
				if(size > 3 && (currVal.get(0) > 190 || Math.abs(currVal.get(0) - currVal.get(1)) > 25)) {
					value = currVal.get(3) + 0.5f;
				}
				else {
					pos = size/2;
					value = currVal.get(pos);
				}
				
				if(size == 1){
					continue;
				}
			}
			
//			System.out.println("Values.....  " + currentPos);
//			for(int k = 0 ; k < size; k++) {
//				System.out.println(currVal.get(k));
//			}
			
//			float spread = 0.555f;
//			value = (value < 200) ? (value + spread) : (value - spread);
//			if(value > 300) 
//				value -= spread;
			posMap.put(currentPos, value );
		}

		return posMap;
	}


}
