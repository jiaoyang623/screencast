package org.daniel.android.miracast.widgets;

import android.app.Presentation;
import android.content.Context;
import android.view.Display;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date Apr 29 2015 3:49 PM
 */
public class SimplePresentation extends Presentation {
    public SimplePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public SimplePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }
}
