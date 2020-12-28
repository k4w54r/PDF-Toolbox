package pdfbuddy;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainSceneController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    @FXML
    public void singleImageOCRClicked(ActionEvent event) throws IOException {
        Parent temp = FXMLLoader.load(getClass().getResource("/fxml/SingleImageScene.fxml"));

        Scene scene = new Scene(temp);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage newWindow = new Stage();
        newWindow.setTitle("Single OCR");
        newWindow.setScene(scene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        //newWindow.setX(primaryStage.getX() + 200);
        //newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
    }
    @FXML
    public void singlePDFOCRClicked(ActionEvent event) throws IOException {
        Parent temp = FXMLLoader.load(getClass().getResource("/fxml/SinglePDFScene.fxml"));

        Scene scene = new Scene(temp);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage newWindow = new Stage();
        newWindow.setTitle("Single OCR");
        newWindow.setScene(scene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        //newWindow.setX(primaryStage.getX() + 200);
        //newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
    }
    public void bulkImageOCRClicked(ActionEvent event) throws IOException {
        Parent temp = FXMLLoader.load(getClass().getResource("/fxml/BulkImageDialog.fxml"));

        Scene scene = new Scene(temp);

        scene.getStylesheets().add("/styles/Styles.css");
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        final Stage newWindow = new Stage();
        newWindow.setTitle("Bulk OCR");

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                newWindow.setX(event.getScreenX() - xOffset);
                newWindow.setY(event.getScreenY() - yOffset);
            }
        });

        newWindow.setScene(scene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initStyle(StageStyle.UNDECORATED);


        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        //newWindow.setX(primaryStage.getX() + 200);
        //newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();


    }
    public void bulkPDFOCRClicked(ActionEvent event) throws IOException {
        Parent temp = FXMLLoader.load(getClass().getResource("/fxml/BulkPDFDialog.fxml"));

        Scene scene = new Scene(temp);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage newWindow = new Stage();
        newWindow.setTitle("Bulk OCR");
        newWindow.setScene(scene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        //newWindow.setX(primaryStage.getX() + 200);
        //newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
        newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
            }
        });
    }
}
