package tamk.ohsyte.commands;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tamk.ohsyte.datamodel.Category;



@Command(name = "addevent")
public class AddEvent implements Runnable {

    @Option(names = "--date", description = "Sets a date for new event")
    String dateOption;

    @Option(names = "--category", description = "Sets a category for event")
    String categoryOption;

    @Option(names = "--description", description = "Sets a description for the category")
    String descriptionOption;


    @Override
    public void run() {

        // Make two type of date time formatters for LocalDate objects and MonthDay objects
        DateTimeFormatter formatWithYear = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("--MM-dd");


        // Category parsin
        Category category = null;
        try {
            category = Category.parse(this.categoryOption);
        } catch (Exception e) {
            System.out.println("Error in category...");
            return;
        }



        String eventString = null;
        int dateContinue = 0;

        /* Parsing of the date
         * Option:
         * - 1: LocalDate
         * - 2: MonthDay
         *
         * If the parsing fails according to the DateTimeFormatter of choice,
         * return to main program
         */
        if (isDateInFormat(this.dateOption, formatWithYear, 1)) {

            LocalDate date = LocalDate.parse(this.dateOption, formatWithYear);
            eventString = date.toString() + ',' + this.descriptionOption + ',' + category;
            dateContinue = 1;

        }
        if (isDateInFormat(this.dateOption, format, 2)) {

            MonthDay date = MonthDay.parse(this.dateOption, format);
            eventString = date.toString() + ',' + this.descriptionOption + ',' + category;
            dateContinue = 1;
        }


        if (dateContinue == 1) {

            // Set the path for the file we want to write into
            String homeDirectory = System.getProperty("user.home");
            String configDirectory = ".today";
            String path = homeDirectory + "/" + configDirectory + "/" + "events.csv";
            System.out.println(path);


            /* Write to the file using FileWriter and PrintWriter */
            PrintWriter pw = null;
            try {
                File file = new File(path);
                FileWriter fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                pw.println(eventString);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                pw.close();
            }

        } else {
            System.out.println("Error in date... or formatting");
            System.out.println("Make sure to use one of these formats:");
            System.out.println("--MM-dd");
            System.out.println("yyyy-MM-dd");
        }

    }

    /* Option:
     * 1: LocalDate
     * 2: MonthDay
     */
    public static boolean isDateInFormat(String date, DateTimeFormatter formatter, int option) {

        if (option == 1) {
            try {
                LocalDate.parse(date, formatter);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        } else {
            try {
                MonthDay.parse(date, formatter);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }

    }
}
