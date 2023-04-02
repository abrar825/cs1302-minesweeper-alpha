package cs1302.game;

import java.util.Scanner;

/**
 * This driver creates a {@code MinesweeperGame} object and runs the
 * {@code play()} method. System exits with error if more than one
 * argument is supplied.
 */
public class MinesweeperDriver {

    public static void main (String[] args) {

        Scanner stdIn = new Scanner(System.in);
        if (args.length != 1) {
            System.err.println();
            System.err.println("Invalid usage. Usage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }
        String seedPath = args[0];




        MinesweeperGame game = new MinesweeperGame(stdIn, seedPath);



        game.play();



    }
}
