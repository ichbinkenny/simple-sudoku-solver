import jdk.jshell.spi.ExecutionControl;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SudokuPuzzle {

    private ArrayList<ArrayList<Integer>> values = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> hints = new ArrayList<>();
    int solvedSquares = 0;

    /**
     * Creates a new sudoku puzzle from the provided CSV file.
     * @param filename the file to load
     */
    public SudokuPuzzle(String filename){
        File file = new File(filename);
        try{
            int index = 0;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            ArrayList<Integer>  value;
            while((line = bufferedReader.readLine()) != null){
                for(String s : line.split(",", -1)) {
                    ArrayList<Integer> defaultHints = new ArrayList<>();
                    defaultHints.add(0, 1);
                    defaultHints.add(1, 2);
                    defaultHints.add(2, 3);
                    defaultHints.add(3, 4);
                    defaultHints.add(4, 5);
                    defaultHints.add(5, 6);
                    defaultHints.add(6, 7);
                    defaultHints.add(7, 8);
                    defaultHints.add(8, 9);
                    if(s.equals("\n") || s.equals("")) {
                        value = new ArrayList<>();
                        value.add(0, 0);
                        values.add(index, value);
                        hints.add(index, defaultHints); //this value is zero so we will need to analyze the hints to get the correct value
                    }
                    else {
                        //System.out.println(s);
                        try {
                            value = new ArrayList<>();
                            value.add(0, Integer.parseInt(s));
                            values.add(index, value); //we know the current value, so there is no need
                            hints.add(index, new ArrayList<>());
                            solvedSquares++;
                        }catch(NumberFormatException ex){
                            value = new ArrayList<>();
                            value.add(0, 0);
                            values.add(index, value);
                            hints.add(index, defaultHints);
                        }
                    }
                    index++;
                }
            }
        }catch(IOException e) {
            System.err.println("File " + file + " was not found!");
            System.exit(-1);
        }
    }

    private void setupRowHintsForCell(int square){
        for(int i = 0; i < 9; i++){
            ArrayList<Integer> otherCellValueList = values.get(((square / 9) * 9) + i);
            ArrayList<Integer> otherCellHintsList = hints.get(((square / 9) * 9) + i);
            if(otherCellValueList.size() == 1 && otherCellValueList.get(0) != 0){
                hints.get(square).remove(otherCellValueList.get(0));
            }
            if(otherCellHintsList.size() == 1){
                hints.get(square).remove(otherCellValueList.get(0));
            }
        }
        String valueList = "";
        for(Integer i : hints.get(square)){
            valueList += i + " ";
        }
        //System.out.println("Remaining row options for cell " + square + " are " + valueList);
    }

    /**
     * Examines column of current cell and modifies available hint values
     * @param square the cell to check hints for
     */
    private void setupColumnHintsForCell(int square){
        for(int i = 0; i < 9; i++){
            ArrayList<Integer> otherCellValueList = values.get((square % 9) + (9 * i));
            ArrayList<Integer> otherCellHintsList = hints.get((square % 9) + (9 * i));
            if(otherCellValueList.size() == 1){
                hints.get(square).remove(otherCellValueList.get(0));
            }
            if(otherCellHintsList.size() == 1){
                hints.get(square).remove(otherCellValueList.get(0));
            }
        }
    }

    /**
     * Examines row of current cell and modifies available hint values
     * @param square the cell to check hints for
     */
    private void setupBoxHintsForCell(int square){
        int boxRow = (square / 9) / 3;
        int boxColumn = ((square % 9) / 3);
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                //ArrayList<Integer> otherBoxHints;
                int boxIndex = ((boxRow * 27) + i * 9) + (boxColumn * 3) + j;
                ArrayList<Integer> otherCellValueList = values.get(boxIndex);
                ArrayList<Integer> otherCellHintsList = hints.get(boxIndex);
                if(otherCellHintsList.size() == 1 && otherCellHintsList.get(0) != 0)
                    values.get(square).remove(otherCellHintsList.get(0));
                if(otherCellValueList.size() == 1)
                    hints.get(square).remove(otherCellValueList.get(0));
            }
        }
        String valueList = "";
    }

    /**
     * Gets the next cell that needs a value assigned to it
     * @param offset where to begin checking cells
     * @return the next cell to check or -1 if no new squares could be found from the offset position.
     */
    private int getNextSquare(int offset){
        for(int i = offset; i < 81; i++){
            if(values.get(i).size() == 1 && values.get(i).get(0) == 0){
                return i;
            }
        }
        return -1;
    }

    /**
     * Solves the attempted sudoku file.
     */

    public void solve() {
        int index = 0;
        while(solvedSquares < 81){
            int nextSquare = getNextSquare(index);
            if(nextSquare == -1)
                nextSquare = getNextSquare(0);
                if(nextSquare == -1){
                    System.err.println("No solution could be found for file...");
                }
            //System.out.println("Next Cell " + nextSquare);
                setupRowHintsForCell(nextSquare);
                setupColumnHintsForCell(nextSquare);
                setupBoxHintsForCell(nextSquare);
                if (hints.get(nextSquare).size() == 1 && hints.get(nextSquare).get(0) != 0) {
                    values.get(nextSquare).set(0, hints.get(nextSquare).get(0));
                    hints.get(nextSquare).set(0, 0);
                    solvedSquares++;
                }
            index++;
            if(index > 80)
                index = 0;
        }
    }

    /**
     * Writes a found solution to the file.
     * @param filename the file to write to.
     */
    public void writeSolutionToFile(String filename){
        File solutionFile = new File(filename);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            int count = 1;
            for(ArrayList<Integer> list : values){
                for(Integer value : list){
                    if(count == 9){
                        writer.write(value + "\n");
                        count = 1;
                    }
                    else {
                        writer.write(value + ",");
                        count++;
                    }
                }
            }
            writer.close();
        }catch(IOException e){
            System.err.println("Failed to write solution file to destination!");
        }
    }


    public String toString(){
        String s = "";
        int count = 0;
        for(ArrayList<Integer> list : values){
            for(Integer value : list){
                s += value + " ";
                count++;
                if(count == 9){
                    count = 0;
                    s += "\n";
                }
            }
        }
        return s;
    }

}
