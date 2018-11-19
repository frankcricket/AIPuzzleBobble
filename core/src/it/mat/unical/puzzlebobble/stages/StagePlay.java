package it.mat.unical.puzzlebobble.stages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.Preferences;
import it.mat.unical.puzzlebobble.RoundLoader;
import it.mat.unical.puzzlebobble.Score;
import it.mat.unical.puzzlebobble.entities.Cannon;
import it.mat.unical.puzzlebobble.entities.Smoke;
import it.mat.unical.puzzlebobble.entities.Sphere;
import it.mat.unical.puzzlebobble.entities.Sphere.Colors;
import it.mat.unical.puzzlebobble.helpers.InputHelper;
import it.mat.unical.puzzlebobble.helpers.SettingsHelper;
import it.mat.unical.puzzlebobble.logic.Simulator;

public class StagePlay implements Stage {
	
	public static StagePlay stagePlay = null;
	
	public static int LEFT_BORDER = 37;
	public static int RIGHT_BORDER = 440;
	public static int TOP_BORDER = 615;
	public static int BOTTOM_BORDER = 35;
	public static int DEADEND = 185;
	public static int MAX_SPHERES = -9; // fixed top

	private enum State {
		Prestart,
		Waiting,
		Beginning,
		Playing,
		Lost,
		Won
	};

	private static int MAX_ROUNDS = 5;
	private static int POINTS_PER_SPHERE = 10;

	private State state;
	private int round = 1;
	private RoundLoader loader;
	private Sprite background;
	private Sprite deadend;
	private Sprite youlose;
	private Sprite ready;
	private Sprite go;
	private Sprite prestart;
	private BitmapFont font;
	private List<Smoke> smokes;
	private List<Sphere> spheres;
	private Cannon cannon;
	private Sphere currentSphere;
	private Sphere nextSphere;
	private Score score;
	private Sound boo;
	private float counter = 0.0f;
	
	
	@Override
	public void load() {
		this.font = new BitmapFont();
		font.getData().setScale(5f);
		this.boo = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/boo.ogg"));
		this.background = new Sprite(new Texture("assets/images/new/playscreen1.png"));
		this.deadend = new Sprite(new Texture("assets/images/new/deadend.png"));
		this.deadend.setPosition(LEFT_BORDER - 5, DEADEND);

		this.youlose = new Sprite(new Texture("assets/images/youlose.png"));
		this.youlose.setPosition(Game.GAME_WIDTH / 2 - (this.youlose.getWidth() / 2),
									Game.GAME_HEIGHT / 2 - (this.youlose.getHeight() / 2));
		this.ready = new Sprite(new Texture("assets/images/ready.png"));
		this.ready.setPosition(Game.GAME_WIDTH / 2 - (this.ready.getWidth() / 2),
									Game.GAME_HEIGHT / 2 - (this.ready.getHeight() / 2));
		this.go = new Sprite(new Texture("assets/images/go.png"));
		this.go.setPosition(Game.GAME_WIDTH / 2 - (this.go.getWidth() / 2),
									Game.GAME_HEIGHT / 2 - (this.go.getHeight() / 2));
		
		//bottone per far partire il livello
		this.prestart = new Sprite(new Texture("assets/images/go.png"));
		this.prestart.setPosition(30,398);

		this.score = new Score(20, 785);
		this.cannon = new Cannon();
		this.cannon.setPosition(Game.GAME_WIDTH / 2, 0);
		
		this.loader = new RoundLoader();
		this.smokes = new ArrayList<Smoke>();
		Smoke.preloadImages();
		this.state = State.Prestart;
		this.nextRound();
	}

	@Override
	public void dispose() {
		Sphere.destroySound.dispose();
		this.boo.dispose();
		this.cannon.dispose();
	}

	/*
	 * TODO tempo di attesa
	 */
	private void beginning(float delta) {
		this.counter += delta / 1000;
		if (this.counter > 3.0f) {
//			this.state = State.Playing;
			
		}
	}
	
	private void playing(float delta) {
		if (this.currentSphere.state() == Sphere.State.Stopped) {
			boolean brokeFlag = false;
			this.currentSphere.fixPosition(false);
			List<Sphere> similars = this.currentSphere.findSimilars(this.spheres);
			this.spheres.add(this.currentSphere);
			if (similars.size() > 2) {
				brokeFlag = true;
				InputHelper.play(Sphere.destroySound);
				this.score.addPoints(similars.size() * POINTS_PER_SPHERE);
				this.smokeOnDestroyedSpheres(similars);
				
				this.spheres.removeAll(similars);
				// Delete floating ones
				List<Sphere> floatingSpheres = new ArrayList<Sphere>();
				Iterator<Sphere> it = this.spheres.iterator();
				while (it.hasNext()) {
					Sphere sphere = it.next();
					if (sphere.floating(spheres)) {
						floatingSpheres.add(sphere);
					}
				}
				if (floatingSpheres.size() > 0) {
					this.smokeOnDestroyedSpheres(floatingSpheres);
					this.spheres.removeAll(floatingSpheres);
					this.score.addPoints((int) Math.pow(2, floatingSpheres.size() - 1) * 10);
				}
			}
			/*Controllo "hai vinto"*/
			if (this.spheres.size() == 0) {
				this.currentSphere = null;
				this.nextSphere = null;
				this.score.addPoints(this.score.secondsLeft() * 1000);
				this.state = State.Won;
				return;
			}
			/*Controllo "hai perso"*/
			if (brokeFlag == false) {
				if (this.currentSphere.gridPosition().y <= MAX_SPHERES) {
					InputHelper.play(this.boo);
					this.state = State.Lost;
					return;
				}
			}
			this.nextSphere();
			
			state = State.Waiting;
			return;
		}
		if(currentSphere.state() == Sphere.State.Ready)	{
			if(!cannon.update(delta)) {
				System.out.println(cannon.getTargetVector());
				cannon.shoot(currentSphere);
			}
		}
		currentSphere.update(spheres, delta);
	}

	@Override
	public void logic(float delta) {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Game.getGameInstance().changeStage(new StageMainMenu());
		}
		// update animations
		Iterator<Smoke> it = this.smokes.iterator();
		while (it.hasNext()) {
			if (it.next().update(delta) == true) {
				it.remove();
			}
		}
		
		switch(this.state) {
		case Beginning:{
			
			break;			
		}
		case Prestart:{
			if(InputHelper.readyToPlay()) {
				new Simulator(spheres,currentSphere).start();
				state = State.Beginning;
			}
			break;
		}
		case Playing:{
			playing(delta);
			break;
		}
		case Lost:{
			lost(delta);
			break;
		}
		case Won:{
			won(delta);
			break;
		}
		case Waiting:{
			new Simulator(spheres,currentSphere).start();
			state = State.Beginning;
			break;
		}
		default:
			break;
		}

	}
	
	@Override
	public void draw(SpriteBatch spriteBatch) {
		this.background.draw(spriteBatch);
		this.score.draw(spriteBatch);
		this.deadend.draw(spriteBatch);
		Iterator<Sphere> it1 = this.spheres.iterator();
		while (it1.hasNext()) {
			it1.next().draw(spriteBatch);
		}
		Iterator<Smoke> it2 = this.smokes.iterator();
		while (it2.hasNext()) {
			it2.next().draw(spriteBatch);
		}
		cannon.draw(spriteBatch);
		if (this.currentSphere != null) {
			this.currentSphere.draw(spriteBatch);
		}
//		if (this.currentSimSphere != null) {
//			this.currentSimSphere.draw(spriteBatch);
//		}
		if (this.nextSphere != null) {
			this.nextSphere.draw(spriteBatch);
		}
		if(this.state == State.Prestart) {
			this.prestart.draw(spriteBatch);
		}


//		if (this.state == State.Beginning) {
//			if (this.counter < 2.0f) {
//				this.ready.draw(spriteBatch);
//			}
//			if (this.counter > 2.0f) {
//				this.go.draw(spriteBatch);
//			}
//		}
		if (this.state == State.Lost) {
			this.youlose.draw(spriteBatch);
		}

	}

	private void newSphere() {
		this.nextSphere = new Sphere(this.ingameColor());
		this.nextSphere.setPosition((Game.GAME_WIDTH / 2) - (this.currentSphere.getWidth() / 2) - 30 , 10);
	}

	private void nextSphere() {
		this.nextSphere.setPosition((Game.GAME_WIDTH / 2) - (this.currentSphere.getWidth() / 2) - 15
									, 80);
		this.currentSphere = this.nextSphere;
		this.newSphere();
	}

	private void nextRound() {
		this.cannon.reset();
		this.spheres = this.loader.loadRound(this.round);
		this.currentSphere = new Sphere(this.loader.loadRoundColor(this.round));
		this.currentSphere.setPosition((Game.GAME_WIDTH / 2) - (this.currentSphere.getWidth() / 2) - 15
										, 80);
		this.newSphere();
	}

	/**
	 * Verifica i colori presenti nel gioco
	 * @return color un colore random tra quelli presenti
	 */
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

	private void smokeOnDestroyedSpheres(List<Sphere> spheres) {
		Iterator<Sphere> it = spheres.iterator();
		while (it.hasNext()) {
			Sphere s = it.next();
			Smoke smoke = new Smoke(s.getX(), s.getY());
			this.smokes.add(smoke);
		}
	}
	
	private void lost(float delta) {
		if (InputHelper.justTouched()) {
			Game.getGameInstance().changeStage(new StageMainMenu());
		}
	}

	private void won(float delta) {
		if (round < MAX_ROUNDS) {
			this.round++;
			this.nextRound();
			this.score.setRound(this.round);
			this.score.resetTime();
			this.state = State.Prestart;
		} else {
			// end game state
			if (this.round >= MAX_ROUNDS) {
				if (InputHelper.justTouched()) {
					this.round++;
				}
			}
			if (this.round > MAX_ROUNDS + 1) {
				if (InputHelper.justTouched()) {
					if (this.score.points() > Preferences.maxScore()) {
						Preferences.maxScore(this.score.points());
					}
					Game.getGameInstance().changeStage(new StageMainMenu());
				}
			}
		}

	}
	
	public static StagePlay newInstance() {
		return (stagePlay = new StagePlay());
	}
	public static StagePlay getInstance() {
		return stagePlay;
	}
	public List<Sphere> getSpheres() {
		return spheres;
	}
	
	public void setPlayingSate() {
		this.state = State.Playing;
	}
	
	public void setCannonTarget(float target) {
		cannon.target(target, SettingsHelper.START_Y_CANNON_SIMULATION);
	}
}
