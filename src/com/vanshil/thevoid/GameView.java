package com.vanshil.thevoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class GameView extends GraphicsView{

	boolean drawDummy = false;
	int gold;
	int [] pathColors = {Color.rgb(107, 185, 240),//blue
			Color.rgb(210, 77, 87),//red
			Color.rgb(244, 179, 80),//yellow
			Color.rgb(27, 188, 155)};//green
	
	Arrow[] arrows;
	Base base;
	Dragger[] draggers;
	Dragger dummy;
	GameActivity gameContext;
	LinePath[] paths;
	Point[][] grid;
	Random r;

	public GameView(Context context) {
		super(context);
		gameContext = (GameActivity) context;
	}
	
	@Override
	public void init(){
		GraphicsView.WIDTH = getWidth();
		GraphicsView.HEIGHT = getHeight();
		r = new Random();
		gold = Dragger.cross_cost*2;
		arrows = new Arrow[4];
		initGridPath();
		initDraggers();
		initBase();
		Log.d("init", "init happened");
	}
		private void initBase(){
			final int assignedPath = r.nextInt(4);
			base = new Base(paths[assignedPath].points[3].getCenterX(), paths[assignedPath].points[3].getCenterY(), assignedPath);
		}
		private void initDraggers(){
			draggers = new Dragger[4];
			for(int i = 0; i < draggers.length; i++){
				draggers[i] = new Dragger(GraphicsView.WIDTH-100, GraphicsView.HEIGHT/5*(i+1), i);
			}
		}
		private void initGridPath(){
			final float initX = GraphicsView.WIDTH/5, initY = GraphicsView.HEIGHT/5;
			grid = new Point[4][4];
			paths = new LinePath[4];
			for(int i = 0; i < paths.length; i++){
				paths[i] = new LinePath(pathColors[i], this);
			}
			for(int i = 0; i < grid.length; i++){
				for(int j = 0; j < grid[i].length; j++){
	
					grid[i][j] = new Point(initX*(j), (int)(initY*(i+0.25)));
					paths[j].points[i] = grid[i][j];
					grid[i][j].setPath(paths[j]);
					//Log.d("sCreated", "" + WIDTH + "" + HEIGHT);
				}
			}
		}

	@Override	
	public void screenTouched(float eventX, float eventY) {
		assignDummy(eventX, eventY);
	}
		private void assignDummy(float eventX, float eventY){
		for (final Dragger dragger : draggers) {
			if(dragger.getRect().contains((int)eventX, (int)eventY)){
				dummy = dragger.clone();
				drawDummy = true;
			}
		}
		postInvalidate();
	}
		
	@Override
	public void screenMoved(float eventX, float eventY) {
		if(drawDummy){
			dummy.x = eventX;
			dummy.y = eventY - dummy.fingerOffset;
		}
		postInvalidate();
	}
	
	@Override
	public void screenReleased(float eventX, float eventY) {
		if(drawDummy){
			testDummyWithPoints();
			dummy = new Dragger(0, 0, 0);
			drawDummy = false;
		}
		postInvalidate();
	}
		private void testDummyWithPoints(){
		if(dummy.type == 0 && gold >= Dragger.cross_cost ){
			for(int i = 0; i < grid.length-1; i++){
				for(int j = 0; j < grid[i].length; j++){
					draggerCross(i, j);
				}
			}
		}else if(dummy.type >= 1){
			for(int i = 0; i < grid.length; i++){
				for(int j = 0; j < grid[i].length; j++){
					draggerUnit(i, j);
				}
			}
		}
	}
			private void draggerCross(int i, int j){
				if(grid[i][j].getRect().contains((int)dummy.x, (int)dummy.y) && j<grid[i].length-1){//If dummy was released near this point

					grid[i][j].path.points[i] = grid[i][j+1];
					final LinePath temp = grid[i][j+1].path;

					grid[i][j+1].setPath(grid[i][j].path);
					temp.points[i] = grid[i][j];

					grid[i][j].setPath(temp);
					gold -= Dragger.cross_cost;
				}
			}
			private void draggerUnit(int i, int j){
				if(grid[i][j].getRect().contains((int)dummy.x, (int)dummy.y)){//If dummy was released near this point
					final LinePath path = grid[i][j].path;
					final Point[] points = path.points;
					Unit toAdd = null;
					switch(dummy.type){
					case Unit.type_tank:
						toAdd = new Tank(points[3].getCenterX(), points[3].getCenterY(), j);
						break;
					case Unit.type_needle:
						toAdd = new Needle(points[3].getCenterX(), points[3].getCenterY(), j);
						break;
					case Unit.type_mini:
						toAdd = new Mini(points[3].getCenterX(), points[3].getCenterY(), j);
						break;
					}
					if(gold >= toAdd.cost){
						gold -= toAdd.cost;
						path.addOutUnit(toAdd);
					}
				}
			}
			
	
	@Override
	public void update() {
		if(base.health <= 0){
			//gameContext.endGame();
		}
		updatePaths();
		updateGold();
		updateArrows();
		postInvalidate();
	}
		private void updateArrows(){
			for(final Arrow arrow : arrows){
				if(arrow != null){
					if(arrow.timer > 0){
						arrow.timer --;
						//Log.d("UPDATE ARROWS", arrow.timer + "");
					}
				}
			}
		}
		private void updateGold(){
			gold += r.nextInt(5);
		}
		private void updatePaths(){
			for (final LinePath path : paths) {
				path.updateUnits();
			}
		}
		
	public String messageIn(byte[] message){
		if(message[0] == 101){
			addUnit(message);
		}
		else if(message[0] == 102){
			addArrow(message);
		}

		String text = "";
		for(final byte b : message){
			text += b + " ";
		}
		if(text.length()>0){
			Log.d("MESSAGE", "RECEIVED: "  + text);
		}
		return text;
	}
		private void addArrow(byte[] message){
			for(int i = 0; i < message.length-1; i++){
				switch(message[i + 1]){
				case 1:
					arrows[i] = new Arrow(grid[0][3-i].getCenterX(), grid[0][3-i].getCenterY(), -1, false);
					break;
				case 2:
					arrows[i] = new Arrow(grid[0][3-i].getCenterX(), grid[0][3-i].getCenterY(), -1, true);
					break;
				}
			}
		}
		private void addUnit(byte[] message){
			for(int i = 1; i < message.length; i += 3){
				final LinePath path = grid[0][3-message[i]].path;
				final Point[] points = path.points;
				Unit toAdd = null;
				switch(message[i + 1]){
				case 1:
					toAdd = new Tank(points[0].getCenterX(), points[0].getCenterY(), i);
					break;
				case 2:
					toAdd = new Needle(points[0].getCenterX(), points[0].getCenterY(), i);
					break;
				case 3:
					toAdd = new Mini(points[0].getCenterX(), points[0].getCenterY(), i);
					break;
				}
				//	Log.d("UNIT BUILDING", message[i+2] + "");
				toAdd.health = message[i+2]*toAdd.maxHealth/100;
				path.addInUnit(toAdd);
	
				//Log.d("UNIT ADDED", toAdd.health + "");
			}
		}
	public byte[] messageOutArrows(){
		final byte[] message = new byte[5];
		message[0] = 0;
		for(int i = 0; i < paths.length; i++){
			final LinePath path = paths[i];
			if(path.arrived){
				message[0] = 102;
				int gridIndex = -1;
				Log.d("PATH KNOWS ARRIVED", "IDEK");
				for(int j = 0; j < grid[0].length; j++){
					if(grid[0][j].equals(path.points[0])){
						gridIndex = i;
					}
				}
				if(base.path == i){
					message[gridIndex + 1] = 2;
				}else{
					message[gridIndex + 1] = 1;
				}
				path.arrived = false;
			}
		}
		return message;
	}
	public byte[] messageOutUnits(){
		final List<Unit> goneUnits = new ArrayList<Unit>();
		for(final LinePath path : paths){
			goneUnits.addAll(path.goneUnits);
			path.goneUnits = new ArrayList<Unit>();
		}
		final byte[] message = new byte[goneUnits.size()*3 + 1];
		message[0] = 101;
		for(final Unit unit : goneUnits){
			final int index = goneUnits.indexOf(unit);
			int gridIndex = -1;
			for(int i = 0; i < grid[0].length; i++){
				if(grid[0][i].equals(paths[unit.path].points[0])){
					gridIndex = i;
				}
			}
			message[index * 3 + 1] = (byte)gridIndex;
			message[index * 3 + 2] = (byte)unit.type;
			message[index * 3 + 3] = (byte)((unit.health/unit.maxHealth)*100);
		}

		return message;
	}
	
	@Override
	public void draw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		this.canvas = canvas;
		drawGrid();
		drawPath();
		drawDraggers();
		drawArrows();
	}
		private void drawArrows(){
			for(final Arrow arrow : arrows){
				if(arrow != null){
					if(arrow.timer > 0){
						if(arrow.hit){
							GraphicsView.paint.setColor(pathColors[3]);
						}
						else{
							GraphicsView.paint.setColor(pathColors[1]);
						}
						arrow.draw(canvas, GraphicsView.paint);
					}
				}
			}
		}
		public void drawDraggers(){
			if(drawDummy){
				GraphicsView.paint.setColor(pathColors[dummy.type]);
				dummy.draw(canvas, GraphicsView.paint);
			}
			GraphicsView.paint.setColor(Color.WHITE);
			for(final Dragger dragger : draggers){
				dragger.draw(canvas, GraphicsView.paint);
			}
			GraphicsView.paint.setColor(pathColors[2]);
			GraphicsView.paint.setTextSize((float) (GraphicsView.HEIGHT * 0.040));
			canvas.drawText(gold + "", (float)(GraphicsView.WIDTH * 0.8), (float)(GraphicsView.HEIGHT * 0.1), GraphicsView.paint);
		}
		public void drawGrid(){
			GraphicsView.paint.setColor(Color.GRAY);
			for (final Point[] element : grid) {
				for(int j = 0; j < element.length; j++){
					element[j].draw(canvas, GraphicsView.paint);
				}
			}
		}
		public void drawPath(){
			for (final LinePath path : paths) {
				path.draw(canvas, GraphicsView.paint);
			}
			GraphicsView.paint.setColor(pathColors[base.path]);
			base.draw(canvas, GraphicsView.paint);
		}
		


}
