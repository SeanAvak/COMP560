package com.conorthomason.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KenKenSolver {
    private ConstraintCell constrainedArray[][];
    private int arraySize;
    private TreeMap<Character, SolutionConstraint> constraints;
    private TreeMap<Character, Cage> cages;
    private static int simpleBacktrackCounter = 0;
    private static int improvedBacktrackCounter = 0;
    private static int localCounter = 0;

    public KenKenSolver(ConstraintCell constrainedArray[][], TreeMap constraints, TreeMap cages) {
        this.constrainedArray = constrainedArray;
        this.constraints = constraints;
        this.cages = cages;
        arraySize = constrainedArray.length;
    }

    public KenKenSolver() {
        //nop
        //testing only
    }

    public ConstraintCell[][] solveKenKen() {
        /*if (simpleBacktrackSolve()) {
            System.out.println("\nFull Solution");
            System.out.println(simpleBacktrackCounter);
        }
        else {
            System.out.println("\nIncomplete Solution/No Solution");
            return constrainedArray; //If it returns false, that means no solution was found.
        }
         */
        /*
        if (improvedBacktrackSolve()) {
            System.out.println("\nFull Solution");
            System.out.println(improvedBacktrackCounter);
            return constrainedArray;
        }
        else {
            System.out.println("\nIncomplete Solution/No Solution");
            return constrainedArray; //If it returns false, that means no solution was found.
        }
         */
        if (localSearch()) {
            System.out.println("\nFull Solution");
            System.out.println(localCounter);
            return constrainedArray;
        }
        else {
            System.out.println("\nIncomplete Solution/No Solution");
            return constrainedArray; //If it returns false, that means no solution was found.
        }
    }

    public boolean improvedBacktrackSolve(){
        /*
        The main premise here is by taking advantage of numerical information; Using a method such as finding the GCD
        can reduce the number of possibilities left in a cell; therefore reducing the iterations somewhat.
        (Will likely vary depending on the puzzle provided)
         */
        improvedBacktrackCounter++;
        for (int row = 0; row < arraySize; row++){
            for (int col = 0; col < arraySize; col++){
                if (constrainedArray[row][col].getCellValue() == 0){
                    Cage currentCage = cages.get(constrainedArray[row][col].getCellKey());
                    if (currentCage.nearlyFilled()){
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int j = 0; j < currentCage.getCageSize(); j++){
                            list.add(currentCage.getCellIndex(j).getCellValue());
                        }
                        int divisor = gcd(list);
                        boolean safeValueDivisor = safeValueCheck(divisor, row, col);
                        for (int i = 1; i <= arraySize; i++) {
                            if (safeValueCheck(i, row, col)){
                                int assignedValue = (safeValueDivisor) ? divisor : i;
                                safeValueDivisor = false; //If it doesn't work now, chances are it won't work in the future.
                                constrainedArray[row][col].setCellValue(assignedValue);
                                if (kenKenRegionFilled(row, col)){
                                    if (kenKenValid(row, col)) {
                                        if (improvedBacktrackSolve()){
                                            return true;
                                        } else {
                                            constrainedArray[row][col].setCellValue(0);
                                        }
                                    } else {
                                        constrainedArray[row][col].setCellValue(0);
                                    }
                                }
                                else {
                                    if (improvedBacktrackSolve()) {
                                        return true;
                                    } else {
                                        constrainedArray[row][col].setCellValue(0);
                                    }
                                }
                            }
                        }}
                    return false;
                }
            }
        }
        return true;
    }
    private int gcd(int a, int b)
    {
        while (b > 0)
        {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    private int gcd(ArrayList<Integer> input)
    {
        int result = input.get(0);
        for(int i = 1; i < input.size(); i++)
            result = gcd(result, input.get(i));
        return result;
    }
    public boolean simpleBacktrackSolve(){
        simpleBacktrackCounter++;
        for (int row = 0; row < arraySize; row++){
            for (int col = 0; col < arraySize; col++){
                if (constrainedArray[row][col].getCellValue() == 0){
                    for (int i = 1; i <= arraySize; i++){
                        /*
                        Ok, this is where the tricky bit comes in.
                        I need to let safeValueCheck decide whether or not the value being assigned meets the rules
                        of what is basically Sudoku (I.e., row and column uniqueness).
                        This is independent of the current value being placed, so it isn't difficult.
                        However, the tricky bit begins when I need to start checking if both
                        A) The current "cage" (I.e. collection of constraint cells) is filled with nonzero values
                        B) If the cage is filled, it conforms to the constraints of said cage (I.e, the operations
                        performed produce the value listed).
                         */
                        if (safeValueCheck(i, row, col)){
                            constrainedArray[row][col].setCellValue(i);
                            if (kenKenRegionFilled(row, col)){
                                if (kenKenValid(row, col)) {
                                    if (simpleBacktrackSolve()){
                                        return true;
                                    } else {
                                        constrainedArray[row][col].setCellValue(0);
                                    }
                                } else {
                                    constrainedArray[row][col].setCellValue(0);
                                }
                            }
                            else {
                                if (simpleBacktrackSolve()) {
                                    return true;
                                } else {
                                    constrainedArray[row][col].setCellValue(0);
                                }
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean localSearch(){
        final boolean[] timeEnd = {false};
        Runnable runnable = new Runnable(){
            public void run(){
                long start = System.currentTimeMillis();
                long end = start + 20*1000; //Provided with 20 seconds to find a solution
                while (System.currentTimeMillis() < end)
                {
                    //running the loop
                }
                timeEnd[0] = true;
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        //Fill the array with random values, ensuring that they are not used more than 9 times for each possible value
        while (!timeEnd[0]) {
            int skipValue = 0;
            ArrayList<Integer> values = new ArrayList<>();
            for (int i = 0; i < arraySize; i++){
                for (int j = 1; j <= arraySize; j++){
                    values.add(j);
                }
            }

            //Assigns costs to each cell depending on their situation
            for (int i = 0; i < arraySize; i++){
                for (int j = 0; j < arraySize; j++){
                    ConstraintCell workingCell = constrainedArray[i][j];
                    if (!safeRow(i, workingCell.getCellValue())){
                        workingCell.setCellValue(workingCell.getCellValue() + 1);
                    }
                    if (!safeColumn(j, workingCell.getCellValue())){
                        workingCell.setCellValue(workingCell.getCellValue() + 1);
                    }
                    if (!kenKenValid(i, j)){
                        workingCell.setCellValue(workingCell.getCellValue() + 1);
                    }
                }
            }
            Collections.shuffle(values);
            for (int i = 0; i < arraySize; i++){
                for (int j = 0; j < arraySize; j++){
                    constrainedArray[i][j].setCellValue(values.get(j + skipValue));
                }
                skipValue += arraySize;
            }
            while (!timeEnd[0]){
                if (stoppingCriterion()){
                    return true;
                }

                //This is where the cost of each of the cells needs to come into play
            /*
            Provided any cell, a complete search of the board is done, determining what two values would be
            best to switch. The cost is assigned based off a tally system - 1 pt for incorrect row, 1 pt for incorrect
            column, 1 pt for incorrect KenKen (weightings may change). These costs are assigned after they are
            randomly set, so a full analysis can be made of their impact.
             */
                else {

                }
            }

        }
        return false;
    }

    private void comparativeSwapping(ConstraintCell cell1, ConstraintCell cell2){

    }
    private boolean stoppingCriterion(){

        //Checks to make sure each column/row has a unique set of numbers
        for (int i = 0; i < arraySize; i++){
            for (int j = 0; j < arraySize; j++){
                for (int k = j + 1; k < arraySize; k++){
                    if (constrainedArray[i][j].getCellValue() == constrainedArray[i][k].getCellValue()){
                        return false;
                    }
                }
            }
        }

        //Checks KenKen requirements
        for (Map.Entry<Character, Cage> entry : cages.entrySet()) {
            Cage cage = entry.getValue();
            if (!cage.filledCage()){
                return false;
            }
            for (int i = 0; i < cage.getCageSize(); i++){
                if (!kenKenValid(entry.getValue(),
                        constraints.get(cage.getCageKey()).getOperator(),
                        constraints.get(cage.getCageKey()).getValue())){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean safeRow(int row, int value){
        for (int i = 0; i < arraySize; i++) {
            if (constrainedArray[row][i].getCellValue() == value) {
                return false;
            }
        }
        return true;
    }

    private boolean safeColumn(int col, int value){
        for (int j = 0; j < arraySize; j++) {
            if (constrainedArray[j][col].getCellValue() == value) {
                return false;
            }
        }
        return true;
    }

    private boolean kenKenRegionFilled(int row, int column){
        char constraintChar = constrainedArray[row][column].getCellKey();
        Cage constraintCage = cages.get(constraintChar);
        return constraintCage.filledCage();
    }
    private boolean safeValueCheck(int value, int row, int column) {
        if (safeRow(row, value) && safeColumn(column, value))
            return true;
        return false;

    }

    public boolean kenKenValid(int row, int column){
        Cage constraintCage = cages.get(constrainedArray[row][column].getCellKey());
        char operator = constraints.get(constrainedArray[row][column].getCellKey()).getOperator();
        int workingValue = 0;
        int secondaryValue = 0;
        int index0 = constraintCage.getCellIndex(0).getCellValue();

        switch(operator){
            case '+':
                for (int i = 0; i < constraintCage.getCageSize(); i++){
                    workingValue += constraintCage.getCellIndex(i).getCellValue();
                }
                break;
            case '*':
                workingValue = 1;
                for (int i = 0; i < constraintCage.getCageSize(); i++){
                    workingValue *= constraintCage.getCellIndex(i).getCellValue();
                }
                break;
            case '-':
                int index1 = constraintCage.getCellIndex(1).getCellValue();
                workingValue = index0 - index1;
                secondaryValue = index1 - index0;
                break;
            case '/':
                index1 = constraintCage.getCellIndex(1).getCellValue();
                if (constraintCage.getCellIndex(0).getCellValue() == 0) {
                    workingValue = 0;
                    break;
                } else {
                    if (index0 >= index1){
                        workingValue = index0 / index1;
                    } else
                        workingValue = index1 / index0;
                }
                break;
        }
        int comparator = constraints.get(constrainedArray[row][column].getCellKey()).getValue();
        if (workingValue == comparator)
            return true;
        else if (secondaryValue == comparator)
            return true;
        else
            return false;
    }

    public boolean kenKenValid(Cage constraintCage, char operator, int comparator){
        int workingValue = 0;
        int secondaryValue = 0;
        int index0 = constraintCage.getCellIndex(0).getCellValue();

        switch(operator){
            case '+':
                for (int i = 0; i < constraintCage.getCageSize(); i++){
                    workingValue += constraintCage.getCellIndex(i).getCellValue();
                }
                break;
            case '*':
                workingValue = 1;
                for (int i = 0; i < constraintCage.getCageSize(); i++){
                    workingValue *= constraintCage.getCellIndex(i).getCellValue();
                }
                break;
            case '-':
                int index1 = constraintCage.getCellIndex(1).getCellValue();
                workingValue = index0 - index1;
                secondaryValue = index1 - index0;
                break;
            case '/':
                index1 = constraintCage.getCellIndex(1).getCellValue();
                if (constraintCage.getCellIndex(0).getCellValue() == 0) {
                    workingValue = 0;
                    break;
                } else {
                    if (index0 >= index1){
                        workingValue = index0 / index1;
                    } else
                        workingValue = index1 / index0;
                }
                break;
        }
        if (workingValue == comparator)
            return true;
        else if (secondaryValue == comparator)
            return true;
        else
            return false;
    }

}