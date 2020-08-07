import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class application extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{

        //设置主界面
        URL location = getClass().getResource("sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Music Player");
        primaryStage.getIcons().add(new Image("images/music.png"));
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);

        MainController controller = fxmlLoader.getController();


        //设置对话框
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialogAddList.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        root = fxmlLoader.load();
        Stage dialogAddList = new Stage();
        dialogAddList.initModality(Modality.APPLICATION_MODAL);
        dialogAddList.setResizable(false);
        scene = new Scene(root, 500, 250);
        dialogAddList.setScene(scene);
        DialogAddListController dialogAddListController = fxmlLoader.getController();
        dialogAddListController.setStage(dialogAddList);
        dialogAddListController.init();

        controller.setState(primaryStage, dialogAddList, dialogAddListController);
        controller.init();

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
