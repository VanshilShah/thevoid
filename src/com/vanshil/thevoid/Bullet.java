package com.vanshil.thevoid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
/**
 * The bullet that fires from one unit to another.
 * @author Vanshil Shah
 * @version 1.0
 */
public class Bullet {
	
	float x, y;
	float damage;
	float speed = 20;
	Unit parent, target;
	
	/**
	 * Bullet Constructor
	 * @param parent the Unit that fired the bullet
	 * @param target the Unit that the bullet is moving towards
	 */
	public Bullet(Unit parent, Unit target){
		this.x = parent.x;
		this.y = parent.y;
		this.damage = parent.attack/5;
		this.parent = parent;
		this.target = target;
	}
	
	/**
	 * Draws the Bullet
	 * @param canvas Canvas on which to draw the Bullet
	 * @param paint Paint with which to paint the Bullet
	 */
	public void draw(Canvas canvas, Paint paint){
		canvas.drawCircle(x, y, damage, paint);
	}
	
	/**
	 * @return the bounding rectangle of the Bullet
	 */
	public Rect getRect(){
		return new Rect((int)x, (int)y, (int)(x+damage), (int)(y+damage));
	}
	
	/**
	 *	Moves the Bullet towards its Target
	 */
	private void move(){
		if(target.x > x){
			x += speed;
		}
		else if(target.x < x){
			x -= speed;
		}

		if(target.y > y){
			y += speed;
		}
		else if(target.y < y){
			y -= speed;
		}
	}
	
	/**
	 * All necessary functions to update the Bullet(move, delete, fight).
	 */
	public void update(){
		if(target.health > 0){
			move();
		}
		else{
			parent.bullets.remove(this);
		}
		if(this.getRect().intersect(target.getRect())){
			target.fight(this);
		}
	}
}
