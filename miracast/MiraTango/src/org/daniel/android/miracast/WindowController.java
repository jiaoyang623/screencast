package org.daniel.android.miracast;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * 用于控制Miracast显示窗体，支持从Service中进行投屏
 *
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 22 2015 5:00 PM
 */
public class WindowController {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private Display mDisplay;

    public WindowController(Context outerWindowManager, Display display) {
        mDisplay = display;
        // 取得系统窗体
        mWindowManager = (WindowManager) outerWindowManager.createDisplayContext(display).getSystemService(Context.WINDOW_SERVICE);
        // 窗体的布局样式
        mLayoutParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型――TYPE_SYSTEM_ALERT(系统提示)
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置显示的模式
        mLayoutParams.format = PixelFormat.RGB_565;
        // 设置对齐的方法
        mLayoutParams.gravity = Gravity.FILL;
        // 设置窗体宽度和高度

        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public void addView(View view) {
        mWindowManager.addView(view, mLayoutParams);
    }

    public void removeView(View view) {
        mWindowManager.removeView(view);
    }

    public Display getDisplay() {
        return mDisplay;
    }
}
