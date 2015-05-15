package org.daniel.android.dlna;

import android.text.TextUtils;
import android.util.Log;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装DLNA的设备控制指令
 *
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 11:05 AM
 */
public class RenderController {
    private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
    private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";

    private Device mDevice = null;

    public enum PlayerState {
        STOPPED, PLAYING, TRANSITIONING, PAUSED_PLAYBACK
    }

    public RenderController(Device device) {
        mDevice = device;
    }

    /*
     * Media Control
     */

    public void setDataSource(String uri) throws ServiceException {
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


    public void seek(int position) throws ServiceException {
        String time = getDateStr(position);

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Unit", "ABS_TIME");
        argument.put("Target", String.valueOf(time));

        if (action("Seek", argument) == null) {
            argument.put("Unit", "REL_TIME");
            action("Seek", argument);
        }
    }

    /**
     * @return position and length
     */
    public int[] getPosition() throws ServiceException {
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

    public void play() throws ServiceException {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Speed", "1");
        action("Play", argument);
    }

    public void pause() throws ServiceException {
        action("Pause");
    }

    public void stop() throws ServiceException {
        action("Stop");
    }

    public PlayerState getState() throws ServiceException {
        Action action = action("GetTransportInfo");
        String stateStr = action.getArgumentValue("CurrentTransportState");
        if (!TextUtils.isEmpty(stateStr)) {
            for (PlayerState state : PlayerState.values()) {
                if (state.toString().equalsIgnoreCase(stateStr)) {
                    return state;
                }
            }
        }
        return PlayerState.STOPPED;
    }

    private Action action(String actionName) throws ServiceException {
        return action(actionName, null);
    }

    private Action action(String actionName, Map<String, String> arguments) throws ServiceException {
        return action(AVTransport1, actionName, arguments);
    }

    private Action actionCtrl(String actionName) throws ServiceException {
        return action(RenderingControl, actionName, null);
    }

    private Action actionCtrl(String actionName, Map<String, String> arguments) throws ServiceException {
        return action(RenderingControl, actionName, arguments);
    }

    private Action action(String serviceName, String actionName, Map<String, String> arguments) throws ServiceException {
        boolean log = true;
//        boolean log = true;
        if (log) {
            Log.i("jy", "action(" + actionName + ")");
        }
        Service service = mDevice.getService(serviceName);

        if (service == null) {
            throw new ServiceException("no service " + actionName);
        }

        final Action action = service.getAction(actionName);
        if (action == null) {
            throw new ServiceException("no action " + actionName);
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
            throw new ServiceException("action return null " + actionName);
        }
    }


    public void mock(Object obj) {
//        Service service = mDevice.getService(RenderingControl);
//
//        if (service == null) {
//            Log.w("jy", "mock.service is null");
//            return;
//        }
//
//        for (Object obja : service.getActionList()) {
//            Action action = (Action) obja;
//            Log.i("jy", "actions: " + action.getName());
//        }

//        String[] ctrlSet = new String[]{"GetBrightness", "GetVolume", "GetVolumeDB", "GetVolumeDBRange"};
//        for (String action : ctrlSet) {
//            action(RenderingControl, action, null);
//        }
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

    /*
     * Device Control
     */

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

    public int getBrightness() throws ServiceException {
        Log.i("jy", "getBrightness()");
        Action action = actionCtrl("GetBrightness");
        return action == null ? 0 : parse(action.getArgumentValue("CurrentBrightness"), 0);
    }

    public void setBrightness(int brightness) throws ServiceException {
        Log.i("jy", "setBrightness() " + brightness);

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("DesiredBrightness", String.valueOf(brightness));

        action("SetBrightness", argument);
    }

    /*
     * Volume
     */
    public boolean isVolumeEnabled() {
        return isCtrlEnabled("GetVolume");
    }

    public int getVolume() throws ServiceException {
        Log.i("jy", "getVolume()");
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        Action action = action("GetVolume", argument);

        return action == null ? 0 : parse(action.getArgumentValue("CurrentVolume"), 0);
    }

    public void setVolume(int volume) throws ServiceException {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        argument.put("DesiredVolume", String.valueOf(volume));

        action("SetVolume", argument);
    }

    /*
     * VolumeDB
     */
    public boolean isVolumeDbEnabled() {
        return isCtrlEnabled("GetVolumeDB");
    }

    public int[] getVolumeDbRange() throws ServiceException {
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

    public int getVolumeDb() throws ServiceException {
        Log.i("jy", "getVolumeDb()");
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        Action action = action("GetVolumeDB", argument);

        return action == null ? 0 : parse(action.getArgumentValue("CurrentVolume"), 0);
    }

    public void setVolumeDb(int volumeDb) throws ServiceException {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        argument.put("DesiredVolume", String.valueOf(volumeDb));

        action("SetVolumeDb", argument);
    }

}
