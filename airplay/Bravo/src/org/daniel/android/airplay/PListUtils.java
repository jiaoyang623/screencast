package org.daniel.android.airplay;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;

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

//        Map<String, PListObject> provinceCities = root.getConfigMap();
//
//        for (int i = 0; i < provinceCities.keySet().size(); i++) {
//
//            Dict provinceRoot = (Dict) provinceCities.get(String.valueOf(i));
//            Map<String, PListObject> province = provinceRoot.getConfigMap();
//
//            String provinceName = province.keySet().iterator().next();
//            System.out.println("省份为:" + provinceName);                      // 打印省份
//
//            Dict cityRoot = (Dict) province.get(provinceName);
//
//            Map<String, PListObject> cities = cityRoot.getConfigMap();
//
//            for (int j = 0; j < cities.keySet().size(); j++) {
//                Dict city = (Dict) cities.get(String.valueOf(j));
//                String cityName = city.getConfigMap().keySet().iterator().next();
//                System.out.println("城市为:" + cityName);                      // 打印城市
//                Array districts = city.getConfigurationArray(cityName);
//                for (int k = 0; k < districts.size(); k++) {
//                    com.longevitysoft.android.xml.plist.domain.String district = (com.longevitysoft.android.xml.plist.domain.String) districts.get(k);
//                    System.out.println("地区为:" + district.getValue());       // 打印地区
//                }
//            }
//
//        }


        return root;
    }

    public static <T> T toObject(String data, Class<T> clazz) {

        return null;
    }

    public static String toPList(Object data) {
        return null;
    }
}
