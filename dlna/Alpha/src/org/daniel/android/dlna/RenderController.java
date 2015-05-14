package org.daniel.android.dlna;

import android.text.TextUtils;
import android.util.Log;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang@360.cn
 * @version 1.0
 * @date May 11 2015 11:05 AM
 */
public class RenderController implements IRenderController {
    private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
    private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";

    private int mVolumeMin = 0;
    private int mVolumeMax = 100;

    private Device mDevice = null;

    int mLength = 0;
    int mPosition = 0;
    PlayerState mState = PlayerState.Inited;

    public enum PlayerState {
        Inited, Loading, Playing, Paused, Stopped, Error
    }

    @Override
    public void setDevice(Device device) {
        Log.i("jy", "setDevice() ");

        mDevice = device;
        initVolume();
        initBrightness();
    }

    /**
     * Media Control
     */


    @Override
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


    @Override
    public void seek(int position) {
        mPosition = position;

        String time = getDateStr(position);

        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Unit", "ABS_TIME");
        argument.put("Target", String.valueOf(time));

        if (action("Seek", argument) == null) {
            argument.put("Unit", "REL_TIME");
            action("Seek", argument);
        }
    }

    private List<String> statusSet = Arrays.asList(new String[]{
            "STOPPED", "PAUSED_PLAYBACK", "PLAYING", "TRANSITIONING"
    });
    private PlayerState[] enumSet = {
            PlayerState.Stopped, PlayerState.Paused, PlayerState.Playing, PlayerState.Loading
    };

    protected void refreshState() {
        getPosition();
        Action action = action("GetTransportInfo");
        String state = action.getArgumentValue("CurrentTransportState");
        int index = statusSet.indexOf(state);
        if (index >= 0) {
            mState = enumSet[index];
        } else {
            mState = PlayerState.Error;
        }
    }

    @Override
    public int getPosition() {
        Action action = action("GetPositionInfo");
        if (action != null) {
            String time = action.getArgumentValue("AbsTime");
            if ("NOT_IMPLEMENTED".equals(time)) {
                time = action.getArgumentValue("RelTime");
            }
            mPosition = getTimestamp(time);
            String duration = action.getArgumentValue("TrackDuration");
            mLength = getTimestamp(duration);

//            Log.i("jy", "RenderController.getPosition: " + mPosition + ", " + time);
            return mPosition;
        } else {
            Log.w("jy", "getPosition.postControlAction() return null");
            return 0;
        }
    }

    @Override
    public int getLength() {
        if (mLength == 0) {
            getPosition();
        }
        return mLength;
    }

    @Override
    public void play() {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Speed", "1");
        action("Play", argument);
    }

    @Override
    public void pause() {
        action("Pause");
    }

    @Override
    public void stop() {
        action("Stop");
    }

    private Action action(String actionName) {
        return action(actionName, null);
    }

    private Action action(String actionName, Map<String, String> arguments) {
        return action(AVTransport1, actionName, arguments);
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


    @Override
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

    private void initVolume() {
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("Channel", "Master");

        Action action = action(RenderingControl, "GetVolumeDBRange", arguments);
        if (action != null) {
            mVolumeMin = parse(action.getArgumentValue("MinValue"), 0);
            mVolumeMax = parse(action.getArgumentValue("MaxValue"), 100);
            Log.i("jy", String.format("initVolume: [%d, %d]", mVolumeMin, mVolumeMax));
        } else {
            Log.w("jy", "initVolume.action.postControlAction() is null");
        }
    }

    /**
     * Brightness is from 0 to 100
     */
    private void initBrightness() {
        Map<String, String> arguments = new HashMap<String, String>();

        Action action = action(RenderingControl, "GetBrightness", arguments);
        if (action != null) {
            String brightness = action.getArgumentValue("CurrentBrightness");
            Log.i("jy", "Brightness: " + brightness);
        } else {
            Log.w("jy", "initVolume.action.postControlAction() is null");
        }
    }

    private int parse(String src, int defaultValue) {
        if (TextUtils.isEmpty(src)) {
            return defaultValue;
        } else {
            return Integer.parseInt(src);
        }
    }

    @Override
    public int getBrightness() {
        Log.i("jy", "getBrightness()");

        return 0;
    }

    @Override
    public void setBrightness(int brightness) {
        Log.i("jy", "setBrightness() " + brightness);

    }

    @Override
    public int getVolume() {
        Log.i("jy", "getVolume()");

        return 0;
    }

    @Override
    public void setVolume(int volume) {
        Map<String, String> argument = new HashMap<String, String>();
        argument.put("Channel", "Master");
        argument.put("DesiredVolume", String.valueOf(volume));

        action("SetVolume", argument);
    }
}
