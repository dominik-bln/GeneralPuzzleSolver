package generalpuzzlesolver;

import generalpuzzlesolver.puzzle.PuzzleState;
import generalpuzzlesolver.puzzle.LocalStateManager;
import generalpuzzlesolver.graph.GraphColoringStateManager;
import generalpuzzlesolver.sudoku.SudokuStateManager;
import java.util.Scanner;

/**
 * This class provides a command line interface for running different puzzle solving algorithms on a
 * bunch of different puzzles.
 *
 * I measures some statistics and lets you choose the number of runs that should be performed.
 */
public class GeneralPuzzleSolver {

  private Scanner scanner;
  private ConstraintBasedLocalSearch searcher;
  private int numberOfRuns;
  private LocalStateManager puzzle;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    GeneralPuzzleSolver gps = new GeneralPuzzleSolver();
    gps.start();
  }

  private GeneralPuzzleSolver() {
    this.scanner = new Scanner(System.in);
  }

  /**
   * Lets the user choose all necessary parameters and starts the run of the algorithm.
   */
  private void start() {
    System.out.println("---------- Welcome to the General Puzzle Solver ----------");

    this.puzzle = this.choosePuzzle();
    this.searcher = this.chooseSearchAlgorithm();
    this.searcher.setStateManager(this.puzzle);
    this.numberOfRuns = this.chooseNumberOfRuns();

    this.resetStatistics();

    PuzzleState currentSolution;

    for (int i = 0; i < this.numberOfRuns; i++) {
      currentSolution = this.searcher.run();
      if(this.numberOfRuns == 1){
        currentSolution.display();
      }
      System.out.println("Used Steps: " + this.searcher.getStepCount());
      System.out.println("Number of conflicts: " + currentSolution.getConflicts().size());
      this.searcher.reset();
    }
  }

  private void resetStatistics() {
  }

  /**
   * Both higher and lower bound are inclusive.
   *
   * @param low
   * @param high
   * @return
   */
  private int readIntInRange(int low, int high) {
    int readInt = low - 1;
    while (readInt < low || readInt > high) {
      readInt = this.scanner.nextInt();
    }
    return readInt;
  }

  private LocalStateManager choosePuzzle() {
    System.out.println("Which puzzle would you like to use?");
    System.out.println("1. k-Queens");
    System.out.println("2. Graph Coloring");
    System.out.println("3. Sudoku");

    int puzzleIndex = this.readIntInRange(1, 2);

    LocalStateManager selectedPuzzle;

    switch (puzzleIndex) {
      case 1:
        selectedPuzzle = new KQueensStateManager();
        break;
      case 2:
        GraphColoringStateManager graphColoring = new GraphColoringStateManager();
        String graphDataFile = "";
        int scale = 1;

        switch (this.selectDifficulty()) {
          case 1:
            graphDataFile = "graph-color-1.txt";
            scale = 30;
            break;
          case 2:
            graphDataFile = "graph-color-2.txt";
            scale = 5;
            break;
          case 3:
            graphDataFile = "graph-color-3.txt";
            scale = 1;
            break;
        }
        graphColoring.generateInitialStateFromFile(graphDataFile, scale);
        selectedPuzzle = graphColoring;
        break;
      case 3:
        selectedPuzzle = new SudokuStateManager();
        break;
      default:
        //wrong selection, so start again
        System.out.println("Your selection was invalid, please try again.");
        selectedPuzzle = this.choosePuzzle();
    }

    return selectedPuzzle;
  }

  private int selectDifficulty() {
    System.out.println("Please select the difficulty (1-3):");
    int difficulty = this.scanner.nextInt();
    if (difficulty < 0 || difficulty > 3) {
      System.out.println("Your selection was not in the proper range.");
      difficulty = this.selectDifficulty();
    }
    return difficulty;
  }

  private int chooseNumberOfRuns() {
    int selectedRuns = 0;
    System.out.println("Please specify the number of runs:");
    while (selectedRuns < 1) {
      selectedRuns = this.scanner.nextInt();
    }
    return selectedRuns;
  }

  private ConstraintBasedLocalSearch chooseSearchAlgorithm() {
    System.out.println("Which algorithm would you like to use?");
    System.out.println("1. Simulated Annealing");
    System.out.println("2. Min-Conflicts");

    int algorithmIndex = this.readIntInRange(1, 2);
    
    ConstraintBasedLocalSearch selectedSearcher;

    switch (algorithmIndex) {
      case 1:
        selectedSearcher = new SimulatedAnnealing(10000);
        break;
      case 2:
        selectedSearcher = new MinConflicts(10000);
        break;
      default:
        //wrong selection, so start again
        System.out.println("Your selection was invalid, please try again.");
        selectedSearcher = this.chooseSearchAlgorithm();
    }

    return selectedSearcher;
  }
}
