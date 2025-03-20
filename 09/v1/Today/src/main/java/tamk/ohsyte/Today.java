package tamk.ohsyte;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.MonthDay;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import tamk.ohsyte.commands.ListProviders;
import tamk.ohsyte.commands.ListEvents;
import tamk.ohsyte.providers.CSVEventProvider;

@Command(name = "today", subcommands = { ListProviders.class, ListEvents.class }, description = "Shows events from history and annual observations")
public class Today {
    public Today() {
        // Gets the singleton manager. Later calls to getInstance
        // will return the same reference.
        EventManager manager = EventManager.getInstance();

        // Construct a path to a file in the user's home directory,
        // in a subdirectory called ".today".
        String homeDirectory = System.getProperty("user.home");
        String configDirectory = ".today";
        Path path = Paths.get(homeDirectory, configDirectory, "events.csv");
        //System.out.println("Path = " + path.toString());

        // Create the events file if it doesn't exist
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                System.err.println("Unable to create events file");
                System.exit(1);
            }
        }

        String eventProviderId = "standard";

        // Add a CSV event provider that reads from the given file.
        manager.addEventProvider(
                new CSVEventProvider(path.toString(), eventProviderId));

        // Try to add an event provider with the same ID again:
        if (!manager.addEventProvider(
                new CSVEventProvider(path.toString(), eventProviderId))) {
            System.err.printf("Event provider '%s' is already registered%n", eventProviderId);
        }

        // Exercise the Java HTTP client API.
        // This will later be folded into WebEventProvider.
        MonthDay monthDay = MonthDay.now();  // get today's month-day
        var serverAddress = "https://todayserver-89bb2a1b2e80.herokuapp.com/";
        var serverEventsPath = "api/v1/events";
        var eventsParameters = String.format("?date=%s", monthDay.toString().substring(2));
        try {
            HttpClient client = HttpClient.newHttpClient();
            var serverUriString = String.format("%s%s%s", serverAddress, serverEventsPath, eventsParameters);
            URI serverUri = new URI(serverUriString);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(serverUri)
                    .GET()
                    .build();
            System.out.println(String.format("About to make HTTP request to %s%n", serverUri));
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String bodyString = response.body();
            int status = response.statusCode();
            if (status != 200) {
                System.err.printf("HTTP response: %d%n", status);
                System.err.println("Response body = " + bodyString);
            } else {
                System.out.println("Response headers: " + response.headers());
                System.out.println("Response body = " + bodyString);
            }
        } catch (URISyntaxException use) {
            System.err.println("Error making URI: " + use.getLocalizedMessage());
        } catch (IOException | InterruptedException ex) {
            System.err.println("Error sending HTTP request: " + ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Today()).execute(args);
        System.exit(exitCode);
    }
}
