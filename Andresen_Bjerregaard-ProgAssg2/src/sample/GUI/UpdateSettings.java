package sample.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.Controllers.UpdateController;

/**
 * Created by Administrator on 08-03-2015.
 */
public class UpdateSettings extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateSettings.fxml"));
        Parent root = loader.load();

        UpdateController updateController = loader.getController();

        primaryStage.getIcons().add(new Image("coin.png"));
        primaryStage.setTitle("Updater settings");
        primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
