package com.vanshil.thevoid;

public class Tank extends Unit {

	public Tank (float x, float y, int path){
		super(x, y, path, 3);
		width *= 1.25;
		height *= 1.5;
		attack *= .75;
		defense *= 5;
		health *= 4;
		maxHealth = health;
		range *= 0.5;
		type = 1;
		cost = 800;
		this.updatePoints();

	}
	@Override
	public void updatePoints(){//Updates the points of the shape

		points = new float[5][2];

		final float x1 = x-width/2, y1 = y-height/2;

		points[0][0] = (float)(x1+width*0.25);
		points[0][1] = y1;

		points[1][0] = x1;
		points[1][1] = (float)(y1+height*.3);

		points[2][0] = x1+width/2;
		points[2][1] = y1+height;

		points[3][0] = x1+width;
		points[3][1] = (float)(y1+height*.3);

		points[4][0] = (float)(x1+width*0.8);
		points[4][1] = y1;
	}
}
