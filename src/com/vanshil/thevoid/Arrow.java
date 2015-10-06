package com.vanshil.thevoid;

/**
 * An Arrow Unit used to indicate a hit or miss.<p>Also used in User-Interface.
 * @author Vanshil Shah
 * @version 1.0
 */
public class Arrow extends Unit{

	boolean hit;
	int timer = 20;

	/**
	 * Arrow Constructor
	 * @param x X-Position
	 * @param y Y-Position
	 * @param path index in GameView.paths
	 * @param hit whether or not there was a hit on this path 
	 */
	public Arrow(float x, float y, int path, boolean hit) {
		super(x, y, path, Unit.type_arrow);
		width *= 1.4;
		height *= 2.5;
		this.hit = hit;
		updatePoints();
	}
	
	/* (non-Javadoc)
	 * @see com.vanshil.thevoid.Unit#updatePoints()
	 */
	@Override
	public void updatePoints (){
		points = new float[7][2];

		final float x1 = x-width/2, y1 = y-height;

		points[0][0] = x1+width/2;
		points[0][1] = y1;

		points[1][0] = x1;
		points[1][1] = (float)(y1  + height* 0.3);

		points[2][0] = (float)(x1  + width * 0.15);
		points[2][1] = (float)(y1  + height * 0.3);

		points[3][0] = (float)(x1  + width * 0.15);
		points[3][1] = y1 + height;

		points[4][0] = (float)(x1 + width * 0.85);
		points[4][1] = y1 + height;

		points[5][0] = (float)(x1 + width * 0.85);
		points[5][1] = (float)(y1 + height * 0.3);

		points[6][0] = x1 + width;
		points[6][1] = (float)(y1 + height * 0.3);
	}

}
