package template;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
    	FXMLLoader loader = new FXMLLoader(App.class.getResource("main.fxml"));
    	Parent root = loader.load();
    	stage.setResizable(false);
    	stage.setTitle("Adi Chaudhary - Project 1.5");
    	Controller controller = loader.getController();
    	scene = new Scene(root);
        
        scene.setOnKeyPressed(e -> {
        	if (e.getCode() == KeyCode.W) {
                controller.moveUp();
            } 
        	else if (e.getCode() == KeyCode.S) {
                controller.moveDown();
            } 
        	else if (e.getCode() == KeyCode.A) {
                controller.moveLeft();
            } 
        	else if (e.getCode() == KeyCode.D) {
                controller.moveRight();
            } 
        	else if (e.getCode() == KeyCode.R && controller.moneyGone()) {
                controller.restartWorld();
	        }
        });
        
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}