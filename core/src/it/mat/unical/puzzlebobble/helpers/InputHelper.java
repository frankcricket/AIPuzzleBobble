package it.mat.unical.puzzlebobble.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

import it.mat.unical.puzzlebobble.Game;

public class InputHelper
{
	public static Vector3 touch = Vector3.Zero;
	
	public static boolean justTouched()
	{
		if (Gdx.input.justTouched())
		{
			touch.x = Gdx.input.getX();
			touch.y = Gdx.input.getY();
			Game.getGameInstance().camera.unproject(touch);
			
			return touch.x >= 0 && touch.x <= Game.GAME_WIDTH && touch.y >=0 && touch.y <= Game.GAME_HEIGHT;
		}
		return false;
	}
	public static boolean readyToPlay() {
		if (Gdx.input.isTouched())
		{
			float x,y;
			x = Gdx.input.getX();
			y = Gdx.input.getY();
//			System.out.println(x+ " " + y);
			return (x >= 178 && x <= 282 &&
					y >= 260 && y <= 320);
		}
		return false;
	}

	public static void play(Sound sound)
	{
//		if (Preferences.effects())
//		{
//			sound.play();
//		}
	}

	public static void play(Music music)
	{
//		if (Preferences.music())
//		{
//			music.play();
//		}
	}
	private static boolean inGame() {
		float x = Gdx.input.getX();
		float y = Gdx.input.getY();
		return (x >= 0 && x <= Game.GAME_WIDTH && y >=0 && y <= Game.GAME_HEIGHT);
	}
}