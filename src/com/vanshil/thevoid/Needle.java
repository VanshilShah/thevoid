package com.vanshil.thevoid;

public class Needle extends Unit {

	public Needle (float x, float y, int path){
		super(x, y, path, 1);
		width *= 0.7;
		height *= 1.25;
		attack *= 3;
		defense *= 1;
		health *= 1;
		maxHealth = health;
		range *= 1.5;
		type = 2;
		cost = 1000;
		this.updatePoints();
	}
	@Override
	public void updatePoints(){//Updates the points of the shape
		points = new float[3][2];
		//float height =  (float)(Math.sqrt((width*width)-((width/2)*(width/2))));
		final float x1 = x-width/2, y1 = y-height/2;
		points[0][0] = x1+width/2;
		points[0][1] = y1;

		points[1][0] = x1;
		points[1][1] = y1+height;

		points[2][0] = x1+width;
		points[2][1] = y1+height;


	}
}
