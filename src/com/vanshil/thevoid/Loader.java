package com.vanshil.thevoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Loader {
	Bitmap[][] bitmaps = new Bitmap[4][3];
	public Loader(Context context){//blue, red, yellow green

		bitmaps[0][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_tank);
		bitmaps[0][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_needle);
		bitmaps[0][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_mini);

		bitmaps[1][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tank);
		bitmaps[1][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_needle);
		bitmaps[1][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_mini);

		bitmaps[2][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_tank);
		bitmaps[2][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_needle);
		bitmaps[2][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_mini);


		bitmaps[3][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_tank);
		bitmaps[3][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_needle);
		bitmaps[3][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_mini);
		Log.d("Loader", "BITMAPS LOADED");
	}
}
