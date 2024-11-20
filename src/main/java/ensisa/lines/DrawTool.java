package ensisa.lines;

import ensisa.lines.model.StraightLine;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class DrawTool implements Tool {
    public MainController mainController;
    public DrawTool(MainController controller) {
        state = State.initial;
        this.mainController = controller;
    }
    enum State { initial, drawing }
    private State state;
    private StraightLine currentLine;

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            currentLine = new StraightLine();
            currentLine.setStartX(event.getX());
            currentLine.setStartY(event.getY());
            currentLine.setEndX(event.getX());
            currentLine.setEndY(event.getY());
            mainController.getDocument().getLines().add(currentLine);
            state = State.drawing;
        }
    }
    @Override
    public void mouseDragged(MouseEvent event) {
        if (state == State.drawing && event.isPrimaryButtonDown()) {
            currentLine.setEndX(event.getX());
            currentLine.setEndY(event.getY());
        }
    }
    @Override
    public void mouseReleased(MouseEvent event) {
        state = State.initial;
    }
    @Override
    public void mouseEntered(MouseEvent event) {
        mainController.editorPane.setCursor(Cursor.CROSSHAIR);
    }
    @Override
    public void mouseExited(MouseEvent event) {
        mainController.editorPane.setCursor(Cursor.DEFAULT);
    }
}