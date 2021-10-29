package ab.appdev.drivemaster;

public class Configurable {

    public final static String SENSITIVITY = "sensitivity";
    public final static String TAG = "SleepyDriver";

    public static int getDetectionDelayInMilliseconds(int value) {
        return (value + 2) * 250;
    }
}
