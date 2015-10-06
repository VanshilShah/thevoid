package com.vanshil.thevoid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class GraphicsView extends SurfaceView implements SurfaceHolder.Callback{

	public static float HEIGHT;
	public static Paint paint = new Paint();
	public static float WIDTH;
	public Canvas canvas = new Canvas();
	public GraphicsActivity context;
	public int counter;

	boolean init = false;

	public GraphicsView(Context context) {
		super(context);
		this.context = (GraphicsActivity) context;
		this.setBackgroundColor(Color.BLACK);
		GraphicsView.paint.setAntiAlias(true);
		GraphicsView.paint.setStrokeWidth(6f);
		GraphicsView.paint.setStyle(Paint.Style.STROKE);
		GraphicsView.paint.setStrokeJoin(Paint.Join.ROUND);
		getHolder().addCallback(this);
	}

	@Override
	public abstract void draw(Canvas canvas);
	public abstract void init();
	@Override
	protected void onDraw(Canvas canvas){
		draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final float eventX = event.getX();
		final float eventY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			screenTouched(eventX, eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
			screenMoved(eventX, eventY);
			break;
		case MotionEvent.ACTION_UP:
			screenReleased(eventX, eventY);
			break;
		default:
			return false;
		}
		return true;
	}

	public abstract void screenMoved(float eventX, float eventY);
	public abstract void screenReleased(float eventX, float eventY);
	public abstract void screenTouched(float eventX, float eventY);

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
		this.setWillNotDraw(false);

		GraphicsView.WIDTH = getWidth();
		GraphicsView.HEIGHT = getHeight();

		if(!init){
			init();
			init = true;
		}

		context.running = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder){

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder){

	}

	public abstract void update();




}
