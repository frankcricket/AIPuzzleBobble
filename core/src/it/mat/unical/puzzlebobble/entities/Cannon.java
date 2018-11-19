package it.mat.unical.puzzlebobble.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.helpers.InputHelper;
import it.mat.unical.puzzlebobble.helpers.MathHelper;

public class Cannon {
	private float rotateSpeed = 0.1F;
	private float shootSpeed = 0.6F;
	private float targetAngle = 0.0F;
	public Vector2 targetVector = Vector2.Zero;
	private Sprite base;
	private Sprite rotor;
	private Sprite arrow;
	private Sound shootingSound;
	private Sound hammeringSound;

	public Cannon() {
		this.shootingSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/8bithurt.wav"));
		this.hammeringSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/hammerclick.wav"));

		this.base = new Sprite(new Texture(Gdx.files.internal("assets/images/new/cannon-base.png")));
		this.rotor = new Sprite(new Texture(Gdx.files.internal("assets/images/new/cannon-rotor.png")));
		this.arrow = new Sprite(new Texture(Gdx.files.internal("assets/images/cannon-arrow.png")));
		this.rotor.setOrigin(this.rotor.getWidth() / 2.0F, this.rotor.getHeight() / 2.0F);
		this.rotor.setScale(0.85F);

		this.arrow.setOrigin(this.arrow.getWidth() / 2.0F, this.arrow.getHeight() / 3.0F);
	}

	public boolean update(float delta) {
		float currentAngle = arrow.getRotation();
		if (currentAngle != targetAngle) {
//			System.out.println("cannon update " + currentAngle + " :angle: " + targetAngle);
			if (Math.abs(targetAngle - currentAngle) < rotateSpeed * delta) {
				arrow.setRotation(targetAngle);
				return false;
			}
			if (this.targetAngle > currentAngle) {
				rotateLeft(rotateSpeed * delta);
			} else {
				rotateRight(rotateSpeed * delta);
			}
			return true;
		}
		return false;
	}

	public void setPosition(float x, float y) {
		float x2 = x - 15 - rotor.getWidth() / 2.0F;
		float x3 = x - 15 - arrow.getWidth() / 2.0F;
		float x4 = x + 25 - base.getWidth() / 2.0F;
		rotor.setPosition(x2, y + 12);
		arrow.setPosition(x3, y + 50f);
		base.setPosition(x4, y);
	}

	public void rotateRight(float degrees) {
		float darrow = arrow.getRotation();
		if (darrow < -84.0F) {
			return;
		}
		float drotor = rotor.getRotation();

		this.rotor.setRotation(drotor - degrees * 3.0F);
		this.arrow.setRotation(darrow - degrees);
	}

	public void rotateLeft(float degrees) {
		float darrow = this.arrow.getRotation();
		if (darrow > 84.0F) {
			return;
		}
		float drotor = this.rotor.getRotation();

		this.rotor.setRotation(drotor + degrees * 3.0F);
		this.arrow.setRotation(darrow + degrees);
	}

	public void target(float x, float y) {
		this.targetVector.x = x;
		this.targetVector.y = y;

		float ax = this.arrow.getX() + this.arrow.getOriginX();
		float ay = this.arrow.getY() + this.arrow.getOriginY();
		this.targetAngle = ((float) MathHelper.angle(ax, ay, x, y));
	}
	
	public void simulationTarget(float x, float y) {
		this.targetVector.x = x;
		this.targetVector.y = y;

		float ax = this.arrow.getX() + this.arrow.getOriginX();
		float ay = this.arrow.getY() + this.arrow.getOriginY();
		this.targetAngle = ((float) MathHelper.angle(ax, ay, x, y));
		
		arrow.setRotation(targetAngle);
		
	}

	public void shoot(Sphere sphere) {
		if (sphere.state() == Sphere.State.Ready) {
			InputHelper.play(this.shootingSound);
			float offsety = MathHelper.offsetY(this.arrow.getRotation(), this.shootSpeed);
			float offsetx = MathHelper.offsetX(this.arrow.getRotation(), this.shootSpeed);
			sphere.setDirection(offsety, offsetx);
			sphere.move();
		}

	}

	public final Vector2 getTargetVector() {
		return targetVector;
	}

	public void draw(SpriteBatch spriteBatch) {
		this.base.draw(spriteBatch);
		this.arrow.draw(spriteBatch);
		this.rotor.draw(spriteBatch);
	}

	public void reset() {
		this.arrow.setRotation(0.0F);
		this.rotor.setRotation(0.0F);
	}

	public void dispose() {
		this.shootingSound.dispose();
		this.hammeringSound.dispose();
	}
}
