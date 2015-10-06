package com.vanshil.thevoid;


/**
 * A Base Unit, used to represent the players status in the Game
 * @author Vanshil Shah
 * @version 1.0
 */
public class Base extends Unit{
	
	float radius;
	
	/**
	 * Base Constructor
	 * @param x X-Position
	 * @param y Y-Position
	 * @param path index in GameView.paths
	 */
	public Base(float x, float y, int path) {
		super(x, y, path, Unit.type_base);
		health *= 10;
		updatePoints();
	}

	/**
	 * Calculates damage from given unit
	 * @param unit that arrived to the base
	 */
	public void takeDamage(Unit unit){
		if(unit.path == path){
			health -= unit.attack*2 + unit.health;
			updatePoints();
		}
	}

	/* (non-Javadoc)
	 * @see com.vanshil.thevoid.Unit#updatePoints()
	 */
	@Override
	public void updatePoints (){//Updates the points of the shape
		points = new float[8][2];
		radius = health/15;
		final float x1 = x-radius, y1 = y;

		points[0][0] = x1;
		points[0][1] = (float) (y1+(radius*2)*0.3);

		points[1][0] = (float) (x1+(radius*2)*0.3);
		points[1][1] = y1;

		points[2][0] = (float) (x1+(radius*2)*0.7);
		points[2][1] = y1;

		points[3][0] = (x1+(radius*2));
		points[3][1] = (float) (y1+(radius*2)*0.3);

		points[4][0] = (x1+(radius*2));
		points[4][1] = (float) (y1+(radius*2)*0.7);

		points[5][0] = (float) (x1+(radius*2)*0.7);
		points[5][1] = (y1+(radius*2));

		points[6][0] = (float) (x1+(radius*2)*0.3);
		points[6][1] = (y1+(radius*2));

		points[7][0] = x1;
		points[7][1] = (float) (y1+(radius*2)*0.7);

	}
}
