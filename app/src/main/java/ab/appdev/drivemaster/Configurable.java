package ab.appdev.drivemaster;

import java.util.ArrayList;

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

    public static ArrayList<IntroViewItem> getIntroViewList() {

        ArrayList<IntroViewItem> introList = new ArrayList<>();

//        introList.add(
//                new IntroViewItem(
//                        "Create, Collab & Explore",
//                        "personal workspace for teams.",
//                        R.mipmap.ic_launcher)
//        );

        introList.add(
                new IntroViewItem(
                        "Car companion",
                        "attach your device infront of driver. Get alerted if drowsiness detected",
                        R.drawable.senderimg)
        );
        introList.add(
                new IntroViewItem(
                        "Scan QR and Connect",
                        "device Detection companion and receive alert in realtime",
                        R.drawable.recieverimg                )
        );
        introList.add(
                new IntroViewItem(
                        "Eye Sense",
                        "Your travel-drowsiness scanning companion",
                        R.mipmap.ic_launcher)
        );


        return introList;
    }


}