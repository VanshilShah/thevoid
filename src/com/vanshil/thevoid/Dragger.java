package com.vanshil.thevoid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Dragger used to drag Units or Crossers onto the Paths.
 * @author Vanshil Shah
 * @version 1.0
 */
public class Dragger {
	final static int type_cross = -1;
	final static int cross_cost = 5000;
	int type;
	float fingerOffset = 200;
	float x,  y, radius;
	
	/**
	 * @param x X-Position
	 * @param y Y-Position
	 * @param type Distinguish between 	Dragger.type_cross,
	 * 									Dragger.type_mini,
	 * 									Dragger.type_needle,
	 * 									Dragger.type_tank
	 */
	public Dragger(float x, float y, int type){
		this.x = x;
		this.y = y;
		this.type = type;
		radius = GraphicsView.WIDTH/10;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Dragger clone(){
		return new Dragger(this.x, this.y, this.type);
	}
	
	/**
	 * Draws the Dragger depending on its type
	 * @param canvas Canvas on which to draw the Bullet
	 * @param paint Paint with which to paint the Bullet
	 */
	public void draw(Canvas canvas, Paint paint){
		Unit toDraw = new Unit(x, y, 0, 0);
		switch (type){
		case type_cross:
			canvas.drawLine(x-radius/2, y-radius/2, x+radius/2, y+radius/2, paint);
			canvas.drawLine(x+radius/2, y-radius/2, x-radius/2, y+radius/2, paint);
			canvas.drawText(Dragger.cross_cost + "", x-radius, y+radius, paint);
			break;
		case Unit.type_mini:
			toDraw = new Mini(x, y, 0);
			toDraw.draw(canvas, paint);
			canvas.drawText(toDraw.cost + "", x-radius, y+radius, paint);
			break;
		case Unit.type_needle:
			toDraw = new Needle(x, y, 0);
			toDraw.draw(canvas, paint);
			canvas.drawText(toDraw.cost + "", x-radius, y+radius, paint);
			break;
		case Unit.type_tank:
			toDraw = new Tank(x, y, 0);
			toDraw.draw(canvas, paint);
			canvas.drawText(toDraw.cost + "", x-radius, y+radius, paint);
			break;
		}
	}
	
	/**
	 * @return the bounding rectangle of the Bullet
	 */
	public Rect getRect(){
		return new Rect((int)(x-radius), (int)(y-radius), (int)(x+radius), (int)(y+radius));
	}
}
