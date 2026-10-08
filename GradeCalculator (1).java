/*
 * Class: CMSC203 202720
 * Instructor: Huseyin Aygun
 * Description: Reads a config file and a student's scores, finds the
 *              weighted average and letter grade, and writes a report.
 * Due: 10/7/26
 * Platform/compiler: Eclipse, Java
 * I pledge that I have completed the programming assignment
 * independently. I have not copied the code from a student or
 * any source. I have not given my code to any student.
 * Print your Name here: Gabriela Million
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GradeCalculator {

    public static void main(String[] args) throws FileNotFoundException {

        String configFileName = "gradeconfig.txt";
        String inputFileName = "grades_input.txt";
        String outputFileName = "grades_report.txt";

        System.out.println("========================================");
        System.out.println("   CMSC203 Project 1 - Grade Calculator");
        System.out.println("========================================");
        System.out.println();

        // ---------- check the config file ----------
        System.out.println("Loading configuration from " + configFileName + " ...");

        File configFile = new File(configFileName);
        boolean usedDefault = false;

        if (configFile.exists() == false) {
            usedDefault = true;
        } else {
            // read it once just to check it
            Scanner checkScan = new Scanner(configFile);
            int totalWeight = 0;
            int howMany = 0;
            boolean good = true;

            if (checkScan.hasNextLine()) {
                checkScan.nextLine(); // course name
            } else {
                good = false;
            }

            if (good && checkScan.hasNextInt()) {
                howMany = checkScan.nextInt();
            } else {
                good = false;
            }

            for (int i = 0; i < howMany && good; i++) {
                if (checkScan.hasNext()) {
                    checkScan.next(); // category name
                } else {
                    good = false;
                }
                if (good && checkScan.hasNextInt()) {
                    totalWeight = totalWeight + checkScan.nextInt();
                } else {
                    good = false;
                }
            }
            checkScan.close();

            // weights have to add up to 100
            if (good == false || totalWeight != 100 || howMany <= 0) {
                usedDefault = true;
            }
        }

        // now open the config we are really going to use
        Scanner configScan;
        if (usedDefault) {
            configScan = new Scanner("CMSC203 Computer Science I\n3\nProjects 40\nQuizzes 30\nExams 30\n");
            System.out.println("Config file missing or invalid. Using default configuration.");
        } else {
            configScan = new Scanner(configFile);
            System.out.println("Configuration loaded successfully.");
        }

        String courseName = configScan.nextLine();
        int numCategories = configScan.nextInt();

        System.out.println();
        System.out.println("Using input file: " + inputFileName);
        System.out.println("Using output file: " + outputFileName);
        System.out.println();

        // ---------- read the student file ----------
        System.out.println("Reading student scores...");
        System.out.println();

        File inputFile = new File(inputFileName);
        if (inputFile.exists() == false) {
            System.out.println("Error: could not open " + inputFileName + ". Exiting.");
            return;
        }

        Scanner fileScan = new Scanner(inputFile);

        if (fileScan.hasNext() == false) {
            System.out.println("Error: " + inputFileName + " is empty. Exiting.");
            return;
        }

        String firstName = fileScan.next();
        String lastName = "";
        if (fileScan.hasNext()) {
            lastName = fileScan.next();
        }

        String categoryResults = "";
        double weightedSum = 0;
        int weightUsed = 0;

        // loop through the categories
        for (int i = 0; i < numCategories; i++) {

            // category name and weight from the config
            String configName = configScan.next();
            int weight = configScan.nextInt();

            // category name and number of scores from the student file
            String catName = "";
            int count = 0;
            if (fileScan.hasNext()) {
                catName = fileScan.next();
            }
            if (fileScan.hasNextInt()) {
                count = fileScan.nextInt();
            }

            if (catName.equals(configName) == false) {
                // names don't match, so skip these scores
                System.out.println("Error: category " + catName + " does not match " + configName + " in the config. Skipping it.");
                for (int j = 0; j < count && fileScan.hasNext(); j++) {
                    fileScan.next();
                }
            } else {
                // add up the scores
                double sum = 0;
                int goodScores = 0;
                for (int j = 0; j < count && fileScan.hasNext(); j++) {
                    if (fileScan.hasNextDouble()) {
                        double score = fileScan.nextDouble();
                        if (score >= 0 && score <= 100) {
                            sum = sum + score;
                            goodScores++;
                        } else {
                            System.out.println("Error: score " + score + " in " + catName + " is out of range. Skipping it.");
                        }
                    } else {
                        String bad = fileScan.next();
                        System.out.println("Error: \"" + bad + "\" in " + catName + " is not a number. Skipping it.");
                    }
                }

                // average (don't divide by 0)
                double average = 0;
                if (goodScores > 0) {
                    average = sum / goodScores;
                }

                weightedSum = weightedSum + average * weight;
                weightUsed = weightUsed + weight;
                categoryResults = categoryResults + "  " + catName + " (" + weight + "%): average = "
                        + String.format("%.2f", average) + "\n";
            }
        }
        fileScan.close();
        configScan.close();

        if (weightUsed == 0) {
            System.out.println("Error: no valid categories were found. Exiting.");
            return;
        }

        // ---------- show category results ----------
        System.out.println();
        System.out.println("Student: " + firstName + " " + lastName);
        System.out.println("Course: " + courseName);
        System.out.println();
        System.out.println("Category Results:");
        System.out.print(categoryResults);
        System.out.println();

        // ---------- ask about +/- grading ----------
        Scanner keyboard = new Scanner(System.in);
        String answer;
        do {
            System.out.print("Apply +/- grading? (Y/N): ");
            answer = keyboard.next();
            if (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")) {
                System.out.println("Invalid input. Please type Y or N.");
            }
        } while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N"));
        keyboard.close();

        // ---------- find the grade ----------
        double overall = weightedSum / weightUsed;

        String letter;
        double bandStart; // lowest number in the letter's band
        if (overall >= 90) {
            letter = "A";
            bandStart = 90;
        } else if (overall >= 80) {
            letter = "B";
            bandStart = 80;
        } else if (overall >= 70) {
            letter = "C";
            bandStart = 70;
        } else if (overall >= 60) {
            letter = "D";
            bandStart = 60;
        } else {
            letter = "F";
            bandStart = 0;
        }

        String finalLetter = letter;
        if (answer.equalsIgnoreCase("Y") && !letter.equals("F")) {
            double part = overall - bandStart;
            if (part >= 7) {
                finalLetter = letter + "+";
            } else if (part < 3) {
                finalLetter = letter + "-";
            }
        }

        // ---------- make the summary ----------
        String summary = "Student: " + firstName + " " + lastName + "\n"
                + "Course: " + courseName + "\n"
                + "\n"
                + "Category Results:\n"
                + categoryResults
                + "\n"
                + "Overall numeric average: " + String.format("%.2f", overall) + "\n"
                + "Base letter grade: " + letter + "\n"
                + "Final letter grade: " + finalLetter + "\n";
        if (usedDefault) {
            summary = summary + "Default configuration was used: Yes\n";
        } else {
            summary = summary + "Default configuration was used: No\n";
        }

        System.out.println();
        System.out.print(summary);
        System.out.println();

        // ---------- write the report file ----------
        PrintWriter out = new PrintWriter(outputFileName);
        out.print(summary);
        out.close();
        System.out.println("Summary written to " + outputFileName);

        System.out.println("Program complete. Goodbye!");
        System.out.println("Programmer: Gabriela Million");
    }
}
