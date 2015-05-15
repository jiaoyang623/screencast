package org.daniel.android.dlna;

import android.text.TextUtils;
import android.util.Log;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 11:05 AM
 */
public class RenderController {
    private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
    private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";

    private Device mDevice = null;

//    private boolean isBrightnessEnabled = false;
//    private boolean isVolumeEnabled = false;
//    private boolean isVolumeDbEnabled = false;
//    private int mBrightness = 0;
//    private int mVolume = 0;
//    private int mVolumeDb = 0;
//    private int mVolumeDbMin = 0;
//
//    private int mVolumeDbMax = 100;
//
//    int mLength = 0;
//    int mPosition = 0;
//    PlayerState mState = PlayerState.Inited;
//
//    public enum PlayerState {
//        Inited, Loading, Playing, Paused, Stopped, Error
//    }

    public RenderController(Device device) {
        mDevice = device;
    }

    /**
     * Media Control
     */


    public void setDataSource(String uri) {
        Log.i("jy", "setDataSource() " + uri);
        if (TextUtils.isEmpty(uri)) {
            Log.i("jy", "uri is empty");
            return;
        }

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("CurrentURI", uri);
        argument.put("CurrentURIMetaData", "0");
        action("SetAVTransportURI", argument);
    }


    public void seek(int position) {
//        mPosition = position;

        String time = getDateStr(position);

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Unit", "ABS_TIME");
        argument.put("Target", String.valueOf(time));

        if (action("Seek", argument) == null) {
            argument.put("Unit", "REL_TIME");
            action("Seek", argument);
        }
    }

//    private List<String> statusSet = Arrays.asList(new String[]{
//            "STOPPED", "PAUSED_PLAYBACK", "PLAYING", "TRANSITIONING"
//    });
//    private PlayerState[] enumSet = {
//            PlayerState.Stopped, PlayerState.Paused, PlayerState.Playing, PlayerState.Loading
//    };
//
//    protected void refreshState() {
//        // Position
//        getPosition();
//        // Playing state
//        Action action = action("GetTransportInfo");
//        String state = action.getArgumentValue("CurrentTransportState");
//        int index = statusSet.indexOf(state);
//        if (index >= 0) {
//            mState = enumSet[index];
//        } else {
//            mState = PlayerState.Error;
//        }
//        // volume
//        if(isVolumeEnabled){
//
//        }
//        // brightness
//
//    }

    /**
     * @return position and length
     */
    public int[] getPosition() {
        int[] values = {0, 0};
        Action action = action("GetPositionInfo");
        if (action != null) {
            String time = action.getArgumentValue("AbsTime");
            if ("NOT_IMPLEMENTED".equals(time)) {
                time = action.getArgumentValue("RelTime");
            }
            values[0] = getTimestamp(time);
            String duration = action.getArgumentValue("TrackDuration");
            values[1] = getTimestamp(duration);
//            Log.i("jy", "RenderController.getPosition: " + mPosition + ", " + time);
        } else {
            Log.w("jy", "getPosition.postControlAction() return null");
        }
        return values;
    }

//    public int getLength() {
//        if (mLength == 0) {
//            getPosition();
//        }
//        return mLength;
//    }

    public void play() {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Speed", "1");
        action("Play", argument);
    }

    public void pause() {
        action("Pause");
    }

    public void stop() {
        action("Stop");
    }

    private Action action(String actionName) {
        return action(actionName, null);
    }

    private Action action(String actionName, Map<String, String> arguments) {
        return action(AVTransport1, actionName, arguments);
    }

    private Action actionCtrl(String actionName) {
        return action(RenderingControl, actionName, null);
    }

    private Action actionCtrl(String actionName, Map<String, String> arguments) {
        return action(RenderingControl, actionName, arguments);
    }

    private Action action(String serviceName, String actionName, Map<String, String> arguments) {
        boolean log = !"GetPositionInfo".equals(actionName) && !"GetTransportInfo".equals(actionName);
//        boolean log = true;
        if (log) {
            Log.i("jy", "action(" + actionName + ")");
        }
        Service service = mDevice.getService(serviceName);

        if (service == null) {
            Log.w("jy", actionName + ".service is null");
            return null;
        }

        final Action action = service.getAction(actionName);
        if (action == null) {
            Log.w("jy", actionName + ".action is null");
            return null;
        }

        //设置参数
        action.setArgumentValue("InstanceID", "0");
        if (arguments != null) {
            for (String key : arguments.keySet()) {
                action.setArgumentValue(key, arguments.get(key));
            }
        }

        //执行
        boolean isSuccess = action.postControlAction();
        if (isSuccess) {
            if (log) {
                Log.i("jy", action.toString());
            }
            return action;
        } else {
            Log.w("jy", actionName + ".postControlAction() return null");
            return null;
        }
    }


    public void mock(Object obj) {
        Service service = mDevice.getService(RenderingControl);

        if (service == null) {
            Log.w("jy", "mock.service is null");
            return;
        }

        for (Object obja : service.getActionList()) {
            Action action = (Action) obja;
            Log.i("jy", "actions: " + action.getName());
        }

        String[] ctrlSet = new String[]{"GetBrightness", "GetVolume", "GetVolumeDB", "GetVolumeDBRange"};
        for (String action : ctrlSet) {
            action(RenderingControl, action, null);
        }
    }

    private static int getTimestamp(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }

        String[] segment = str.split(":");
        if (segment.length != 3) {
            return 0;
        }

        int hour = Integer.parseInt(segment[0]);
        int minute = Integer.parseInt(segment[1]);
        int second = Integer.parseInt(segment[2]);

        return ((hour * 60 + minute) * 60 + second) * 1000;
    }

    private static String getDateStr(int timestamp) {
        if (timestamp <= 0) {
            return "00:00:00";
        }

        timestamp /= 1000;
        int second = timestamp % 60;
        timestamp /= 60;
        int minute = timestamp % 60;
        int hour = timestamp / 60;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * Device Control
     */

//    private void initVolume() {
//        isVolumeDbEnabled = isCtrlEnabled("GetVolume");
//        if (isVolumeDbEnabled) {
//            int[] volumeRange = getVolumeDbRange();
//            mVolumeDbMin = volumeRange[0];
//            mVolumeDbMax = volumeRange[1];
//
//            mVolumeDb = getVolumeDb();
//        } else {
//            Log.i("jy", "VolumeDb disabled");
//        }
//
//        isVolumeEnabled = isCtrlEnabled("GetVolumeDB");
//        if (isVolumeEnabled) {
//            mVolume = getVolume();
//        } else {
//            Log.i("jy", "Volume disabled");
//        }
//    }


    /**
     * Brightness is from 0 to 100
     */
//    private void initBrightness() {
//        isBrightnessEnabled = isCtrlEnabled("GetBrightness");
//        if (isBrightnessEnabled) {
//            mBrightness = getBrightness();
//        } else {
//            Log.i("jy", "Brightness disabled");
//        }
//    }
    private int parse(String src, int defaultValue) {
        if (TextUtils.isEmpty(src)) {
            return defaultValue;
        } else {
            return Integer.parseInt(src);
        }
    }

    private boolean isCtrlEnabled(String actionName) {
        Service service = mDevice.getService(RenderingControl);

        if (service == null) {
            Log.w("jy", actionName + ".service is null");
            return false;
        }
        return service.getAction(actionName) != null;
    }

    public boolean isBrightnessEnabled() {
        return isCtrlEnabled("GetBrightness");
    }

    public int getBrightness() {
        Log.i("jy", "getBrightness()");
        Action action = actionCtrl("GetBrightness");
        return action == null ? 0 : parse(action.getArgumentValue("CurrentBrightness"), 0);
    }

    public void setBrightness(int brightness) {
        Log.i("jy", "setBrightness() " + brightness);

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("DesiredBrightness", String.valueOf(brightness));

        action("SetBrightness", argument);
    }

    /**
     * Volume
     */

    public boolean isVolumeEnabled() {
        return isCtrlEnabled("GetVolume");
    }

    public int getVolume() {
        Log.i("jy", "getVolume()");
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        Action action = action("GetVolume", argument);

        return action == null ? 0 : parse(action.getArgumentValue("CurrentVolume"), 0);
    }

    public void setVolume(int volume) {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        argument.put("DesiredVolume", String.valueOf(volume));

        action("SetVolume", argument);
    }

    /**
     * VolumeDB
     */
    public boolean isVolumeDbEnabled() {
        return isCtrlEnabled("GetVolumeDB");
    }

    public int[] getVolumeDbRange() {
        int[] values = {0, 0};
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("Channel", "Master");

        Action action = actionCtrl("GetVolumeDBRange", arguments);
        if (action != null) {
            values[0] = parse(action.getArgumentValue("MinValue"), 0);
            values[1] = parse(action.getArgumentValue("MaxValue"), 100);
            Log.i("jy", String.format("initVolume: [%d, %d]", values[0], values[1]));
        } else {
            Log.w("jy", "initVolume.action.postControlAction() is null");
        }

        return values;
    }

    public int getVolumeDb() {
        Log.i("jy", "getVolumeDb()");
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        Action action = action("GetVolumeDB", argument);

        return action == null ? 0 : parse(action.getArgumentValue("CurrentVolume"), 0);
    }

    public void setVolumeDb(int volumeDb) {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        argument.put("DesiredVolume", String.valueOf(volumeDb));

        action("SetVolumeDb", argument);
    }

}
