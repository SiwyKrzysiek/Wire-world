package views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import models.CellularAutomaton;

import java.util.Map;

//TODO: Dynamically adjust cell size

/**
 * Class responsible for displaying CellularAutomaton on Canvas
 * It used <b>delegation</b> architecture
 */
public class CellularAutomatonView<T extends Enum> {
    private double cellSize;

    private CellularAutomaton<T> cellularAutomaton;
    private final Canvas canvas;
    private final Map<T, Paint> cellStateToColorMap;
    private final Label generationNumberLabel;

    private Cell<T>[] cells;

    private int generationNumber = 0;

    /**
     * Set up fields. <b>CellularAutomaton</b> must be set before use!
     *
     * @param canvas
     * @param generationNumberLabel
     * @param cellStateToColorMap
     */
    public CellularAutomatonView(Canvas canvas, Label generationNumberLabel, Map<T, Paint> cellStateToColorMap, double cellSize) {
        this.canvas = canvas;
        this.generationNumberLabel = generationNumberLabel;

        this.cellStateToColorMap = cellStateToColorMap;
        this.cellSize = cellSize;
    }

    /**
     * Set up fields and draw given automaton
     *
     * @param canvas
     * @param generationNumberLabel
     * @param cellStateToColorMap
     * @param cellularAutomaton
     */
    public CellularAutomatonView(Canvas canvas, Label generationNumberLabel, Map<T, Paint> cellStateToColorMap, double cellSize, CellularAutomaton cellularAutomaton) {
        this(canvas, generationNumberLabel, cellStateToColorMap, cellSize);

        setCellularAutomaton(cellularAutomaton);
    }

    public void draw() {
        if (cellularAutomaton == null)
            return;

        updateStates();

        canvas.setHeight(getHeight());
        canvas.setWidth(getWidth());

        canvas.getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

        for (Cell cell : cells)
            cell.draw();

        generationNumberLabel.setText(Integer.toString(generationNumber));
    }

    public void randomize() {
        cellularAutomaton.randomize();
        generationNumber = 0;
        draw();
    }

    public void nextGeneration() {
        cellularAutomaton.nextGeneration();
        generationNumber++;
        draw();
    }

    /**
     * Set cell at given position to new value
     *
     * @param mouseX
     * @param mouseY
     * @param newValue
     */
    public void setCellToValue(final double mouseX, final double mouseY, T newValue) {
        final int row = (int) (mouseY / cellSize);
        final int column = (int) (mouseX / cellSize);
        final int index = row * getColumnCount() + column;

        cellularAutomaton.setCell(row, column, newValue);
        draw();
    }

    /**
     * Initial creation of cells.
     * Should be run whenever automaton changes shape.
     */
    private void generateCells() {
        cells = new Cell[getCellCount()];

        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                final int index = r * getColumnCount() + c;

                cells[index] = new Cell(c * cellSize, r * cellSize, cellularAutomaton.getCells()[index]);
            }
        }
    }

    private void updateStates() {
        for (int i = 0; i < getCellCount(); i++) {
            cells[i].setState(cellularAutomaton.getCells()[i]);
        }
    }

    public int getCellCount() {
        return cellularAutomaton.getCellCount();
    }

    /**
     * Calculates number of cells in vertical dimension
     *
     * @return number of cells in vertical dimension
     */
    public int getRowCount() {
        return cellularAutomaton.getHeight();
    }

    /**
     * Calculates number of cells in horizontal dimension
     *
     * @return number of cells in horizontal dimension
     */
    public int getColumnCount() {
        return cellularAutomaton.getWidth();
    }

    /**
     * Calculates height of entire view
     *
     * @return height of entire view
     */
    public double getHeight() {
        return cellSize * getRowCount();
    }

    /**
     * Calculates width of entire view
     *
     * @return width of entire view
     */
    public double getWidth() {
        return cellSize * getColumnCount();
    }

    /**
     * Take new cellular automaton and draw it
     *
     * @param cellularAutomaton
     */
    public void setCellularAutomaton(CellularAutomaton cellularAutomaton) {
        if (cellularAutomaton.getPossibleCellValues().length != cellStateToColorMap.size())
            throw new IllegalArgumentException("cellStateToColorMap size should be the same as number of possible states of automaton");

        this.cellularAutomaton = cellularAutomaton;

        generateCells();
        draw();
    }

    public boolean hasCellularAutomaton() {
        return cellularAutomaton != null;
    }

    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
        if (!hasCellularAutomaton())
            return;
        generateCells();
        draw();
    }

    private class Cell<T> {
        private final double x;
        private final double y;
//        private final double size = cellSize;
        //TODO: Consider removing state and use only states from cellAutomaton
        private T state;

        public Cell(double x, double y, T state) {
            this.x = x;
            this.y = y;
            this.state = state;
        }

        public void draw() {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(cellStateToColorMap.get(state));
            gc.fillRect(x, y, cellSize, cellSize);
        }

        public void setState(T state) {
            this.state = state;
        }
    }
}
