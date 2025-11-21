package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    public static void navigateTo(Stage stage, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Navigator.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            // mark root so stylesheet can target the whole window background
            try { root.getStyleClass().add("app-root"); } catch (Exception ignored) {}
            // attach stylesheet
            try {
                scene.getStylesheets().add(Navigator.class.getResource("/css/styles.css").toExternalForm());
            } catch (Exception ignored) {}
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openDialog(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Navigator.class.getResource(fxmlPath));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            try { root.getStyleClass().add("app-root"); } catch (Exception ignored) {}
            try { scene.getStylesheets().add(Navigator.class.getResource("/css/styles.css").toExternalForm()); } catch (Exception ignored) {}
            dialog.setScene(scene);
            dialog.setTitle(title);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
