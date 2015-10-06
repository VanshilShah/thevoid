package com.vanshil.thevoid;

import android.content.Context;
import android.graphics.Canvas;

public class InstructionView extends GraphicsView{

	Arrow back;
	float textSize;
	public InstructionView(Context context) {
		super(context);
	}

	@Override
	public void draw(Canvas canvas) {

	}

	@Override
	public void init() {
		textSize = (float)(GraphicsView.HEIGHT*0.065);
		back = new Arrow(0, 0, 0, true);
	}

	@Override
	public void screenMoved(float eventX, float eventY) {

	}

	@Override
	public void screenReleased(float eventX, float eventY) {

	}

	@Override
	public void screenTouched(float eventX, float eventY) {

	}

	@Override
	public void update() {

	}

}
