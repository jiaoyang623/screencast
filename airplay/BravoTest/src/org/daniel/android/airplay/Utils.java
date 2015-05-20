package org.daniel.android.airplay;

import android.content.Context;
import com.longevitysoft.android.xml.plist.domain.Dict;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 5:31 PM
 */
public class Utils {

    public static String getStringFromAsset(String path, Context context) {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = context.getAssets().open(path);
            reader = new InputStreamReader(is);
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, count);
            }

            return builder.toString();
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

        return null;
    }

    public static Dict getDictFromAsset(String path, Context context) {
        InputStream is = null;
        InputStreamReader reader = null;
        try {
            is = context.getAssets().open(path);
            reader = new InputStreamReader(is);
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, count);
            }

            return PListUtils.parse(builder.toString());
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

        return null;
    }
}
