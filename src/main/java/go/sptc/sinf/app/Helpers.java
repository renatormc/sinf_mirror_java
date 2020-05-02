package go.sptc.sinf.app;


public class Helpers {

    public static String fmtDuration(long secs){
        long hours = secs/3600;
        secs = secs%3600;
        long mins = secs/60;
        secs = secs%60;
        return String.format("%d:%02d:%02d", hours, mins, secs);
    }
}