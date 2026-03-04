package sorting;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Group root = new Group();
        Scene scene = new Scene(root,600,600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Example");

        Text text=new Text();
        text.setFill(Color.GREEN);
        text.setText("Hello World");
        text.setX(50);
        text.setY(50);
        text.setUnderline(true);
        text.setStyle("-fx-font-size:40");
        root.getChildren().add(text);

        Line line=new Line();
        line.setStartX(50);
        line.setStartY(50);
        line.setEndX(200);
        line.setEndY(250);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5);
        line.setRotate(0);
        root.getChildren().add(line);

        Rectangle rectangle=new Rectangle();
        rectangle.setFill(Color.BLUE);
        rectangle.setWidth(300);
        rectangle.setHeight(300);
        rectangle.setX(200);
        rectangle.setY(200);
        rectangle.setCursor(Cursor.CLOSED_HAND);
        root.getChildren().add(rectangle);



        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}