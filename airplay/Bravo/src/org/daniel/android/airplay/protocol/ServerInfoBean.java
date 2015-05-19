package org.daniel.android.airplay.protocol;

import com.longevitysoft.android.xml.plist.domain.Dict;

/**
 * ServerInfo 数据
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 18 2015 5:22 PM
 */
public class ServerInfoBean {
    /*
        <key>deviceid</key>		<string>58:55:CA:1A:E2:88</string>
		<key>features</key>		<integer>14839</integer>
		<key>model</key>		<string>AppleTV2,1</string>
		<key>protovers</key>		<string>1.0</string>
		<key>srcvers</key>		<string>120.2</string>
    * */

    public String deviceId;
    public int features;
    public String model;
    public String protovers;
    public String srcvers;

    private ServerInfoBean() {
    }

    public static ServerInfoBean parse(Dict dict) {
        ServerInfoBean bean = new ServerInfoBean();
        bean.deviceId = dict.getString("deviceid");
        bean.features = dict.getInteger("features");
        bean.model = dict.getString("model");
        bean.protovers = dict.getString("protovers");
        bean.srcvers = dict.getString("srcvers");

        return bean;
    }

    @Override
    public String toString() {
        return "ServerInfoBean{" +
                "deviceId='" + deviceId + '\'' +
                ", features=" + features +
                ", model='" + model + '\'' +
                ", protovers='" + protovers + '\'' +
                ", srcvers='" + srcvers + '\'' +
                '}';
    }
}
