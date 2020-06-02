package performance;

import models.GameOfLife;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameOfLifePerformanceTest {
    final static int WIDTH = 1000;
    final static int HEIGHT = 1000;
    final static int GENERATIONS = 50;
    final static int DATA_SAMPLE_SIZE = 200;

    public static void main(String[] args) throws IOException {
        var resultsFile = "Sequential_game_of_life_performance.txt";

        var results = new ArrayList<Long>(DATA_SAMPLE_SIZE);
        for (int i = 0; i < DATA_SAMPLE_SIZE; i++) {
            results.add(simulateManyGenerationsForRandomBoard());
        }

        try (var writer = new BufferedWriter(new FileWriter(resultsFile))) {
            writer.write("Width: " + WIDTH + "\n");
            writer.write("Height: " + HEIGHT + "\n");
            writer.write("Generations: " + GENERATIONS + "\n");

            for (var result : results) {
                writer.write(result.toString() + "\n");
            }
        }
    }

    public static long simulateManyGenerationsForRandomBoard() {
        GameOfLife gameOfLife = new GameOfLife(WIDTH, HEIGHT);
        gameOfLife.randomize();

        var statTime = System.nanoTime();
        for (int i = 0; i < GENERATIONS; i++) {
            gameOfLife.nextGeneration();
        }
        var endTime = System.nanoTime();

        var elapsedTime = endTime - statTime;
        System.out.println("Czas obliczeń w ns :" + elapsedTime);
        System.out.println("Czas obliczeń w ms :" + elapsedTime / 1e6);

        return elapsedTime;
    }
}
