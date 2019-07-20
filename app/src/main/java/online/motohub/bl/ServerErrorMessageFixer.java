package online.motohub.bl;

/**
 * You can override server error message with one you like
 */
public class ServerErrorMessageFixer {

    public static String shouldOverRideMessage(String errorMessage, String path) {
        if (errorMessage == null) return null;
        if (errorMessage.trim().startsWith("<!D")) {
            return MTErrorDefinition.ERROR_UNKNOW;
        }
        return null;
    }
}
