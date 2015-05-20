package org.daniel.android.miracast.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import org.daniel.android.miracast.utils.SafeHandler;
import org.daniel.android.miracast.utils.TU;


/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date Apr 09 2015 2:39 PM
 */
public class PresView extends View {
    private Paint mPaint;
    private OneHandler mHandler;

    public PresView(Context context) {
        super(context);
        init(context);
    }

    public PresView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PresView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        TU.j("PresView", "init");

        mPaint = new Paint();
        mHandler = new OneHandler(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TU.j("PresView", "onDraw");
        int w = getWidth();
        int h = getHeight();
        int d = Math.min(w, h) * 2 / 3;
        int r = (int) (Math.random() * 0xff);
        int g = (int) (Math.random() * 0xff);
        int b = (int) (Math.random() * 0xff);
        mPaint.setColor(Color.rgb(r, g, b));
        canvas.drawCircle(w / 2, h / 2, d / 2, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        TU.j("PresView", "onAttachedToWindow");

        super.onAttachedToWindow();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        TU.j("PresView", "onDetachedFromWindow");

        super.onDetachedFromWindow();
        mHandler.removeMessages(0);
    }

    private static class OneHandler extends SafeHandler<PresView> {

        public OneHandler(PresView presView) {
            super(presView);
        }

        @Override
        protected void handlerMessage(Message msg, PresView presView) {
            TU.j("PresView", "handleMessage");
            presView.invalidate();
            sendEmptyMessageDelayed(0, 300);
        }
    }
}
