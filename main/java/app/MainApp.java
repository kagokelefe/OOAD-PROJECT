package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DbUtil;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DbUtil.initDatabase(); // initialization and start

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(root);
        try {
            String css = getClass().getResource("/css/app.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        primaryStage.setTitle("Banking App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
