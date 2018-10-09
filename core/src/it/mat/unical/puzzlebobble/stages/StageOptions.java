package it.mat.unical.puzzlebobble.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.Preferences;
import it.mat.unical.puzzlebobble.helpers.InputHelper;

public class StageOptions implements Stage {
	private Image background;
	private com.badlogic.gdx.scenes.scene2d.Stage stage = new com.badlogic.gdx.scenes.scene2d.Stage();
	
	private static boolean ENABLE_SOUND = true;
	private static boolean ENABLE_MUSIC = true;
	
	private Rectangle home;
	private Rectangle exit;
	

	public void load() {
		
		Gdx.input.setInputProcessor(stage);
		
		home = new Rectangle(0,0,115,115);
		exit = new Rectangle(360, 0, 115, 115);
		
		background = new Image(new Texture("assets/images/new/options_1.png"));
		
		final Image sound_on = new Image(new Texture("assets/images/new/sound_on.png"));
		final Image sound_off = new Image(new Texture("assets/images/new/sound_off.png"));
		
		sound_on.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(ENABLE_SOUND){
					sound_off.setVisible(true);
					sound_on.setVisible(false);
					
					Preferences.effects(false);
				}
				ENABLE_SOUND = false;
			}
		});
		

		sound_off.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!ENABLE_SOUND){
					sound_on.setVisible(true);
					sound_off.setVisible(false);
					Preferences.effects(true);
				}
				ENABLE_SOUND = true;
			}

		});	
		if(ENABLE_SOUND){
			sound_on.setVisible(true);
			sound_off.setVisible(false);
			Preferences.effects(true);
		}else{
			sound_on.setVisible(false);
			sound_off.setVisible(true);
			Preferences.effects(false);
		}
		
		final Image music_on = new Image(new Texture("assets/images/new/music_on.png"));
		final Image music_off = new Image(new Texture("assets/images/new/music_off.png"));
		
		music_on.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(ENABLE_MUSIC){
					music_off.setVisible(true);
					music_on.setVisible(false);
					Preferences.music(false);
				}
				ENABLE_MUSIC = false;
			}
		});
		

		music_off.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!ENABLE_MUSIC){
					music_on.setVisible(true);
					music_off.setVisible(false);
					Preferences.music(true);
				}
				ENABLE_MUSIC = true;
			}

		});	
		if(ENABLE_MUSIC){
			music_on.setVisible(true);
			music_off.setVisible(false);
			Preferences.music(true);
		}else{
			music_on.setVisible(false);
			music_off.setVisible(true);
			Preferences.music(false);
		}

		sound_on.setPosition(100,450);
		sound_off.setPosition(100,450);
		music_on.setPosition(250, 450);
		music_off.setPosition(250, 450);
		stage.addActor(background);
		stage.addActor(sound_on);
		stage.addActor(sound_off);
		stage.addActor(music_on);
		stage.addActor(music_off);
		
		
	}

	public void dispose() {
	}

	public void logic(float delta) {
		if (InputHelper.justTouched()) {
			if (home.contains(InputHelper.touch.x, InputHelper.touch.y)) {
				Game.getGameInstance().changeStage(new StageMainMenu());
			}
			if (exit.contains(InputHelper.touch.x, InputHelper.touch.y)) {
				Gdx.app.exit();
			}
		}
	}

	public void draw(SpriteBatch spriteBatch) {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
}
