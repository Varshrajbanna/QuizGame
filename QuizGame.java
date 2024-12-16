import java.io.*;
import java.util.*;

class Question {
    String question;
    String[] options = new String[4];
    char correctOption;
}

public class QuizGame {

    // Method to load questions from a file
    public static List<Question> loadQuestions(String filename) {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                Question q = new Question();
                q.question = line;
                for (int i = 0; i < 4; i++) {
                    q.options[i] = br.readLine();
                    if (q.options[i] == null) {
                        System.out.println("Error: Not enough options for question.");
                        return questions; // Exit if not enough options
                    }
                }
                String correctOptionLine = br.readLine();
                if (correctOptionLine != null && !correctOptionLine.trim().isEmpty()) {
                    q.correctOption = correctOptionLine.toUpperCase().charAt(0);
                } else {
                    System.out.println("Error: Missing correct option for question.");
                    return questions; // Exit if no correct option
                }
                questions.add(q);
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to load questions from file.");
        }

        return questions;
    }

    // Method to display a question and get user input
    public static boolean askQuestion(Question q) {
        System.out.println("\n" + q.question);
        System.out.println("A. " + q.options[0]);
        System.out.println("B. " + q.options[1]);
        System.out.println("C. " + q.options[2]);
        System.out.println("D. " + q.options[3]);
        System.out.print("Enter your answer (A/B/C/D): ");

        Scanner scanner = new Scanner(System.in);
        char answer = scanner.nextLine().toUpperCase().charAt(0);

        return answer == q.correctOption;
    }

    // Method for player registration
    public static void registerPlayer(String filename) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(username + "\t" + email + "\n");
            System.out.println("Registration successful!");
        } catch (IOException e) {
            System.out.println("Error: Unable to register player.");
        }
    }

    // Method for player login
    public static boolean loginPlayer(String filename) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("\t");
                if (credentials[0].equals(username) && credentials[1].equals(email)) {
                    System.out.println("Login successful!");
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to log in.");
        }

        System.out.println("Invalid credentials or user not registered.");
        return false;
    }

    // Method to play the quiz
    public static void playQuiz(List<Question> questions) {
        if (questions.isEmpty()) {
            System.out.println("No questions available. Please add questions to the file.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean[] asked = new boolean[questions.size()];
        int score = 0;

        for (int i = 0; i < 5; i++) { // Ask 5 random questions
            int idx;
            do {
                idx = random.nextInt(questions.size());
            } while (asked[idx]);

            asked[idx] = true;
            System.out.println("\nQuestion " + (i + 1) + ":");
            if (askQuestion(questions.get(idx))) {
                System.out.println("Correct!\n");
                score++;
            } else {
                System.out.println("Wrong! The correct answer was " + questions.get(idx).correctOption + ".\n");
            }
        }

        System.out.println("\nQuiz completed!");
        System.out.println("Your score: " + score + "/5");
    }

    // Main menu
    public static void main(String[] args) {
        String registrationFile = "players.txt";
        String questionFile = "questions.txt";

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Quiz Game System ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Play Quiz");
            System.out.println("4. Exit");
            System.out.print(" Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerPlayer(registrationFile);
                    break;
                case 2:
                    if (loginPlayer(registrationFile)) {
                        playQuiz(loadQuestions(questionFile));
                    }
                    break;
                case 3:
                    playQuiz(loadQuestions(questionFile));
                    break;
                case 4:
                    System.out.println("Thank you for playing. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}