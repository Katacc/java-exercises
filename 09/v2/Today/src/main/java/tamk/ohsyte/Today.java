package tamk.ohsyte;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.MonthDay;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import tamk.ohsyte.commands.ListEvents;
import tamk.ohsyte.commands.ListProviders;
import tamk.ohsyte.providers.web.WebEventProvider;

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


        // Give the date parameter for events to grab for WebEventProvider
        MonthDay monthDay = MonthDay.now();

        // URI for WebEventProvider
        URI serverUri = null;
        try {

            var serverAddress = "https://todayserver-89bb2a1b2e80.herokuapp.com/";
            var serverEventsPath = "api/v1/events";
            var eventsParameters = String.format("?date=%s", monthDay.toString().substring(2));
            var serverUriString = String.format("%s%s%s", serverAddress, serverEventsPath, eventsParameters);
            serverUri = new URI(serverUriString);

        } catch (URISyntaxException use) {
            System.err.println("Error making URI: " + use.getLocalizedMessage());
        }


        // Add a new WebEventProvider
        manager.addEventProvider(new WebEventProvider(serverUri, "webProvider"));
/*
        // Add a CSV event provider that reads from the given file.
        manager.addEventProvider(
                new CSVEventProvider(path.toString(), eventProviderId));

        // Try to add an event provider with the same ID again:
        if (!manager.addEventProvider(
                new CSVEventProvider(path.toString(), eventProviderId))) {
            System.err.printf("Event provider '%s' is already registered%n", eventProviderId);
        }
 */
}

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Today()).execute(args);
        System.exit(exitCode);
    }
}
