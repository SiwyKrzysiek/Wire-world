package controlers;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import models.CellularAutomaton;
import models.Pattern;
import models.WireWorld;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.paint.Color.*;
import static models.WireWorld.CellStates.*;

//TODO: States for GUI (Simulation paused, played, ...)

/**
 * Sub controller used by SetupController
 * It controls Game of Life tab of the main view
 */
public class WireWorldController extends CellularAutomatonController<WireWorld.CellStates> {

    private final Button powerOffButton;
    private final RadioButton emptyRadioButton;
    private final RadioButton tailRadioButton;
    private final RadioButton headRadioButton;
    private final RadioButton conductorRadioButton;


    public WireWorldController(TabPane modesTabPane, ListView patternListView, Button editPatternButton, Button newPatternButton, Button powerOffButton, Slider speedSlider, Canvas canvas, Slider zoomSlider, ToggleButton autoRunToggleButton, Button previousGenerationButton, Button nextGenerationButton, Spinner widthSpinner, Spinner heightSpinner, Button randomButton, Button emptyButton, Button saveButton, Button loadButton, Label generationNumberLabel, RadioButton emptyRadioButton, RadioButton tailRadioButton, RadioButton headRadioButton, RadioButton conductorRadioButton) {
        super(modesTabPane, patternListView, editPatternButton, newPatternButton ,speedSlider, canvas, zoomSlider,  autoRunToggleButton,  previousGenerationButton,  nextGenerationButton,  widthSpinner,  heightSpinner,  randomButton,  emptyButton,  saveButton,  loadButton,  generationNumberLabel);

        this.powerOffButton = powerOffButton;
        this.emptyRadioButton = emptyRadioButton;
        this.headRadioButton = headRadioButton;
        this.tailRadioButton = tailRadioButton;
        this.conductorRadioButton = conductorRadioButton;

        powerOffButton.setOnAction(this::powerOff);
        loadInitialPatterns();
    }

    @Override
    protected void loadInitialPatterns() {
        Pattern<WireWorld.CellStates> circuit = new Pattern<>("Closed circuit", 6, 4,
                new WireWorld.CellStates[] {
                        CONDUCTOR, CONDUCTOR, CONDUCTOR, TAIL, HEAD, CONDUCTOR,
                        CONDUCTOR, EMPTY, EMPTY, EMPTY, EMPTY, CONDUCTOR,
                        CONDUCTOR, EMPTY, EMPTY, EMPTY, EMPTY, CONDUCTOR,
                        CONDUCTOR, CONDUCTOR, CONDUCTOR, CONDUCTOR, CONDUCTOR, CONDUCTOR});
        patterns.add(circuit);
    }

    @Override
    protected FXMLLoader loadEditorFXMLLoader() throws IOException {
        return new FXMLLoader(getClass().getResource("/WireWorldFigureDrawer.fxml"));
    }

    @Override
    protected Class getCellularAutomatonInstanceClass() {
        return WireWorld.class;
    }

    @Override
    protected CellularAutomaton creteCellularAutomaton() {
        return new WireWorld(widthSpinner.getValue(), heightSpinner.getValue());
    }

    @Override
    protected Map getColoring() {
        Map<WireWorld.CellStates, Paint> coloring = new HashMap<>();

        coloring.put(EMPTY, Color.BLACK);
        coloring.put(WireWorld.CellStates.HEAD, BLUE);
        coloring.put(WireWorld.CellStates.TAIL, Color.RED);
        coloring.put(WireWorld.CellStates.CONDUCTOR, Color.YELLOW);

        return coloring;
    }

    @Override
    protected WireWorld.CellStates getSelectedState() {
        if (emptyRadioButton.isSelected())
            return EMPTY;
        else if (headRadioButton.isSelected())
            return WireWorld.CellStates.HEAD;
        else if (tailRadioButton.isSelected())
            return WireWorld.CellStates.TAIL;
        else
            return WireWorld.CellStates.CONDUCTOR;
    }

    private void powerOff(Event event){
        ((WireWorld) cellularAutomaton).killElectrons();
        cellularAutomatonView.draw(cellularAutomaton, zoomSlider.getValue());
    }

}