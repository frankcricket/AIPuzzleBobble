package it.mat.unical.puzzlebobble;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.mat.unical.puzzlebobble.stages.Stage;
import it.mat.unical.puzzlebobble.stages.StageMainMenu;

public class Game implements ApplicationListener {
	public static int GAME_WIDTH = 480;
	public static int GAME_HEIGHT = 800;

	private  static Game game;
	public OrthographicCamera camera;
	public Music music;
	private SpriteBatch spriteBatch;
	private Stage stage;
	
	private Game() {
		game = null;
	}

	public void create() {
		Gdx.input.setCatchBackKey(true);
		spriteBatch = new SpriteBatch();
		stage = new StageMainMenu();
		stage.load();
	}

	public void resize(int width, int height) {
		this.camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
		this.camera.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0.0F);
		this.camera.update();
		this.spriteBatch.setProjectionMatrix(this.camera.combined);
		
	}

	public void render() {
		Gdx.gl.glClearColor(1.0F, 0.0F, 1.0F, 1.0F);
		Gdx.gl.glClear(16384);

		float delta = Gdx.graphics.getDeltaTime() * 1000.0F;
		stage.logic(delta);
		
		spriteBatch.begin();
		stage.draw(this.spriteBatch);
		spriteBatch.end();
	}
	
	public static Game getGameInstance() {
		if(game == null) {
			game = new Game();
		}
		return game;
	}

	public void pause() {
	}

	public void resume() {
	}

	public void dispose() {
	}

	public void changeStage(Stage newStage) {
		this.stage = newStage;
		this.stage.load();
	}
}
