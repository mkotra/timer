package pl.abdoul.timer;

public class Constants {


    public static final String[] SHUTDOWN_LINUX = new String[]{
            "sh", "-c", "poweroff"
    };


    public static final String[] SHUTDOWN_WINDOWS = new String[]{
            "shutdown", "/p"
    };

    public static final String[] SHUTDOWN_MAC = new String[]{
            "shutdown", "-h", "now"
    };

    public static final String USER_HOME = "user.home";
}
