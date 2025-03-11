package tamk.ohsyte;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Today {

    public static void printEvents(List<Event> events) {
        String date;

        for (Event event : events) {
            if (event instanceof SingularEvent) {
                date = ((SingularEvent) event).getDate().toString();
            } else {
                date = event.getMonthDay().toString();
            }

            System.out.printf(
                    "%s: %s (%s)%n",
                    date,
                    event.getDescription(),
                    event.getCategory());
        }

        System.out.println("");
    }

    public static void main(String[] args) {
        // Gets the singleton manager. Later calls to getInstance
        // will return the same reference.
        EventManager manager = EventManager.getInstance();

        // Add a CSV event provider that reads from the given file.
        // Replace with a valid path to the events.csv file on your own computer!
        String fileName = "/Users/yuki/.today/events.csv";
        manager.addEventProvider(new CSVEventProvider(fileName));

        fileName = "/Users/yuki/git/second-year-school/ohjelmoinninsyventava/07/singular-events.csv";
        manager.addEventProvider(new CSVEventProvider(fileName));

        MonthDay today = MonthDay.now();
        List<Event> allEvents = manager.getEventsOfDate(today);
        List<AnnualEvent> annualEvents = new ArrayList<>();
        List<SingularEvent> singularEvents = new ArrayList<>();


        for (Event event : allEvents) {
            if (event instanceof AnnualEvent) {
                annualEvents.add((AnnualEvent) event);
            } else if (event instanceof SingularEvent) {
                singularEvents.add((SingularEvent) event);
            }
        }

        System.out.println("Today:");
        Collections.sort(annualEvents, new AnnualEventComparator());

        for (AnnualEvent a : annualEvents) {
            System.out.printf(
                    "- %s (%s) %n",
                    a.getDescription(),
                    a.getCategory());
        }
        System.out.printf("%d events%n", annualEvents.size());

        System.out.println("\nToday in history:");
        Collections.sort(singularEvents, new SingularEventComparator());
        Collections.reverse(singularEvents);

        for (SingularEvent s : singularEvents) {
            int year = s.getDate().getYear();
            if (year < 2015) {
                continue;
            }

            System.out.printf(
                    "%d: %s (%s)%n",
                    year,
                    s.getDescription(),
                    s.getCategory());
        }
        System.out.printf("%d events%n \n", singularEvents.size());


        /*
        Filtering events by Month, Date and year using DateFilter
         */
        // List<Event> filteredEventsMDY = manager.getFilteredEvents(new DateFilter(MonthDay.parse("--02-21"), 2012));
        // System.out.println("Date filtered events: (Month, day and year)");
        // System.out.println("-------------------------------------------------");
        // printEvents(filteredEventsMDY);


        /*
         * Filtering events by Month and Date using DateFilter
         */
        // List<Event> filteredEventsMD = manager.getFilteredEvents(new DateFilter(MonthDay.parse("--02-21")));
        // System.out.println("Date filtered events: (Month and day)");
        // System.out.println("---------------------------------------");
        // printEvents(filteredEventsMD);



        /*
         *  Filtering events by category using CategoryFilter
         */
        // List<Event> filteredEventsCAT = manager.getFilteredEvents(new CategoryFilter(Category.parse("test")));
        // System.out.println("Category filtered events: (test)");
        // System.out.println("--------------------------------------");
        //printEvents(filteredEventsCAT);



        /*
         *  Filtering events by Month, Day, Year and Category with DateCategoryFilter
         */
        // List<Event> filteredEventsMDYCAT = manager.getFilteredEvents(new DateCategoryFilter(MonthDay.parse("--02-21"), 2012, Category.parse("test")));
        // System.out.println("Date and Category filtered events: (Month, day, year and test)");
        // System.out.println("--------------------------------------");
        // printEvents(filteredEventsMDYCAT);



        /*
         *  Filtering events by Month, Day and Category with DateCategoryFilter
         */
        List<Event> filteredEventsMDCAT = manager.getFilteredEvents(new DateCategoryFilter(MonthDay.parse("--02-21"), Category.parse("test")));
        System.out.println("Date and Category filtered events: (Month, day and test)");
        System.out.println("--------------------------------------");
        printEvents(filteredEventsMDCAT);
    }
}
