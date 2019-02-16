package it.mat.unical.puzzlebobble.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import it.mat.unical.puzzlebobble.Game;
import it.mat.unical.puzzlebobble.helpers.SettingsHelper;
import it.mat.unical.puzzlebobble.stages.StagePlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sphere extends Sprite {
	public static int Size = 50; //dimensione della sprite

	public static enum Colors {
		Red, Yellow, Blue, Green, White;
	}

	public static enum State {
		Ready, Moving, Stopped;
	}

	public static Color Red = new Color(1.0F, 0.0F, 0.0F, 1.0F);
	public static Color Yellow = new Color(1.0F, 1.0F, 0.0F, 1.0F);
	public static Color Blue = new Color(0.0F, 0.0F, 1.0F, 1.0F);
	public static Color Green = new Color(0.0F, 1.0F, 0.0F, 1.0F);
	public static Color White = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	public static Color[] colors = { Red, Yellow, Blue, Green, White };

	public static Sound destroySound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/glassbreak.wav"));

	private float directionX = 0.0F;

	private float directionY = 0.0F;
	public Vector2 gridPosition = new Vector2();
	private State state = State.Ready;
	public Colors color;

	public Sphere() {
		this(Colors.values()[MathUtils.random(4)]);
	}

	public Sphere(Colors color) {
		super(new Texture("assets/images/sphere.png"));
		if (color == Colors.Red) {
			setColor(Red);
			this.color = Colors.Red;
		}
		if (color == Colors.Yellow) {
			setColor(Yellow);
			this.color = Colors.Yellow;
		}
		if (color == Colors.Blue) {
			setColor(Blue);
			this.color = Colors.Blue;
		}
		if (color == Colors.Green) {
			setColor(Green);
			this.color = Colors.Green;
		}
		if (color == Colors.White) {
			setColor(White);
			this.color = Colors.White;
		}
	}

	public Sphere(Colors color, Vector2 position) {
		super(new Texture("assets/images/sphere.png"));
		if (color == Colors.Red) {
			setColor(Red);
			this.color = Colors.Red;
		}
		if (color == Colors.Yellow) {
			setColor(Yellow);
			this.color = Colors.Yellow;
		}
		if (color == Colors.Blue) {
			setColor(Blue);
			this.color = Colors.Blue;
		}
		if (color == Colors.Green) {
			setColor(Green);
			this.color = Colors.Green;
		}
		if (color == Colors.White) {
			setColor(White);
			this.color = Colors.White;
		}
		this.gridPosition = position;
		fixPosition(true);
	}

	public Colors color() {
		return this.color;
	}

	public void setColor(Colors c) {
		color = c;
	}

	public Vector2 gridPosition() {
		return this.gridPosition;
	}

	public void update(List<Sphere> spheres, float delta) {
		if (this.state == State.Moving) {
			checkCollision(spheres);
			if (getY() >= StagePlay.TOP_BORDER) {
				stop();
				return;
			}
			if (getX() + getWidth() > StagePlay.RIGHT_BORDER) {
				this.directionX *= -1.0F;
			}
			if (getX() < StagePlay.LEFT_BORDER) {
				this.directionX *= -1.0F;
			}
			if (getY() + this.directionY > StagePlay.TOP_BORDER) {
				this.directionY = (StagePlay.TOP_BORDER - getY());
			}
			setPosition(getX() - this.directionX * delta, getY() + this.directionY * delta);
		}
	}

	public void move() {
		this.state = State.Moving;
	}

	public void stop() {
		this.state = State.Stopped;
	}

	public void setDirection(float x, float y) {
		if (this.state != State.Ready) {
			return;
		}
		this.directionX = x;
		this.directionY = y;
	}

	public State state() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	private void nearestGridTile() {
		float positionX = getX() + getWidth() / 2.0F;
		float positionY = getY() + getHeight() / 2.0F - 45.0F;

		int gy = (int) ((positionY - StagePlay.TOP_BORDER - Size / 2) / Size);
		int gx = (int) ((positionX - StagePlay.LEFT_BORDER - gy % 2 * (Size / 2)) / Size);
//		float f=((positionY - StagePlay.TOP_BORDER - Size / 2) / Size);
//		int gy = Math.round(f);
//		int gx = Math.round((positionX - StagePlay.LEFT_BORDER - f % 2 * (Size / 2)) / Size);

		if (gx < 0) {
			gx = 0;
		}
		if (gx > 7) {
			gx = 7;
		}
		this.gridPosition = new Vector2(gx, gy);
	}

	public void fixPosition(boolean set) {
		if (!set) {
			nearestGridTile();
		}
		Vector2 vec = gridPositionToWorld();
		setX(vec.x);
		setY(vec.y);
	}

	private Vector2 gridPositionToWorld() {
		Vector2 vec = new Vector2(0.0F, 0.0F);
		vec.x = (this.gridPosition.x * Size + StagePlay.LEFT_BORDER + this.gridPosition.y % 2.0F * (Size / 2));
		vec.y = (this.gridPosition.y * Size + StagePlay.TOP_BORDER);

		return vec;
	}

	private void checkCollision(List<Sphere> spheres) {
		Iterator<Sphere> it = spheres.iterator();
		while (it.hasNext()) {
			Sphere next = (Sphere) it.next();
			float dx = next.getX() - getX();
			float dy = next.getY() - getY();
			double diff1 = Math.pow(dx, 2.0D) + Math.pow(dy, 2.0D);
			double radioE = Math.pow(Size - 2, 2.0D);
			if (diff1 <= radioE) {
				stop();
			}
		}
	}

	public boolean floating(List<Sphere> spheres) {
		List<Sphere> toCheck = new ArrayList<Sphere>();
		List<Sphere> checked = new ArrayList<Sphere>();

		toCheck.add(this);
		while (toCheck.size() > 0) {
			Sphere sphere = (Sphere) toCheck.get(0);
			checked.add(sphere);
			toCheck.remove(sphere);
			if (sphere.gridPosition.y != 0.0F) {
				List<Sphere> neighbors = sphere.findNeighbors(spheres);
				Iterator<Sphere> it = neighbors.iterator();
				while (it.hasNext()) {
					Sphere sphere2 = (Sphere) it.next();
					if (!checked.contains(sphere2)) {
						toCheck.add(sphere2);
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public List<Sphere> findSimilars(List<Sphere> spheres) {
		List<Sphere> similars = new ArrayList<Sphere>();
		similars.add(this);
		findSimilars(spheres, similars, this);

		return similars;
	}

	public void findSimilars(List<Sphere> spheres, List<Sphere> similars, Sphere current) {
		List<Sphere> neighbors = current.findNeighbors(spheres);

		Iterator<Sphere> it = neighbors.iterator();
		while (it.hasNext()) {
			Sphere sphere = (Sphere) it.next();
			if ((this.color == sphere.color) && (!similars.contains(sphere))) {
				similars.add(sphere);
				findSimilars(spheres, similars, sphere);
			}
		}
	}

	public List<Sphere> findNeighbors(List<Sphere> spheres) {
		List<Sphere> nearSpheres = new ArrayList<Sphere>();

		Iterator<Sphere> it = spheres.iterator();
		while (it.hasNext()) {
			Sphere sphere = (Sphere) it.next();
			if (nextTo(sphere)) {
				nearSpheres.add(sphere);
			}
		}
		return nearSpheres;
	}

	private boolean nextTo(Sphere sphere) {
		Vector2 tmp = new Vector2(0.0F, 0.0F);

		tmp.x = (this.gridPosition.x - Math.abs(this.gridPosition.y) % 2.0F); // top-left
		tmp.y = (this.gridPosition.y + 1.0F);
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		tmp.x = (this.gridPosition.x - Math.abs(this.gridPosition.y) % 2.0F + 1.0F); // top-right
		tmp.y = (this.gridPosition.y + 1.0F);
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		tmp.x = (this.gridPosition.x - 1.0F); // left
		tmp.y = this.gridPosition.y;
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		tmp.x = (this.gridPosition.x + 1.0F); // right
		tmp.y = this.gridPosition.y;
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		tmp.x = (this.gridPosition.x - Math.abs(this.gridPosition.y) % 2.0F); // bottom-left
		tmp.y = (this.gridPosition.y - 1.0F);
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		tmp.x = (this.gridPosition.x - Math.abs(this.gridPosition.y) % 2.0F + 1.0F); // bottom-right
		tmp.y = (this.gridPosition.y - 1.0F);
		if (sphere.gridPosition.equals(tmp)) {
			return true;
		}

		return false;
	}

	public void copy(Sphere sphere) {
		this.directionX = sphere.directionX;
		this.directionY = sphere.directionY;
		this.gridPosition.x = sphere.gridPosition().x;
		this.gridPosition.y = sphere.gridPosition().y;
		this.color = sphere.color;
		setPosition((Game.GAME_WIDTH / 2) - (sphere.getWidth() / 2) - 15, 80);
	}

	public int getColorId() {
		String s_color = this.getColor().toString();
		s_color = s_color.substring(0, 6);
		for (int i = 0; i < colors.length; i++) {
			String palette_color = colors[i].toString();
			palette_color = palette_color.substring(0, 6);
			if (s_color.equals(palette_color))
				return i;
		}
		return 0;
	}
	
	public float getDirectionX() {
		return directionX;
	}

	public void setDirectionX(float directionX) {
		this.directionX = directionX;
	}

	public float getDirectionY() {
		return directionY;
	}

	public void setDirectionY(float directionY) {
		this.directionY = directionY;
	}

	public Vector2 getGridPosition() {
		return gridPosition;
	}

	public void setGridPosition(Vector2 gridPosition) {
		this.gridPosition = gridPosition;
	}

	public State getState() {
		return state;
	}
}
