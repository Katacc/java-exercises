package tamk.ohsyte.providers;

import tamk.ohsyte.EventFactory;
import tamk.ohsyte.datamodel.Category;
import tamk.ohsyte.datamodel.Event;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.MonthDay;
import java.util.*;
import java.util.stream.Collectors;

public class SQLiteEventProvider implements EventProvider {
    private String url;

    public SQLiteEventProvider(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;
        // TODO: normalize path separators to '/'
        //System.out.println("Database URL string = " + this.url);
    }

    private Map<Integer, String> getCategories(List<Integer> categoryIds) {
        Map<Integer, String> result = new HashMap<>();

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select category_id, primary_name, secondary_name from category");
        if (categoryIds.size() > 0) {
            String idList = categoryIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            queryBuilder.append(String.format(" where category_id in (%s)", idList));
        }
        var query = queryBuilder.toString();
        //System.out.println("Category query = " + query);

        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            //System.out.println("Connected to SQLite database.");
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");  // redundant
                String primaryName = rs.getString("primary_name");
                String secondaryName = rs.getString("secondary_name");
                result.put(categoryId, primaryName + "/" + secondaryName);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

    @Override
    public List<Event> getEvents() {
        // Get all categories from the database
        Map<Integer, String> categories = this.getCategories(List.of());

        List<Event> result = new ArrayList<>();

        var query = "SELECT event_date, event_description, category_id FROM event";
        try (var conn = DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            //System.out.println("Connected to SQLite database.");
            while (rs.next()) {
                String dateString = rs.getString("event_date");
                String descriptionString = rs.getString("event_description");
                int categoryId = rs.getInt("category_id");
                //System.out.printf("%s%s%d%n", dateString, descriptionString, categoryId);

                Event event = EventFactory.makeEvent(dateString, descriptionString,
                        categories.get(categoryId));
                result.add(event);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

    @Override
    public List<Event> getEventsOfCategory(Category category) {
        return List.of();
    }

    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        return List.of();
    }

    @Override
    public String getIdentifier() {
        return "sqlite";
    }
}
