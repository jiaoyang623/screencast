package org.daniel.android.airplay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.longevitysoft.android.xml.plist.domain.Dict;
import junit.framework.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:26 PM
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    public void onClick(View v) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                InputStreamReader reader = null;
                try {
                    is = OneApp.INSTANCE.getAssets().open("server-info.plist");
                    reader = new InputStreamReader(is);
                    char[] buffer = new char[1024];
                    StringBuilder builder = new StringBuilder();
                    int count;
                    while ((count = reader.read(buffer)) != -1) {
                        builder.append(buffer, 0, count);
                    }
                    Log.i("jy", builder.toString());
                    Dict dict = PListUtils.parse(builder.toString());
                    Log.i("jy", dict.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }
}
