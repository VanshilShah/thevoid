package com.vanshil.thevoid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class GraphicsActivity extends Activity {

	public static float WIDTH, HEIGHT;
	GraphicsView current;
	FrameLayout	 layout;
	boolean running = false;

	public void addView(View view){
		layout.addView(view);
	}

	@SuppressLint("InlinedApi")
	public void immersive(){
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = new FrameLayout(this);
		setContentView(layout);
	}
	@Override
	protected void onPause(){
		super.onPause();
		running = false;
		Log.d("THREAD", "PAUSED");
	}
	@Override
	protected void onResume(){
		super.onResume();
		immersive();//sets the app into full screen Immersive FullScreen Mode.
		run(current);
		running = true;
		Log.d("THREAD", "RESUMED");
	}

	@Override
	protected void onStart(){
		super.onStart();

	}


	public void run(final GraphicsView graphicsView){//Nested threads. one thread sleeps 50 ms to make sure that updates are the same on the phones of both opponents
		current = graphicsView;
		final Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run(){
				final Canvas canvas = null;
				while (running){
					update(canvas, graphicsView);
				}
			}
		});
		thread.start();
		Log.d("THREAD", "STARTED");
	}

	public void setView(GraphicsView GView){
		layout.removeAllViews();
		layout.addView(GView);
		current = GView;
	}
	public void update(Canvas canvas, GraphicsView graphicsView){
		canvas = null;
		try{
			if(graphicsView.init){
				Thread.sleep(50);
				canvas = graphicsView.getHolder().lockCanvas();
				graphicsView.update();
				if(graphicsView.counter%2000000000 == 0){
					graphicsView.counter = 0;
				}else{
					graphicsView.counter++;
				}
			}
		} catch (final InterruptedException e) {

			e.printStackTrace();
		}
		finally {
			if (canvas != null) {
				graphicsView.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
}
