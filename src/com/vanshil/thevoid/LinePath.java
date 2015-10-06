package com.vanshil.thevoid;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class LinePath {
	boolean actual = false;
	boolean arrived = false;
	public Unit base;
	int color;
	GameView game;
	public List<Unit> goneUnits = new ArrayList<Unit>();
	public List<Unit> inUnits = new ArrayList<Unit>();

	public List<Unit> outUnits = new ArrayList<Unit>();
	public Point[] points = new Point[4];
	public LinePath(int color, GameView game){
		this.color = color;
		this.game = game;
	}
	public void addInUnit(Unit unit){
		inUnits.add(unit);
	}
	public void addOutUnit(Unit unit){
		outUnits.add(unit);
	}
	public void draw(Canvas canvas, Paint paint){
		paint.setColor(color);
		paint.setStrokeWidth(canvas.getWidth()/15);
		final Path path = new Path();
		path.moveTo(points[0].getCenterX(), points[0].getCenterY());

		for(int i = 1; i < points.length; i++){
			path.lineTo(points[i].getCenterX(), points[i].getCenterY());
		}
		canvas.drawPath(path, paint);
		paint.setStrokeWidth(3);

		paint.setColor(Color.WHITE);
		for(int i = 0; i < outUnits.size(); i++){
			outUnits.get(i).draw(canvas, paint);
		}
		paint.setColor(Color.BLACK);
		for(int i = 0; i < inUnits.size(); i++){
			inUnits.get(i).draw(canvas, paint);
		}
	}



	private Point[] flipArray(Point[] points){
		final Point[] newPoints = new Point[points.length];
		for(int i = 0; i < points.length; i++){
			newPoints[i] = points[points.length-(i+1)];
		}
		return newPoints;
	}
	public Unit getInUnit(int index){
		return inUnits.get(index);
	}
	public Unit getOutUnit(int index){
		return outUnits.get(index);
	}
	private void updateOutUnits(){
		for(int i = 0; i < outUnits.size(); i++){
			final Unit out = outUnits.get(i);
			if(out.health <= 0 || out.arrived){
				if(out.arrived){
					goneUnits.add(outUnits.get(i));
					Log.d("UNIT", "ARRIVED");
				}
				outUnits.remove(i);
				i--;
				continue;
			}
			for(int j = 0; j < inUnits.size(); j++){//checks for intersections with all enemies on the path
				final Unit in = inUnits.get(j);
				if(in.arrived){
					arrived = true;
					game.base.takeDamage(in);
					inUnits.remove(j);
					j--;
					Log.d("Update IN", "Arrived and Removed");
					continue;
				}
				else if(in.health <= 1 ){
					inUnits.remove(j);
					j--;
					Log.d("Update IN", " Died and Removed");
					continue;
				}
				//else if(!(in.getRegion().quickReject(out.getRegion()))){//region to region collision checking
				else if(in.getRect().intersect(out.getRect())){
					out.fighting = true;
					out.fight(in);
				}
				else if(!in.arrived && i == 0){//only updates the enemies once
					in.updateIn(flipArray(points));
					//Log.d("Update IN", "Moving");
				}

				if(out.getRange().contains(in.getRect())){
					out.fireAt(in);
				}
				if(in.getRange().contains(out.getRect())){
					in.fireAt(out);
				}
				in.updateBullets();
			}
			if(!out.arrived && !out.fighting){
				out.updateOut(points);
			}
			out.updateBullets();
			out.fighting = false;

		}
	}
	public void updateUnits(){
		if(outUnits.size() > 0){
			updateOutUnits();
		}
		else{
			for(int i = 0; i < inUnits.size(); i++){
				final Unit in = inUnits.get(i);
				if(in.arrived){
					arrived = true;
					game.base.takeDamage(in);
					inUnits.remove(i);
					i--;
					Log.d("Update IN", "Arrived and Removed");
					continue;
				}
				else if(in.health <= 1 ){
					inUnits.remove(i);
					i--;
					Log.d("Update IN", " Died and Removed");
					continue;
				}
				in.updateIn(flipArray(points));
				//Log.d("Update IN", "Moving");
			}
		}
	}
}
