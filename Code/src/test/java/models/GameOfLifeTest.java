package models;

import models.GameOfLife;
import models.GameOfLife.CellStates;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class GameOfLifeTest {
    /*public static void main(String[] args) {
        GameOfLife gameOfLife = new GameOfLife(2, 3);

        CellStates[] states = {
                CellStates.ALIVE, CellStates.ALIVE,
                CellStates.DEAD, CellStates.DEAD,
                CellStates.ALIVE, CellStates.ALIVE
        };
        gameOfLife.setCells(states);

        System.out.println(gameOfLife);

        gameOfLife.nextGeneration();

        System.out.println(gameOfLife);
    }*/

    @Test
    public void nextGeneration() {
        GameOfLife gameOfLife = new GameOfLife(3, 3);
        CellStates[] states = {CellStates.DEAD, CellStates.DEAD, CellStates.DEAD, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.DEAD, CellStates.DEAD, CellStates.DEAD};
        gameOfLife.setCells(states);
        gameOfLife.nextGeneration();
        CellStates[] result = {CellStates.DEAD, CellStates.ALIVE, CellStates.DEAD, CellStates.DEAD, CellStates.ALIVE, CellStates.DEAD, CellStates.DEAD, CellStates.ALIVE, CellStates.DEAD};
        assertArrayEquals(result, gameOfLife.getCells());
    }

    @Test
    public void insertPattern() {
        GameOfLife gameOfLife = new GameOfLife(5, 5);
        CellStates[] states = {
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
        };
        gameOfLife.setCells(states);

        CellStates[] patternStates = {
                CellStates.ALIVE, CellStates.DEAD, CellStates.DEAD,
                CellStates.DEAD, CellStates.ALIVE, CellStates.ALIVE,
        };

        Pattern<GameOfLife.CellStates> pattern = new Pattern<>(3, 2, patternStates);

        //When
        gameOfLife.insertPattern(pattern, 1, 1);

        //Then
        CellStates[] expectedStates = {
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.DEAD, CellStates.DEAD, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.DEAD, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
                CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE, CellStates.ALIVE,
        };

        assertArrayEquals(expectedStates, gameOfLife.getCells());
    }
}
