package com.dk.view.drop;

import com.dk.view.drop.DropCover.OnDragCompeteListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class WaterDrop extends RelativeLayout {
    private Paint mPaint = new Paint();
    private TextView mTextView;
    private DropCover.OnDragCompeteListener mOnDragCompeteListener;
    private boolean mHolderEventFlag;
    
    private boolean isTouch = false;

    public WaterDrop(Context context) {
        super(context);
        init();
    }

    public WaterDrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setText(String str) {
        mTextView.setText(str);
    }

    public void setTextSize(int size) {
        mTextView.setTextSize(size);
    }
    
    public void reset() {
    	CoverManager.getInstance().reset();
   } 

    @SuppressLint("NewApi")
    private void init() {
        mPaint.setAntiAlias(true);
        if (VERSION.SDK_INT > 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mTextView = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTextView.setTextSize(13);
        mTextView.setTextColor(0xffffffff);
        mTextView.setLayoutParams(params);
        addView(mTextView);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        mPaint.setColor(Color.parseColor("#EF8B2B"));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewGroup parent = getScrollableParent();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	isTouch = true;
            mHolderEventFlag = !CoverManager.getInstance().isRunning();
            if (mHolderEventFlag) {
                if (parent != null)
                    parent.requestDisallowInterceptTouchEvent(true);
                CoverManager.getInstance().start(this, event.getRawX(), event.getRawY(), mOnDragCompeteListener);
            }
            break;
        case MotionEvent.ACTION_MOVE:
        	isTouch = true;
            if (mHolderEventFlag) {
                CoverManager.getInstance().update(event.getRawX(), event.getRawY());
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        	isTouch = false;
            if (mHolderEventFlag) {
                if (parent != null)
                    parent.requestDisallowInterceptTouchEvent(false);
                CoverManager.getInstance().finish(this, event.getRawX(), event.getRawY());
            }
            break;
        }

        return true;
    }

    private ViewGroup getScrollableParent() {
        View target = this;
        while (true) {
            View parent;
            try {
                /**
                 * ViewRootImpl cannot be cast to android.view.View
                 */
                parent = (View) target.getParent();
            } catch (Exception e) {
                return null;
            }
            if (parent == null)
                return null;
            if (parent instanceof ListView || parent instanceof ScrollView) {
                return (ViewGroup) parent;
            }
            target = parent;
        }

    }

    public void setOnDragCompeteListener(OnDragCompeteListener onDragCompeteListener) {
        mOnDragCompeteListener = onDragCompeteListener;
    }
    
    public boolean isTouch() {
    	return isTouch;
    }
    
}
