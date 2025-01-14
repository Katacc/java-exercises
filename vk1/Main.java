import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Numeroarvauspeli!");
        System.out.println("- Kata Eho");
        System.out.println("--------------------");

        boolean gameOn = true;
        int attempts = 7;

        int randNumber = (int)(1 + (Math.random() * 100));

        do {
            System.out.println("Arvaa numero valilla 1 - 100, sinulla on " + attempts + " yritysta");
            Scanner userInput = new Scanner(System.in);
            System.out.print("> ");

            String prompt = userInput.nextLine();

            if (CheckPrompt(prompt)) {
                int number = Integer.parseInt(prompt);

                if (number < 101 && number > 0) {

                    if (number == randNumber) {

                        System.out.println("Arvaus: " + prompt + " On oikein! Onnittelut!");
                        gameOn = false;

                    } else {
                        attempts--;
                        System.out.println("Arvaus: " + prompt + " On vaarin :(");
                    }

                    if (attempts == 0) {
                        System.out.println("Arvaukset loppu! Havisit pelin :(");
                        System.out.println("Oikea vastaus olisi ollut = " + randNumber);
                        System.out.println(" ");
                        gameOn = false;
                    }
                } else {
                    System.out.println("Anna luku valilla 1 - 100!");
                }
            } else {
                System.out.println("Syote taytyy olla numero!");
            }
        } while (gameOn == true);
    }

    public static boolean CheckPrompt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
