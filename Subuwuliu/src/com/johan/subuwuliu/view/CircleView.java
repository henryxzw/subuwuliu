package com.johan.subuwuliu.view;

import com.johan.subuwuliu.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
	
	private int color = Color.parseColor("#000000");
	private int padding = 0;

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.circle_view);
		color = Color.parseColor(array.getString(R.styleable.circle_view_circle_color));
		padding = array.getDimensionPixelOffset(R.styleable.circle_view_circle_padding, 0);
		//用完属性应该释放
		array.recycle();
	}

	public CircleView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int centerX = canvas.getWidth() / 2;
		int centerY = canvas.getHeight() / 2;
		int radius = centerX >= centerY ? (centerY - padding) : (centerX - padding);
		//定义画笔
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(centerX, centerY, radius, paint);
	}

}
