package tamk.ohsyte;

import java.time.MonthDay;
import java.util.Collections;
import java.util.List;

public class Today {

    private static String OS = System.getProperty("os.name").toLowerCase();
    public static void main(String[] args) {

        String userprofile = null;
        String fileName = null;


        if (isWindows()) {
            userprofile = System.getenv("USERPROFILE");
            fileName = userprofile + "/.today/events.csv";
        } else if (isMac()) {
            userprofile = System.getenv("HOME");
            fileName = userprofile + "/.today/events.csv";
        } else if (isUnix()) {
            userprofile = System.getenv("HOME");
            fileName = userprofile + "/.today/events.csv";
        }


        // Exception is handled in CSVEventProvider.java
        // No additional exception handling needed
        EventProvider provider = new CSVEventProvider(fileName);

        final MonthDay monthDay = MonthDay.of(2, 11);

        // Get events for given day, any year, any category, newest first
        List<Event>events = provider.getEventsOfDate(monthDay);
        Collections.sort(events);
        Collections.reverse(events);

        for (Event event : events) {
            System.out.println(event);
        }
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

}