import com.google.gson.JsonObject;

public class SampleJava {
    public static void main(String[] args) {
        JsonObject msgObj = new JsonObject();
        JsonObject mainObj = new JsonObject();
        JsonObject detailsObj = new JsonObject();

        detailsObj.addProperty("FollowsProfileID", "3");
        detailsObj.addProperty("ProfileID", "3");

        mainObj.add("Details", detailsObj);
        mainObj.addProperty("Type", "FOLLOW");
        mainObj.addProperty("Msg", "Hampton Dawns Notification");
        msgObj.add("message", mainObj);
        System.out.println(mainObj.toString());
        System.out.println(msgObj.toString());
    }
}
