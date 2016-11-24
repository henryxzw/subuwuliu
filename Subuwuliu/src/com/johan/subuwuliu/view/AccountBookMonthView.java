package com.johan.subuwuliu.view;

import com.johan.subuwuliu.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AccountBookMonthView extends View {
	
	private String month;
	private Paint paint;
	
	private Context context;

	public AccountBookMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		this.context = context;
	}

	public AccountBookMonthView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		this.context = context;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int height = context.getResources().getDimensionPixelOffset(R.dimen.accountbook_current_month_height) - 5;
		int monthTextSize = context.getResources().getDimensionPixelOffset(R.dimen.accountbook_current_month_text_size);
		int monthTextWidth = context.getResources().getDimensionPixelOffset(R.dimen.accountbook_current_month_text_width);
		int monthTipSize = context.getResources().getDimensionPixelOffset(R.dimen.accountbook_current_month_tip_size);
		int monthTipWidth = context.getResources().getDimensionPixelOffset(R.dimen.accountbook_current_month_tip_width);
		paint.setColor(Color.WHITE);
		paint.setTextSize(monthTextSize);
		paint.setStrokeWidth(monthTextWidth);
		canvas.drawText(month, 0, height, paint);
		float nextStartX = paint.measureText(month) + 10;
		paint.setTextSize(monthTipSize);
		paint.setStrokeWidth(monthTipWidth);
		canvas.drawText("月", nextStartX, height, paint);
	}
	
	public void setMonth(String month) {
		this.month = month;
		invalidate();
	}
	
}
