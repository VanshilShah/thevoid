package com.vanshil.thevoid;

public class Mini extends Unit {

	float radius;

	public Mini (float x, float y, int path){
		super(x, y, path, 0);
		radius = width/4;
		speed *= 2;
		attack *= 1.5;
		defense *= .5;
		health *= .4;
		maxHealth = health;
		type = 3;
		cost = 200;
		this.updatePoints();
	}
	@Override
	public void updatePoints (){//Updates the points of the shape
		points = new float[8][2];

		final float x1 = x-radius, y1 = y-radius;

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
