package tamk.ohsyte.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tamk.ohsyte.datamodel.Category;

@Command(name = "addevent")
public class AddEvent implements Runnable {
    @Option(names = "-c", description = "Category to add to event")
    String categoryOptionString;

    @Option(names = "-d", description = "Date for event")
    String dateOptionString;

    @Option(names = "-s", description = "String to add for the event description")
    String descriptionOptionString;


    @Override
    public void run() {
        Category category = null;
        DateTimeFormatter formatWithYear = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("--MM-dd");


        // Url for sqlite
        String homeDirectory = System.getProperty("user.home");
        String configDirectory = ".today";

        Path databasePath = Paths.get(homeDirectory, configDirectory, "events.sqlite3");

        String url = "jdbc:sqlite:" + databasePath;

        boolean categoryRepeat = false;
        int categoryKey = 0;

        do {
            // Map<Integer, String> categories = getCategories(List.of(), url);
            Map<Integer, String> categoriesMap = getCategories(List.of(), url);
            BiMap<Integer, String> categories = HashBiMap.create(categoriesMap);


            // Category parsing to check if its valid
            try {
                category = Category.parse(categoryOptionString);
            } catch (Exception e) {
                System.out.println("Error in category");
                categoryRepeat = false;
            }

            if (categories.containsValue(categoryOptionString)) {
                categoryKey = categories.inverse().get(categoryOptionString);
                categoryRepeat = false;
            } else {

                categoryRepeat = true;
                categoryKey = Collections.max(categoriesMap.keySet());
                // If the catgeroy not found in database
                // -> Create it and insert to database
                String sql = ("INSERT INTO      category" +
                            "                   (primary_name, secondary_name)" +
                            "VALUES             (?, ?)");

                try (var connection = DriverManager.getConnection(url);
                    var preparedStatement = connection.prepareStatement(sql)) {

                    preparedStatement.setString(1, category.getPrimary());
                    preparedStatement.setString(2, category.getSecondary());

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Category added succesfully.");
                    } else {
                        System.out.println("Failed adding category");
                        categoryRepeat = false;
                    }


                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        } while (categoryRepeat);




        String description = descriptionOptionString;

        String date = null;

        // Date parsing to see if its valid
        try {

            if (dateOptionString.startsWith("--")) {

                String year = "9999-";
                String dateString = dateOptionString.substring(2);
                dateString = year + dateString;
                date = MonthDay.parse(this.dateOptionString, format).toString();

            } else {
                date = LocalDate.parse(this.dateOptionString, formatWithYear).toString();
            }

        } catch (DateTimeParseException dtpe) {
            System.out.println(dtpe.getMessage());
        }


        // SQL
        String sql = ("INSERT INTO      event" +
                    "                   (event_date, event_description, category_id)" +
                    "VALUES             (?, ?, ?)");

        try (var connection = DriverManager.getConnection(url);
            var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, categoryKey);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Event added succesfully.");
            } else {
                System.out.println("Failed adding event");
            }

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }

    }

    /* Method stole from SQLEventProvider class */
    private Map<Integer, String> getCategories(List<Integer> categoryIds, String url) {
        Map<Integer, String> result = new HashMap<>();

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select category_id, primary_name, secondary_name from category");
        if (!categoryIds.isEmpty()) {
            // Construct a comma-separated list of category IDs
            // that can be used in the SQL WHERE clause, then
            // append it to the query builder.
            String idList = categoryIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            queryBuilder.append(String.format(" where category_id in (%s)", idList));
        }
        var query = queryBuilder.toString();
        //System.out.println("Category query = " + query);

        // Use the try-with-resources statement to get a connection,
        // a statement, and a result set, so that they will be closed
        // automatically.
        try (var connection = DriverManager.getConnection(url);
             var statement = connection.createStatement();
             var rs = statement.executeQuery(query)) {
            //System.out.println("Connected to SQLite database.");
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String primaryName = rs.getString("primary_name");
                String secondaryName = rs.getString("secondary_name");
                result.put(categoryId, primaryName + "/" + secondaryName);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

}
