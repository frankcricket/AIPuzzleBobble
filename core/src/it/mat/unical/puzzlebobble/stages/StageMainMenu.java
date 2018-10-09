package it.mat.unical.puzzlebobble.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.helpers.InputHelper;

public class StageMainMenu implements Stage
{
	Sprite background;
	Rectangle buttonPlay;
	Rectangle buttonOptions;
	Rectangle buttonExit;
	Sound push;

	public void load()
	{
		this.background = new Sprite(new Texture("assets/images/new/title_1.png"));
		this.buttonPlay 	= new Rectangle(185f, 380f, 109f, 109f);
		this.buttonOptions 	= new Rectangle(185f, 240f, 109f, 109f);
		this.buttonExit 	= new Rectangle(185f, 100f, 109f, 109f);
		
//		this.push = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/push.wav"));
//		if (Game.getGameInstance().music == null)
//		{
//			Game.getGameInstance().music = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/background.ogg"));
//			Game.getGameInstance().music.setLooping(true);
//			InputHelper.play(Game.getGameInstance().music);
//		}
//		else
//		{
//			Game.getGameInstance().music.stop();
//			InputHelper.play(Game.getGameInstance().music);
//		}
	}

	public void dispose()
	{
		this.push.dispose();
	}
	
	/**
	 * Gestisce le cordinate del click nella fase di avvio del gioco, cioè il menu
	 */
	public void logic(float delta)
	{
		if (InputHelper.justTouched())
		{
			/**
			 * PLAY BUTTON
			 */
			if (this.buttonPlay.contains(InputHelper.touch.x, InputHelper.touch.y))
			{
				InputHelper.play(this.push);
				Game.getGameInstance().changeStage(StagePlay.newInstance());
			}
			/**
			 * OPTION BUTTON
			 */
			if (this.buttonOptions.contains(InputHelper.touch.x, InputHelper.touch.y))
			{
				InputHelper.play(this.push);
				Game.getGameInstance().changeStage(new StageOptions());
			}
			/**
			 * EXIT BUTTON
			 */
			if (this.buttonExit.contains(InputHelper.touch.x, InputHelper.touch.y))
			{
				InputHelper.play(this.push);
				Gdx.app.exit();
			}
		}
	}

	public void draw(SpriteBatch spriteBatch)
	{
		this.background.draw(spriteBatch);
	}
}

 