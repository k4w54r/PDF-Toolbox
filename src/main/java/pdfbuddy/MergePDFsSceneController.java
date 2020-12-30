package pdfbuddy;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MergePDFsSceneController implements Initializable {


    @FXML
    private String inputPDFs;
    private String outputFolderPath;
    boolean stopper;
    List<File> selectedFiles;
    Stage stage;


    @FXML
    private TextField pdfField, outputFolderField;

    @FXML
    private Label progress;
    @FXML
    private ProgressBar progressBar;
    @FXML
    public Label x;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stopper=true;
        progress.setVisible(false);
        progressBar.setVisible(false);
    }

    @FXML
    public void selectPDFsClicked(ActionEvent event) throws IOException {
        System.out.println("selectPDFClicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF","*.pdf"));
        selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if(selectedFiles!=null && selectedFiles.size()>0)
        {
            inputPDFs = "";
            for(int i=0;i<selectedFiles.size();i++){
                inputPDFs+= "\"" + selectedFiles.get(i).getName() + "\"" + " ";
            }
            for(int i=0;i<selectedFiles.size();i++){
                System.out.println(selectedFiles.get(i).getName());
            }
            pdfField.setText(inputPDFs);
            System.out.println(inputPDFs);
        }else{
            pdfField.setText(null);
        }
    }

    @FXML
    public void selectOFClicked(ActionEvent event) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if(selectedDirectory!= null){
            outputFolderPath = selectedDirectory.getAbsolutePath();
            outputFolderField.setText(outputFolderPath);
        }else{
            outputFolderField.setText(null);
        }
    }


    @FXML
    public void runClicked(ActionEvent event) throws IOException {
        System.out.println("runClicked");
        if(inputPDFs == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("No PDF Selected!");
            alert.showAndWait();
        }
        else if(outputFolderPath== null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("No Output Folder Selected!");
            alert.showAndWait();
        }
        else
        {
            stopper = false;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    PDFMergerUtility pdfMerger = new PDFMergerUtility();
                    pdfMerger.setDestinationFileName(outputFolderPath + "/" + "merged_output.pdf");
                    for(int i=0;i<selectedFiles.size();i++){
                        if (stopper) {
                            break;
                        }
                        pdfMerger.addSource(selectedFiles.get(i));
                        updateProgress(i,selectedFiles.size()-1);
                    }
                    pdfMerger.mergeDocuments(null);
                    Desktop.getDesktop().open(new File(outputFolderPath));
                    return null;
                }
            };

            progressBar.progressProperty().bind(task.progressProperty());
            progress.setVisible(true);
            progressBar.setVisible(true);
            // Run the task
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            stopper = false;
        }

    };




    @FXML
    public void cancelClicked(ActionEvent event) throws IOException {
        System.out.println("cancelClicked");
        if(!stopper) {
            stopper = true;
        }
        progress.setVisible(false);
        progressBar.setVisible(false);
        pdfField.setText(null);
        inputPDFs = null;
        outputFolderField.setText(null);
        outputFolderPath = null;
    }

    @FXML
    public void xClicked(){
        System.out.println(stopper);
        if(!stopper)
            stopper=true;
        stage = (Stage)x.getScene().getWindow();
        stage.close();
    }

}
