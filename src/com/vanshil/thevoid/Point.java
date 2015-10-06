package com.vanshil.thevoid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Point {

	LinePath path =  null;//Identifies which path this point is attached to.

	float x, y, width, height;
	public Point(float x, float y){
		this.x = x;
		this.y = y;
		width = GraphicsView.WIDTH/5;
		height = GraphicsView.HEIGHT/5;
	}

	public void draw(Canvas canvas, Paint paint){
		canvas.drawCircle(getCenterX(), getCenterY(), 20, paint);
		//canvas.drawRect(getRect(), paint);
	}
	public float getCenterX(){
		return x + width/2;
	}
	public float getCenterY(){
		return y + height/2;
	}
	public Rect getRect(){
		return new Rect((int)x, (int)y, (int)(x+width), (int)(y+height));
	}

	public void setPath(LinePath path) {
		this.path = path;
	}
}
