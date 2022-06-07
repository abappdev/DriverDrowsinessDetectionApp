package ab.appdev.drivemaster;

public class Information {
    private static String transferId = "";

    public static void setBroadcastId(String transferId) {
        Information.transferId = transferId;
    }

    public static String getBroadcastId() {
        return transferId;
    }
}
