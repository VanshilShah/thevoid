package com.vanshil.thevoid;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class UiView extends GraphicsView{

	Unit[] background = new Unit[10];

	GameActivity context;
	Unit instructions, quickPlay, findFriend, connect;
	int [] pathColors = {Color.rgb(107, 185, 240),//blue
			Color.rgb(210, 77, 87),//red
			Color.rgb(244, 179, 80),//yellow
			Color.rgb(27, 188, 155)};//green
	Random r;
	float theVoidText, quickPlayText, findFriendText;
	public UiView(Context context) {
		super(context);
		this.context = (GameActivity) context;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);


		for(final Unit unit: background){
			if(unit != null){
				GraphicsView.paint.setColor(pathColors[0]);
				GraphicsView.paint.setAlpha(190);
				unit.draw(canvas, GraphicsView.paint);
			}
		}

		GraphicsView.paint.setColor(pathColors[2]);
		GraphicsView.paint.setAlpha(200);
		quickPlay.draw(canvas, GraphicsView.paint);

		GraphicsView.paint.setColor(pathColors[3]);
		GraphicsView.paint.setAlpha(200);

		findFriend.draw(canvas, GraphicsView.paint);

		GraphicsView.paint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/eras.ttf"));
		GraphicsView.paint.setColor(Color.WHITE);
		GraphicsView.paint.setStyle(Paint.Style.FILL_AND_STROKE);
		GraphicsView.paint.setAntiAlias(true);

		GraphicsView.paint.setTextSkewX((float) -0.5);
		GraphicsView.paint.setStrokeWidth(GraphicsView.WIDTH/100);
		GraphicsView.paint.setTextSize(theVoidText);
		canvas.drawText("The", 0, (int)(theVoidText*1.5), GraphicsView.paint);
		canvas.drawText("Void", (int)(theVoidText*1.5), (int)(theVoidText*1.5), GraphicsView.paint);

		GraphicsView.paint.setTextSkewX((float) -0.25);
		GraphicsView.paint.setStrokeWidth(GraphicsView.WIDTH/400);
		GraphicsView.paint.setTextSize(quickPlayText);
		canvas.drawText("QuickPlay!", quickPlay.x, quickPlay.y-GraphicsView.HEIGHT/15, GraphicsView.paint);


		GraphicsView.paint.setTextSize(findFriendText);
		canvas.drawText("Challenge", findFriend.x-GraphicsView.WIDTH/5, findFriend.y, GraphicsView.paint);
		canvas.drawText("Friends!", findFriend.x-GraphicsView.WIDTH/3, findFriend.y + findFriendText, GraphicsView.paint);

	}
	private void genUnits(){
		final int index = r.nextInt(background.length);
		final float x1 = r.nextFloat()*GraphicsView.WIDTH, y1 = r.nextFloat()*GraphicsView.HEIGHT, x2 = r.nextFloat()*GraphicsView.WIDTH, y2 = r.nextFloat()*GraphicsView.HEIGHT;
		final int color = r.nextInt(pathColors.length);
		switch(r.nextInt(3) + 1){
		case 1:
			background[index] = new Tank(x1, y1, color);
			break;
		case 2:
			background[index] = new Needle(x1, y1, color);
			break;
		case 3:
			background[index] = new Mini(x1, y1, color);
			break;
		}
		background[index].speed = 1;
		background[index].setTarget(x2, y2, 360*r.nextFloat());
	}
	@Override
	public void init() {
		r = new Random();
		theVoidText = (float)(GraphicsView.HEIGHT*0.15);
		quickPlayText = (float)(GraphicsView.HEIGHT*0.065);
		findFriendText = (float)(GraphicsView.HEIGHT*0.065);
		initUnitButtons();
		initBackground();
	}

	private void initBackground(){
		for(int i = 0; i < 20; i++){//does not garuntee 20 units will be generated but still pregenerates some.
			genUnits();
		}
	}
	private void initUnitButtons(){
		quickPlay = new Tank((float)(GraphicsView.WIDTH*0.475),(float)(GraphicsView.HEIGHT*0.55), 0);
		quickPlay.width = GraphicsView.HEIGHT/4;
		quickPlay.height = GraphicsView.WIDTH;
		quickPlay.updatePoints();
		quickPlay.rotatePoints(quickPlay.x, quickPlay.y, 60);

		findFriend = new Tank((float)(GraphicsView.WIDTH*0.55), (float)(GraphicsView.HEIGHT*0.75), 0);
		findFriend.width = GraphicsView.HEIGHT/4;
		findFriend.height = GraphicsView.WIDTH;
		findFriend.updatePoints();
		findFriend.rotatePoints(findFriend.x, findFriend.y, 240);
	}

	@Override
	public void screenMoved(float eventX, float eventY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void screenReleased(float eventX, float eventY) {
	}

	@Override
	public void screenTouched(float eventX, float eventY) {
		if(quickPlay.getRegion().contains((int)eventX, (int)eventY)){

		}
		if(findFriend.getRegion().contains((int)eventX, (int)eventY)){
			context.startFriendPicker();
		}
	}

	@Override
	public void update() {
		counter++;
		for(final Unit unit : background){
			if(unit != null){
				unit.move();
				if(counter%150 == 0){
					unit.setTarget(r.nextFloat()*GraphicsView.WIDTH, r.nextFloat()*GraphicsView.HEIGHT, 360*r.nextFloat());
				}
			}
		}
		if(counter%50 == 0){
			genUnits();
		}
		postInvalidate();
	}

}
