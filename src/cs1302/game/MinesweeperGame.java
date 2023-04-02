package cs1302.game;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * This class represents the Minesweeper Game.
 */
public class MinesweeperGame {

    private String[][] grid; // displayed grid for user
    private boolean[][] gridMines; // boolean tells if space has mine
    private String[][] noFogGrid; // the cheat code gride
    private ArrayList<Integer> seed; // stores ints from seed file
    private int rows;
    private int cols;
    private int numMines;
    private int rounds;
    private double score;
    private final Scanner stdIn;
    private String seedPath;
    private boolean validCommand;
    private boolean nofog;


    /**
     * Constructs a {@code MinesweeperGame} object with an input
     * scanner and the path to a seed file.
     * @param stdIn the standard input scanner.
     * @param seedPath the path to a seed file for the
     * {@code MinesweeperGame}.
     */
    MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        this.seedPath = seedPath;
        this.seed = new ArrayList<Integer>();
        rows = 0;
        cols = 0;
        rounds = 0;
        score = 0;
        validCommand = false;
        nofog = false;

    } // MinesweeperGame


    /**
     * This method provides the main game loop by invoking other
     * instance methods, as needed.
     */
    public void play() {
        readSeed();
        createGrid();
        printWelcome();

        while (!isWon()) {
            if (nofog == true) {
                printNoFog();
                nofog = false;
            } else {
                printDisplay();
            }
            promptUser();
        } // while isWon() conditions aren't met, game loop continues
        printWin();



    } // play


    /**
     * Reads the seed file that dictates the {@code row}, {@code col},
     * and {@code mines} instance variables.
     */
    public void readSeed() {
        try {
            File configFile = new File(this.seedPath);
            Scanner configScanner = new Scanner(configFile);
            if (!configScanner.hasNextInt()) {
                System.out.println();
                System.err.println("Seed file malformed error: The seed is not compatible.");
                System.exit(3);
            }
            while (configScanner.hasNextInt()) {
                this.seed.add(configScanner.nextInt()); // adds seed input to ArrayList
            }
        } catch (FileNotFoundException fnf) {
            System.err.println();
            System.err.println("Seed file not found error: " + fnf.getMessage());
            System.exit(2);
        }
    }


    /**
     * Uses the {@code seed} ArrayList to set {@code rows},
     * {@code cols}, and {@code numMines}. Also creates the
     * {@code grid}, {@code gridMines}, and {@code noFodGrid} arrays.
     * Sets the mines' positions in the array.
     */
    public void createGrid() {
        int tokens = seed.size();
        rows = seed.get(0);
        cols = seed.get(1);
        int added = rows + cols;
        if (tokens < 5 || added < 10 || added > 20) {
            System.err.println("Cannot create game with " + seedPath);
            System.err.print(" because it is not compatible.");
            System.err.println();
            System.exit(3);
        }
        numMines = seed.get(2);
        grid = new String[rows][cols];
        gridMines = new boolean[rows][cols];
        noFogGrid = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = " ";
                gridMines[i][j] = false;
                noFogGrid[i][j] = " ";
            } // Creates grid with empty strings, and gridmines with no mines
        } for (int i = 3; i < tokens; i += 2) {
            int mineX = seed.get(i);
            int mineY = seed.get(i + 1);
            gridMines[mineX][mineY] = true;
        } // sets mines in gridMines array. Uses pairs of ints in seed arraylist
    }


     /**
     * Method used to print the current minefield.
     */
    public void printMinefield() {
        for (int i = 0; i < rows; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < cols; j++) {
                System.out.print(" " + grid[i][j] + " |");
            }
            System.out.println();
        }
        System.out.print("     " + 0);
        for (int i = 1; i < cols; i++) {
            System.out.print("   " + i);
        }
    } // printMinefield


    /**
     * Method used to print the game display to standard output.
     * Calls upon the {@code printMinefield()} method.
     */
    public void printDisplay() {
        System.out.println();
        System.out.println(" Rounds completed: " + rounds);
        System.out.println();
        printMinefield();
        System.out.println();
    }

    /**
     * Used to print seed to ensure values are correct.
     */
    private void printSeed() {
        for (int i = 0; i < seed.size(); i++) {
            System.out.println(seed.get(i));
        }
    }

    /**
     * Prints the welcome banner that appears at game startup.
     */
    public void printWelcome() {
        try {
            File welcome = new File("resources/welcome.txt");
            Scanner txtScan = new Scanner(welcome);
            while (txtScan.hasNextLine()) {
                System.out.println(txtScan.nextLine());
            }
        } catch (FileNotFoundException fnf) {
            System.err.println("FNF");
        }

    }


    /**
     * Used to print the win message to standard output.
     */
    public void printWin() {
        System.out.println();
        try {
            File win = new File("resources/gamewon.txt");
            Scanner txtScan = new Scanner(win);
            while (txtScan.hasNextLine()) {
                System.out.println(txtScan.nextLine());
            }
        } catch (FileNotFoundException fnf) {
            System.err.println("FNF");
        }
        System.out.println();
        System.exit(0);
    }


    /**
     * Used to print the game over message to standard output.
     */
    public void printLoss() {
        System.out.println();
        try {
            File loss = new File("resources/gameover.txt");
            Scanner txtScan = new Scanner(loss);
            while (txtScan.hasNextLine()) {
                System.out.println(txtScan.nextLine());
            }
        } catch (FileNotFoundException fnf) {
            System.err.println("FNF");
        }
        System.out.println();
        System.exit(0);
    }


    /**
     * Method used to trigger the reveal command by the user.
     * Takes in a Scanner object to give values to
     * {@code revealRow)} and {@code revealCol}.
     *
     * @param inputScan the Scanner passed into the method from
     * {@code promptUser()}
     */
    public void reveal(Scanner inputScan) {
        int revealRow = 0;
        int revealCol = 0;
        if (!inputScan.hasNextInt()) {
            incorrectCommand();
            validCommand = false; // detects if proper input
        }
        if (inputScan.hasNextInt()) {
            revealRow = inputScan.nextInt();
            if (inputScan.hasNextInt()) {
                revealCol = inputScan.nextInt();
            }
            if (!inputScan.hasNextInt()) {
                if (revealRow >= 0 && revealRow < rows && revealCol >= 0 && revealCol < cols) {
                    if (gridMines[revealRow][revealCol]) {
                        printLoss(); // if mines on gridMines is true, lose game
                    } else {
                        grid[revealRow][revealCol] = getNumAdjMines(revealRow, revealCol);
                        rounds++;
                        validCommand = true;
                    }
                } else {
                    incorrectCommand();
                }
            }
        }
    }


    /**
     * Method used to trigger the mark command by the user.
     * Takes in a Scanner object to give values to
     * {@code markRow)} and {@code markCol}.
     *
     * @param inputScan the Scanner passed into the method from
     * {@code promptUser()}
     */
    public void mark(Scanner inputScan) {
        int markRow = 0;
        int markCol = 0;
        if (!inputScan.hasNextInt()) {
            incorrectCommand();
            validCommand = false;
        }
        if (inputScan.hasNextInt()) {
            markRow = inputScan.nextInt();
            if (inputScan.hasNextInt()) {
                markCol = inputScan.nextInt();
            }
            if (!inputScan.hasNextInt()) {
                if (markRow >= 0 && markRow < rows && markCol >= 0 && markCol < cols) {
                    grid[markRow][markCol] = "F";
                    rounds++;
                    validCommand = true; // if row and col are valid, marks space
                } else {
                    incorrectCommand();
                }
            }
        }
    }


    /**
     * Method used to trigger the reveal command by the user.
     * Takes in a Scanner object to give values to
     * {@code guessRow)} and {@code guessCol}.
     *
     * @param inputScan the Scanner passed into the method from
     * {@code promptUser()}
     */
    public void guess(Scanner inputScan) {
        int guessRow = 0;
        int guessCol = 0;
        if (!inputScan.hasNextInt()) {
            incorrectCommand();
            validCommand = false;
        }
        if (inputScan.hasNextInt()) {
            guessRow = inputScan.nextInt();
            if (inputScan.hasNextInt()) {
                guessCol = inputScan.nextInt();
            }
            if (!inputScan.hasNextInt()) {
                if (guessRow >= 0 && guessRow < rows && guessCol >= 0 && guessCol < cols) {
                    grid[guessRow][guessCol] = "F";
                    rounds++;
                    validCommand = true; // if row and col are valid, guesses space
                } else {
                    incorrectCommand();
                }
            }
        }
    }


    /**
     * Method used to trigger the help command by the user.
     * Print out all command options to standard output.
     */
    public void help() {
        System.out.println();
        System.out.println("Commands available...");
        System.out.println(" - Reveal: r/reveal row col");
        System.out.println(" - Mark: m/mark row col");
        System.out.println(" - Guess: g/guess row col");
        System.out.println(" - Help: h/help");
        System.out.println(" - Quit: q/quit");
        System.out.println();
        validCommand = true;
    }


    /**
     * Method used to trigger the quit command by the user.
     * Quits the game and exits program gracefully.
     */
    public void quit() {
        System.out.println();
        System.out.println("Thank you for playing! Goodbye!");
        System.exit(0);
        validCommand = true;
    }



    /**
     * Prints the "no fog" minefield which reveals all mines.
     */
    public void printNoFog() {
        rounds++;
        System.out.println();
        System.out.println(" Rounds completed: " + rounds);
        for (int i = 0; i < rows; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < cols; j++) {
                if (gridMines[i][j]) {
                    System.out.print("<" + grid[i][j] + ">|");
                } else {
                    System.out.print(" " + grid[i][j] + " |");
                }
            }
            System.out.println();
        }
        System.out.print("     " + 0);
        for (int i = 1; i < cols; i++) {
            System.out.print("   " + i);
        }
        System.out.println();
    }


    /**
     * Prints the game prompt to standard output and interprets user
     * input from standard input.
     */
    public void promptUser() {
        System.out.print("minesweeper-alpha: ");

        String input = stdIn.nextLine().trim();
        Scanner inputScan = new Scanner(input);
        String command = inputScan.next().trim();

        if (command.equals("reveal") || command.equals("r")) {
            reveal(inputScan);
        } else if (command.equals("mark") || command.equals("m")) {
            mark(inputScan);
        } else if (command.equals("guess") || command.equals("g")) {
            guess(inputScan);
        } else if (command.equals("help") || command.equals("h")) {
            help();
        } else if (command.equals("quit") || command.equals("quit")) {
            quit();
        } else if (command.equals("nofog")) {
            nofog = true;
        } else {
            System.err.println();
            System.err.println("Invalid command: " + command);
        }
    }


    /**
     * Method used to determine if a space is revealed or not.
     *
     * @param row the row that we want to check.
     * @param col the column that we want to check.
     * @return returns true is space is revealed.
     */
    public boolean isRevealed(int row, int col) {
        if (grid[row][col] == "F" || grid[row][col] == "?") {
            return false;
        } else {
            return true;
        }
    }




    /**
     * Returns {@code true} if, and only if, all conditions are met
     * to win the game.
     *
     * @return returns true if {@code numMines} equals {@code minesFlagged}
     * and if {@code spacesRevealed} equals {@code spaces}.
     */
    public boolean isWon() {
        int minesFlagged = 0;
        int spacesRevealed = 0;
        int spaces = rows * cols - numMines;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridMines[i][j] && grid[i][j] == "F") {
                    minesFlagged++;
                }
                if (isRevealed(i, j)) {
                    spacesRevealed++;
                }
            }
        }
        if (minesFlagged == numMines && spacesRevealed == spaces) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Returns the number of mines adjacent
     * to the specified square in the grid.
     *
     * @param row the row index of the square
     * @param col the column index of the square
     * @return the number of adjacent mines
     */
    private String getNumAdjMines(int row, int col) {
        int adjMines = 0;

        for (int i = row - 1; i < row + 1; i++) {
            for (int j = col - 1; j < col + 1; j++) {
                if ((i >= 0 && i < rows) && (j >= 0 && j < cols)) {
                    if (gridMines[i][j]) {
                        adjMines++;
                    }
                }
            }
        }
        return String.valueOf(adjMines);
    }


    /**
     * Prints invalid command message to standard output.
     */
    public void incorrectCommand() {
        System.out.println();
        System.out.println("Invalid Command: Command not recognized.");
    }


    /**
     * Calculates and returns the score for the player.
     *
     * @return returns the double score.
     */
    public double getScore() {
        score = 100.0 * rows * cols / rounds;
        return score;
    }
}
