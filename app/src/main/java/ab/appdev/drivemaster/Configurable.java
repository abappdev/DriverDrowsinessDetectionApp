package ab.appdev.drivemaster;

public class Configurable {
    public final static String SENSITIVITY = "sensitivity";
    public final static String DEFAULT_SENSITIVITY = "3";
    public final static String TAG = "eyeSense";

    public final static String SHAREDNAME = "eyesenseData";
    public final static String APPMODE = "APPMODE";
    public final static String RECEIVER = "RECEIVER";
    public final static String SENDER = "SENDER";
    public final static String BRODCASTID = "brodcastID";

    public static int getDetectionDelayInMilliseconds(int value) {
        return (value + 2) * 100;
    }
}
