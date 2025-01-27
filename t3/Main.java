import java.time.LocalDate;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        LocalDate date11 = LocalDate.parse("2020-11-12");
        LocalDate date12 = LocalDate.parse("2021-10-25");
        LocalDate date13 = LocalDate.parse("2022-10-24");
        LocalDate date14 = LocalDate.parse("2023-09-26");
        LocalDate date15 = LocalDate.parse("2024-09-16");

        Category category = new Category("apple", "macos");

        String[] versionNames = new String[5];

        Event[] events = {
            new Event(date15, "15 Sequoia", category),
            new Event(date14, "14 Sonoma", category),
            new Event(date13, "13 Ventura", category),
            new Event(date12, "12 Monterey", category),
            new Event(date11, "11 Big Sur", category),
        };


        int i = 0;
        for (Event events1 : events) {
            String date = events1.getDate().getDayOfWeek().toString();
            date = date.charAt(0) + date.substring(1).toLowerCase();
            System.out.println("macOS " + events1.getDescription() + " was released on a " + date);
            String event = events1.getDescription().toString();
            event = event.substring(3);
            versionNames[i] = event;
            i++;
        }

        for (int s = 0; i < versionNames.length; i++) {

        }

        Arrays.sort(versionNames);

        System.out.print("In aplhabetical order: ");
        System.out.print(Arrays.toString(versionNames));



    }
}