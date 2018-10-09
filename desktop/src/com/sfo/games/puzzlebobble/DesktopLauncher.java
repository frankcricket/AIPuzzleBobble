package com.sfo.games.puzzlebobble;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import it.mat.unical.puzzlebobble.Game;

public class DesktopLauncher {
	public static void main(String[] args) {
		new LwjglApplication(Game.getGameInstance(), "Game", 480, 800);
	}
}