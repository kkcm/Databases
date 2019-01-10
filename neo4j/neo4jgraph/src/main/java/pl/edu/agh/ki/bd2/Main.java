package pl.edu.agh.ki.bd2;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        GraphDatabase graphDatabase = GraphDatabase.createDatabase();

        Solution solution = new Solution(graphDatabase);
        solution.databaseStatistics();
        solution.runAllTests();

        AdditionalExercises additionalExercises = new AdditionalExercises(graphDatabase);
        additionalExercises.runAllTests();

    }

}
