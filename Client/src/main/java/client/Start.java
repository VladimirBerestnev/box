package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Start extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception{

            Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
            primaryStage.setTitle("Box");
            primaryStage.setScene(new Scene(root, 800, 400));
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
}

