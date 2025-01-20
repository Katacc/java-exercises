import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Old submission comments, don't want to delete for preservation purpose
        // :-----------------------------------------------------------------------

        // if (args.length == 0) {
        //     System.out.println("(Vinkki. Voit antaa syntymapaivasi myos komentorivi argumenttina (YYYY-MM-DD))");
        //     System.out.println("Anna syntymapaivasi (YYYY-MM-DD): ");
        //     bday = scanner.nextLine();
        // } else {
        //     bday = args[0];
        // }

        // try {
        //     LocalDate tryDate = LocalDate.parse(bday, formatter);
        // } catch (DateTimeParseException e) {
        //     System.out.println("Epasopiva syote, kokeile uudelleen...");
        //     return;
        // }

        // System.setProperty("BIRTHDATE", bday);
        // scanner.close();

        // :-------------------------------------------------------------------------

        String birthday = System.getenv("BIRTHDATE");

        try {
            LocalDate tryDate = LocalDate.parse(birthday, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Aseta syntymapaivasi ymparistomuuttujaan 'BIRTHDATE'=YYYY-MM-DD");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate objectBirthday = LocalDate.parse(birthday);


        if (objectBirthday.isAfter(today)) {
            System.out.println("Syntymapaivasi on tulevaisuudessa...");

        } else {

            int todayDay = today.getDayOfMonth();
            int todayMonth = today.getMonthValue();

            int birthdayDay = objectBirthday.getDayOfMonth();
            int birthdayMonth = objectBirthday.getMonthValue();

            long bdayErotus = ChronoUnit.DAYS.between(objectBirthday, today);

            if (birthdayDay == todayDay && birthdayMonth == todayMonth) {
                System.out.println("Hyvaa syntymapaivaa!");
            }

            String bdayErotusString = String.valueOf(bdayErotus);
            System.out.println("Olet " + bdayErotusString + " Paivaa vanha!");

            if (bdayErotus % 1000 == 0) {
                System.out.println("Kiva pyorea numero :)");
            }
        }

    }

}