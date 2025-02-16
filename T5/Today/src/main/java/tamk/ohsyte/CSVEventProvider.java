package tamk.ohsyte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.csv.*;

public class CSVEventProvider implements EventProvider {
    private List<Event> events;

    public CSVEventProvider(String fileName) {
        this.events = new ArrayList<>();

        // Read all lines from the CSV file
        // and create events with a helper method.
        // This is just a placeholder, and a
        // very bad way to do this;
        // there are just too many special cases in CSV.
        // Always use a library to handle the heavy lifting.
        // You have been warned! The next version will fix this.
        Path path = Paths.get(fileName);
        try {

            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Check if we can access the file
            try (Reader reader = new FileReader(fileName);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

                // Go through every record of the CSV file
                for (CSVRecord record : csvParser) {
                    String dt = record.get(0);
                    LocalDate date = LocalDate.parse(dt, dtf);
                    Category category = null;

                    // Handling category parsing and split it to String array
                    String cat = record.get(2);
                    String[] cat1 = cat.split("/");

                    // Check if there is 2 categories, if not, use null as a second category
                    if (cat1.length == 2) {
                        category = new Category(cat1[0], cat1[1]);
                    } else if (cat1.length == 1) {
                        category = new Category(cat1[0], null);
                    }

                    String description = record.get(1);

                    // Create event and add it to the list
                    Event event1 = new Event(date, description, category);
                    this.events.add(event1);
                }


            } catch (FileNotFoundException e) {
                throw new IOException("CSV file not found at: " + fileName, e);
            } catch (IOException e) {
                throw new IOException("Error reading CSV file: " + e.getMessage(), e);
            }


            System.out.printf("Read %d events from CSV file%n", this.events.size());
        } catch (IOException ioe) {
            System.err.println("File '" + fileName + "' not found");
        }

    }

    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    @Override
    public List<Event> getEventsOfCategory(Category category) {
        List<Event> result = new ArrayList<Event>();
        for (Event event : this.events) {
            if (event.getCategory().equals(category)) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        List<Event> result = new ArrayList<Event>();

        for (Event event : this.events) {
            final Month eventMonth = event.getDate().getMonth();
            final int eventDay = event.getDate().getDayOfMonth();
            if (monthDay.getMonth() == eventMonth && monthDay.getDayOfMonth() == eventDay) {
                result.add(event);
            }
        }

        return result;
    }

    private Event makeEvent(String row) {
        String[] parts = row.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        String description = parts[1];
        String categoryString = parts[2];
        String[] categoryParts = categoryString.split("/");
        String primary = categoryParts[0];
        String secondary = null;
        if (categoryParts.length == 2) {
            secondary = categoryParts[1];
        }
        Category category = new Category(primary, secondary);
        return new Event(date, description, category);
    }
}
