package ensisa.lines;

import ensisa.lines.Tools.SelectTool;
import ensisa.lines.model.Document;
import ensisa.lines.model.LinesEditor;
import ensisa.lines.model.StraightLine;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MainController {
    private Document document;
    private LinesEditor linesEditor;
    private final ObjectProperty<Tool> currentTool;
    private final DrawTool drawTool;
    private final SelectTool selectTool;
    private final ObservableSet<StraightLine> selectedLines;

    public MainController() {
        this.document = new Document();

        selectTool = new SelectTool(this);
        drawTool = new DrawTool(this);
        currentTool = new SimpleObjectProperty<>(selectTool);
        selectedLines = FXCollections.observableSet();

    }
    public ObservableSet<StraightLine> getSelectedLines() {
        return selectedLines;
    }

    public Tool getCurrentTool() {
        return currentTool.get();
    }
    public void setCurrentTool(Tool tool) {
        this.currentTool.set(tool);
    }
    public Document getDocument() {
        return document;
    }
    @FXML
    public Pane editorPane;

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }
    private void observeDocument() {
        document.getLines().addListener(new ListChangeListener<StraightLine>() {
            public void onChanged(ListChangeListener.
                                          Change<? extends StraightLine> c) {
                while (c.next()) {
                    for (StraightLine line : c.getRemoved()) {
                        deselectLine(line);
                        linesEditor.removeLine(line);
                    }
                    for (StraightLine line : c.getAddedSubList()) {
                        linesEditor.createLine(line);
                    }
                }
            }
        });
    }
    public LinesEditor getLinesEditor() {
        return linesEditor;
    }
    public void initialize() {
        linesEditor = new LinesEditor(editorPane);
        initializeToolPalette();
        setClipping();
        observeDocument();
        observeSelection();
    }
    private void setClipping() {
        final Rectangle clip = new Rectangle();
        editorPane.setClip(clip);
        editorPane.layoutBoundsProperty().addListener((v, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }
    @FXML
    private void mousePressedInEditor(MouseEvent event) {
        getCurrentTool().mousePressed(event);
    }
    @FXML
    private void mouseDraggedInEditor(MouseEvent event) {
        getCurrentTool().mouseDragged(event);
    }
    @FXML
    private void mouseReleasedInEditor(MouseEvent event) {
        getCurrentTool().mouseReleased(event);
    }
    @FXML
    private void mouseEntered(MouseEvent event) {
        getCurrentTool().mouseEntered(event);
    }
    @FXML
    void mouseExited(MouseEvent event) {
        getCurrentTool().mouseExited(event);
    }
    @FXML
    private RadioButton selectToolButton;
    @FXML
    private RadioButton drawToolButton;
    private void initializeToolPalette() {
        selectToolButton.getStyleClass().remove("radio-button");
        selectToolButton.getStyleClass().add("toggle-button");
        drawToolButton.getStyleClass().remove("radio-button");
        drawToolButton.getStyleClass().add("toggle-button");
    }
    @FXML
    private void selectToolAction() {
        setCurrentTool(selectTool);
    }
    @FXML
    private void drawToolAction() {
        setCurrentTool(drawTool);
    }
    public void selectLine(StraightLine line, boolean keepSelection) {
        if (!keepSelection)
            getSelectedLines().clear();
        getSelectedLines().add(line);
    }
    public void deselectLine(StraightLine line) {
        getSelectedLines().remove(line);
    }
    public void deselectAll() {
        getSelectedLines().clear();
    }
    public StraightLine findLineForPoint(double x, double y) {
        for (var straightLine : getDocument().getLines()) {
            if (linesEditor.isPointInStartSelectionSquare(x, y, straightLine) ||
                    linesEditor.isPointInEndSelectionSquare(x, y, straightLine) ||
                    linesEditor.isPointInLine(x, y, straightLine))
                return straightLine;
        }
        return null;
    }
    private void observeSelection() {
        selectedLines.addListener(new SetChangeListener<StraightLine>() {
            @Override
            public void onChanged(Change<? extends StraightLine> change) {
                if (change.wasRemoved()) {
                    linesEditor.deselectLine(change.getElementRemoved());
                }
                if (change.wasAdded()) {
                    linesEditor.selectLine(change.getElementAdded());
                }
            }
        });
    }

}