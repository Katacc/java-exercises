package tamk.ohsyte.providers.web;

// Java
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ArrayList;


// Time
import java.time.MonthDay;
import java.time.Month;

// Datamodels

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


import tamk.ohsyte.*;
import tamk.ohsyte.datamodel.AnnualEvent;
import tamk.ohsyte.datamodel.Category;
import tamk.ohsyte.datamodel.Event;
import tamk.ohsyte.datamodel.SingularEvent;

// Providers
import tamk.ohsyte.providers.EventProvider;

public class WebEventProvider implements EventProvider {
    private final List<Event> events = new ArrayList<>();
    private final String identifier;



    public WebEventProvider(URI serverUri, String identifier) {

        this.identifier = identifier;

        // Construct the Http request.
        MonthDay monthDay = MonthDay.now();
        String bodyString = null;

        try {

            /*
             Make a new HttpClient and send a GET request.
             Then parse the response into a body string and status code.
             */
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(serverUri)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            bodyString = response.body();

            int status = response.statusCode();
            /*
            if (status != 200) {
                System.err.printf("HTTP response: %d%n", status);
                System.err.println("Response body = " + bodyString);
            } else {
                System.out.println("Response headers: " + response.headers());
                System.out.println("Response body = " + bodyString);
            }
            */

        } catch (IOException | InterruptedException ex) {
            System.err.println("Error sending HTTP request: " + ex.getLocalizedMessage());
        }

        try {
            // Deserializer for jackson
            SimpleModule module = new SimpleModule("EventDeserializer");
            module.addDeserializer(Event.class, new EventDeserializer());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(module);

            // Custom collection type for Jackson
            JavaType customClassCollection = mapper.getTypeFactory().constructCollectionType(List.class, Event.class);

            // Use Jackson to parse the response body string as a list
            List<Event> webEvents = mapper.readValue(bodyString, customClassCollection);
            this.events.addAll(webEvents);


        } catch (JsonProcessingException ex) {
            System.err.println("Error processing JSON: " + ex.toString());
            ex.printStackTrace();
        }

    }



    /**
     * Gets all events from this provider.
     *
     * @return list of all events
     */
    @Override
    public List<Event> getEvents() {
        return this.events;
    }

    /**
     * Gets all events matching the specified category.
     *
     * @param category the category to match
     * @return list of matching events
     */
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

    /**
     * Gets the events matching the given month-day combination.
     *
     * @param monthDay month and day to match
     * @return list of matching events
     */
    @Override
    public List<Event> getEventsOfDate(MonthDay monthDay) {
        List<Event> result = new ArrayList<Event>();

        for (Event event : this.events) {
            Month eventMonth;
            int eventDay;
            if (event instanceof SingularEvent) {
                SingularEvent s = (SingularEvent) event;
                eventMonth = s.getDate().getMonth();
                eventDay = s.getDate().getDayOfMonth();
            } else if (event instanceof AnnualEvent) {
                AnnualEvent a = (AnnualEvent) event;
                eventMonth = a.getMonthDay().getMonth();
                eventDay = a.getMonthDay().getDayOfMonth();
            } else {
                throw new UnsupportedOperationException(
                        "Operation not supported for " +
                        event.getClass().getName());
            }
            if (monthDay.getMonth() == eventMonth && monthDay.getDayOfMonth() == eventDay) {
                result.add(event);
            }
        }

        return result;
    }


    /**
     * Gets the identifier of this event provider.
     *
     * @return the identifier
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

}
