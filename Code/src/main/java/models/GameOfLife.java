package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GameOfLife extends CellularAutomaton<GameOfLife.CellStates> {

    public enum CellStates {
        DEAD,
        ALIVE;

        public static CellStates randomState() {
            CellStates[] values = CellStates.values();
            return values[random.nextInt(values.length)];
        }

        @Override
        public String toString() {
            return Integer.toString(this.ordinal());
        }
    }

    public GameOfLife(int width, int height) {
        super(width, height);

        this.cells = new CellStates[getCellCount()];
    }

    @JsonCreator
    public GameOfLife(
            @JsonProperty("width") final int width,
            @JsonProperty("height") final int height,
            @JsonProperty("cells") final GameOfLife.CellStates[] cells) {
        this(width, height);
        setCells(cells);
    }

    @Override
    public CellStates[] getPossibleCellValues() {
        return CellStates.values();
    }

    private class NexStateCallable implements Callable<CellStates> {
        private final int x;
        private final int y;

        public NexStateCallable(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public CellStates call() throws Exception {
            int aliveNeighbours = countAliveNeighbours(x, y);
            final int index = y * width + x;
            CellStates cell = cells[index];

            CellStates newState = getDefaultState();
            if (cell == CellStates.DEAD) {
                newState = aliveNeighbours == 3 ? CellStates.ALIVE : CellStates.DEAD;
            } else if (cell == CellStates.ALIVE) {
                Set<Integer> stillAlive = new HashSet<>(Arrays.asList(2, 3));
                newState = stillAlive.contains(aliveNeighbours) ? CellStates.ALIVE : CellStates.DEAD;
            }

            return newState;
        }
    }

    private class NextStateOfCellRangeCallable implements Callable<CellStates[]> {
        private final int from;
        private final int to;
        private final CellStates[] newGeneration;

        public NextStateOfCellRangeCallable(int from, int to, CellStates[] newGeneration) {
            this.from = from;
            this.to = to;
            this.newGeneration = newGeneration;
        }

        @Override
        public CellStates[] call() throws Exception {
            for (int inex = from; inex < to; inex++) {
                int x = inex % width;
                int y = inex / width;
                int aliveNeighbours = countAliveNeighbours(x, y);
                CellStates cell = cells[inex];

                CellStates newState = getDefaultState();
                if (cell == CellStates.DEAD) {
                    newState = aliveNeighbours == 3 ? CellStates.ALIVE : CellStates.DEAD;
                } else if (cell == CellStates.ALIVE) {
                    newState = aliveNeighbours == 2 || aliveNeighbours == 3 ? CellStates.ALIVE : CellStates.DEAD;
                }

                newGeneration[inex] = newState;
            }

            return newGeneration;
        }
    }

    private class NextStateOfCellRangeRunnable implements Runnable {
        private final int from;
        private final int to;
        private final CellStates[] newGeneration;

        public NextStateOfCellRangeRunnable(int from, int to, CellStates[] newGeneration) {
            this.from = from;
            this.to = to;
            this.newGeneration = newGeneration;
        }

        @Override
        public void run() {
            for (int inex = from; inex < to; inex++) {
                int x = inex % width;
                int y = inex / width;
                int aliveNeighbours = countAliveNeighbours(x, y);
                CellStates cell = cells[inex];

                CellStates newState = getDefaultState();
                if (cell == CellStates.DEAD) {
                    newState = aliveNeighbours == 3 ? CellStates.ALIVE : CellStates.DEAD;
                } else if (cell == CellStates.ALIVE) {
                    newState = aliveNeighbours == 2 || aliveNeighbours == 3 ? CellStates.ALIVE : CellStates.DEAD;
                }

                newGeneration[inex] = newState;
            }
        }
    }

    @Override
    protected CellStates[] generateNextGeneration() {
        var newGeneration = new CellStates[getCellCount()];

        var tasks = new ArrayList<NextStateOfCellRangeCallable>();
        var chunkSize = 200;
        for (int i = 0; i < getCellCount(); i+=chunkSize) {
            int to = i + chunkSize <= getCellCount() ? i + chunkSize : getCellCount();
            tasks.add(new NextStateOfCellRangeCallable(i, to, newGeneration));
        }

        try {
            var executor = MyThreadPool.getExecutor();
            executor.invokeAll(tasks);

            return newGeneration;
        } catch (InterruptedException e) {
            System.out.println("Calculations failed");
            return cells;
        }
    }

    @Override
    public void randomize() {
        for (int i = 0; i < getCellCount(); i++) {
            cells[i] = CellStates.randomState();
        }
    }

    @Override
    protected CellStates getDefaultState() {
        return CellStates.DEAD;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(width * height * 2);

        for (int i = 0; i < height; i++) {
            CellStates[] row = Arrays.copyOfRange(cells, i * width, i * width + width);
            List<String> rowValues = Arrays.stream(row).map(c -> c.toString()).collect(Collectors.toList());

            builder.append(String.join(" ", rowValues)).append("\n");
        }

        return builder.toString();
    }

    /**
     * Calculates how many alive cells are around given cell
     *
     * @param cellX
     * @param cellY
     * @return number of alive neighbours
     */
    private int countAliveNeighbours(final int cellX, final int cellY) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;

                final int neighbourX = cellX + i;
                final int neighbourY = cellY + j;

                if (neighbourX < 0 || neighbourX >= width || neighbourY < 0 || neighbourY >= height)
                    continue; //Cells out of board are DEAD

                final int index = neighbourY * width + neighbourX;
                if (cells[index] == CellStates.ALIVE)
                    count++;
            }
        }

        return count;
    }

    /**
     * For each cell calculates how many alive neighbours it has
     *
     * @return Array that in each fields contains count of neighbours of corresponding cell
     */
    private int[] countAliveNeighbours() {
        int[] result = new int[getCellCount()];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int cellIndex = i * width + j;

                result[cellIndex] = countAliveNeighbours(j, i);
            }
        }

        return result;
    }
}
