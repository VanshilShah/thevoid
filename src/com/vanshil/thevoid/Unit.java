package com.vanshil.thevoid;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class Unit {
	
	boolean arrived = false, fighting = false;
	float attack = 10, defense = 10, health = 250, maxHealth = health;
	List<Bullet> bullets = new ArrayList<Bullet>();
	float[][] points;
	float rectWidth, rectHeight;
	Path shapePath;
	int type, path, cost;
	final static int type_mini = 0, type_needle = 1, type_tank = 2, type_base = 4, type_arrow = 5;
	float WIDTH, HEIGHT;
	float x, y, height, width, range, speed;
	float x2, y2, angle2;
	int zone = 3, bulletTimer = 0;
	
	public Unit(float x, float y, int path, int type){
		this.x = x-width/2;
		this.y = y-height/2;
		this.path = path;
		this.type = type;
		this.WIDTH = GraphicsView.WIDTH;
		this.HEIGHT = GraphicsView.HEIGHT;
		height =  GraphicsView.WIDTH/20;
		width =  GraphicsView.WIDTH/20;
		rectWidth = width;
		rectHeight = height;
		range = GraphicsView.WIDTH/2;
		speed = GraphicsView.WIDTH/200;
		shapePath = new Path();
	}
	public void draw(Canvas canvas, Paint paint){//draws each individual line of the shape

		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setAntiAlias(true);

		shapePath = new Path();
		shapePath.setFillType(FillType.EVEN_ODD);
		shapePath.moveTo(points[0][0], points[0][1]);
		for(int i = 1; i < points.length; i++){
			shapePath.lineTo(points[i][0], points[i][1]);
		}
		shapePath.close();

		canvas.drawPath(shapePath, paint);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);

		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(canvas, paint);
		}

	}
	public void fight(Bullet bullet){
		this.health -= bullet.damage - (this.defense/10);
		bullet.parent.bullets.remove(bullet);
	}

	public void fight(Unit enemy){
		this.health -= enemy.attack - (this.defense/10);
		enemy.health -= this.attack - (enemy.defense/10);
	}

	public void fireAt(Unit enemy){
		bulletTimer++;
		if(bulletTimer % 8 == 0){
			bullets.add(new Bullet(this, enemy));
			bulletTimer = 0;
		}
	}
	public Rect getRange(){
		return new Rect((int)(x-range/2), (int)(y-range/2), (int)(x+range/2), (int)(y+range/2));
	}
	public Rect getRect(){
		final RectF rect = new RectF();
		shapePath.computeBounds(rect, true);
		return new Rect((int)(rect.top), (int)(rect.left), (int)(rect.bottom), (int)(rect.right));
	}
	public Region getRegion(){
		final RectF rectF = new RectF();
		shapePath.computeBounds(rectF, true);
		final Region region = new Region();
		region.setPath(shapePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
		return region;
	}
	public void move(){
		if(x < x2){
			x += speed;
		}
		else if(x > x2){
			x -= speed;
		}
		if(y < y2){
			y += speed;
		}
		else if(y > y2){
			y -= speed;
		}
		updatePoints();
		rotatePoints(x, y, (int)angle2);
	}
	private void moveIn(Point[] points){
		Point below = points[zone];
		Point above = points[zone-1];

		if(above.getCenterY()-y < 0 && zone != 0){
			if(zone != 1){
				zone--;
				below = points[zone];
				above = points[zone-1];
			}
			else{
				arrived = true;
				//Log.d("Update Unit", "Arrived");
			}

		}

		final double theta = Math.atan((below.getCenterY()-above.getCenterY())/(below.getCenterX()-above.getCenterX()));

		final float deltaY = (float) (speed * Math.sin(theta));

		y += Math.abs(deltaY);
		x = below.getCenterX() - (float) ((below.getCenterY()-y)/Math.tan(theta));


		updatePoints();

		if(theta<0){
			rotatePoints(x, y, ((int)Math.toDegrees(theta)+270));
		}else{
			rotatePoints(x, y, ((int)Math.toDegrees(theta) + 90));
		}
		//Log.d("Update Unit", "Angle:" + Math.toDegrees(theta) + ", DeltaX:" + deltaX + ", DeltaY:" + deltaY);
	}
	private void moveOut(Point[] points){
		Point below = points[zone];
		Point above = points[zone-1];

		if(y-above.getCenterY() < 0 && zone != 0){
			if(zone != 1){
				zone--;
				below = points[zone];
				above = points[zone-1];
			}
			else{
				arrived = true;
				//Log.d("Update Unit", "Arrived");
			}

		}

		final double theta = Math.atan((below.getCenterY()-above.getCenterY())/(below.getCenterX()-above.getCenterX()));

		final float deltaY = (float) (speed * Math.sin(theta));

		y -= Math.abs(deltaY);
		x = below.getCenterX() - (float) ((below.getCenterY()-y)/Math.tan(theta));


		updatePoints();

		if(theta<0){
			rotatePoints(x, y, -(270 - (int)Math.toDegrees(theta)));
		}else{
			rotatePoints(x, y, (int)Math.toDegrees(theta)-90);
		}
		//Log.d("Update Unit", "Angle:" + Math.toDegrees(theta) + ", DeltaX:" + deltaX + ", DeltaY:" + deltaY);
	}
	public void rotatePoints(float x1, float y1, int degrees){//rotates the points of this shape about point (x1, y1)
		for(int i = 0; i < points.length; i++){
			final float[] newPoints = rotateXandYPoint(x1, y1, points[i][0], points[i][1], degrees);
			points[i][0] = newPoints[0];
			points[i][1] = newPoints[1];
		}
	}

	private float[] rotateXandYPoint(float x1, float y1, float x2, float y2, float degrees){/*rotates point (x2, y2)
			about point (x1, y1)*/
		final float[] arr = new float[2];

		final double rx = x2 - x1;
		final double ry = y2 - y1;
		double angle = (float) Math.atan((y2 - y1)/(x2 - x1));
		if (rx > 0){
			if (ry < 0){
				angle = 2*Math.PI - Math.abs(angle);
			}
			else if(ry == 0){
				angle = 0;
			}
		}
		else if (rx < 0){
			if (ry > 0){
				angle = Math.PI - Math.abs(angle);
			}
			else if (ry < 0){
				angle = Math.PI + Math.abs(angle);
			}
			else if (ry == 0){
				angle = Math.PI;
			}
		}
		if (rx == 0){
			if (ry > 0){
				angle = Math.toRadians(90);
			}
			else if (ry > 0){
				angle = Math.toRadians(270);
			}
		}
		final float r = (float) Math.sqrt((x1 - x2)*(x1 - x2)  + (y1 - y2)*(y1 - y2));

		final double a = Math.toRadians(degrees);
		x2 = (float) (Math.cos(angle+a)*r) + x1;
		y2 = (float) (Math.sin(angle+a)*r) + y1;

		arr[0] = x2;
		arr[1] = y2;

		return arr;
	}
	public void setTarget(float x2, float y2, float angle2){
		this.x2 = x2;
		this.y2 = y2;
		this.angle2 = angle2;
	}

	public void updateBullets(){
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).update();
		}
	}
	public void updateIn(Point[] points){
		moveIn(points);
	}
	public void updateOut(Point[] points){
		moveOut(points);
	}
	public void updatePoints(){

	}
}
