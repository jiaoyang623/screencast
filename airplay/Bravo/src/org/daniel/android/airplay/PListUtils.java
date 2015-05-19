package org.daniel.android.airplay;

import android.util.Log;
import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.longevitysoft.android.xml.plist.domain.PListObjectType;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 2:24 PM
 */
public class PListUtils {

    public static Dict parse(String data) {
        PListXMLParser parser = new PListXMLParser();                // 基于SAX的实现
        PListXMLHandler handler = new PListXMLHandler();
        parser.setHandler(handler);
        parser.parse(data);

        PList actualPList = ((PListXMLHandler) parser.getHandler()).getPlist();
        Dict root = (Dict) actualPList.getRootElement();

        return root;
    }

    /*
05-18 18:45:12.950 I/jy      (14185): duration, float
05-18 18:45:12.950 I/jy      (14185): loadedTimeRanges, class [Lorg.daniel.android.airplay.protocol.PlaybackInfoBean$TimeRanges;
05-18 18:45:12.950 I/jy      (14185): playbackBufferEmpty, boolean
05-18 18:45:12.950 I/jy      (14185): playbackBufferFull, boolean
05-18 18:45:12.950 I/jy      (14185): playbackLikelyToKeepUp, boolean
05-18 18:45:12.950 I/jy      (14185): position, float
05-18 18:45:12.950 I/jy      (14185): rate, float
05-18 18:45:12.950 I/jy      (14185): readyToPlay, boolean
05-18 18:45:12.950 I/jy      (14185): seekableTimeRanges, class [Lorg.daniel.android.airplay.protocol.PlaybackInfoBean$TimeRanges;

    * */

    public static <T> T toObject(Dict dict, T t, Class<T> clazz) {
        if (t == null) {
            return null;
        }

        for (Field field : clazz.getFields()) {
            Log.i("jy", field.getName() + ", " + field.getType());

            field.setAccessible(true);

            String name = field.getName();
            PListObject pObj = dict.getConfigurationObject(name);
            if (pObj == null) {
                Log.i("jy", "has no data: " + name);
                continue;
            }

            if (field.getType().equals(Class.class)) {
                // class
                if (pObj.getType().equals(PListObjectType.DICT)) {
                    Class fieldClass = field.getClass();
                    try {
                        field.set(t, toObject((Dict) pObj, fieldClass.newInstance(), fieldClass));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            } else if (field.getType().equals(String.class)) {
                //String
            } else if (field.getType().equals(Date.class)) {
                //Date
            }
        }
        return t;
    }


    public static <T> T toObject(String data, Class<T> clazz) {
        Dict dict = parse(data);
        if (dict == null) {
            return null;
        }

        T t = null;
        try {
            t = clazz.newInstance();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return toObject(dict, t, clazz);
    }

    public static String toPList(Object data) {
        return null;
    }

    public static void mock() {
        Log.e("jy", "mock");
        int[] array = new int[1];
        Log.i("jy", "Array: " + array.getClass());
    }
}
