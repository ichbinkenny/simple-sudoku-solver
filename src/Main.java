import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        long time = System.nanoTime();
        SudokuPuzzle puzzle1 = new SudokuPuzzle("file1.csv");
        SudokuPuzzle puzzle2 = new SudokuPuzzle("file2.csv");
        puzzle1.solve();
        //puzzle2.solve();
        System.out.println("Both puzzles are now solved!");
        assert !puzzle1.toString().contains("0");
        assert !puzzle2.toString().contains("0");
        puzzle1.writeSolutionToFile("file1_solution.csv");
        puzzle2.writeSolutionToFile("file2_solution.csv");
        time = System.nanoTime() - time;
        System.out.println("Time total: " + time/1000000000.0);
    }
}
