package pdftoolbox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExtractPagesFromPDFSceneController implements Initializable {


    @FXML
    private String inputPDFPath;
    private String outputFolderPath;
    boolean stopper;
    File selectedFile;
    PDDocument document;
    Stage stage;

    @FXML
    private ComboBox<String> selectSaveAs;
    ObservableList<String> saveAsList = FXCollections.observableArrayList("Separate Files", "Single File");
    private String saveAs;

    @FXML
    private TextField pdfField, outputFolderField, fromField, toField;

    @FXML
    private Label progress;
    @FXML
    private ProgressBar progressBar;
    @FXML
    public Label x;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectSaveAs.setItems(saveAsList);
        selectSaveAs.setValue("Separate Files");
        saveAs = selectSaveAs.getValue();
        stopper=true;
        progress.setVisible(false);
        progressBar.setVisible(false);
    }

    @FXML
    public void saveAsSelected(ActionEvent event) {
        saveAs = selectSaveAs.getValue();
        System.out.println(saveAs);
    }



    @FXML
    public void selectPDFClicked(ActionEvent event) throws IOException {
        System.out.println("selectPDFClicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF","*.pdf"));
        selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile!=null)
        {
            inputPDFPath = selectedFile.getAbsolutePath();
            pdfField.setText(inputPDFPath);
            document = PDDocument.load(selectedFile);
            int numberOfPages = document.getNumberOfPages();
            document.close();
            fromField.setText("1");
            toField.setText(String.valueOf(numberOfPages));
            //fileName = selectedFile.getName();
            System.out.println(inputPDFPath);
            //System.out.println(fileName);
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
        if(inputPDFPath == null)
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
                    document = PDDocument.load(selectedFile);
                    Splitter splitter = new Splitter();
                    List<PDDocument> Pages = splitter.split(document);
                    int pageNumber = Integer.parseInt(fromField.getText());
                    int limit = Integer.parseInt(toField.getText());
                    if(saveAs=="Separate Files")
                    {
                        for (int i = pageNumber-1; i < limit; ++i) {
                            if (stopper) {
                                break;
                            }
                            PDDocument pd = Pages.get(i);
                            pd.save(outputFolderPath + "/" + pageNumber +".pdf");
                            pd.close();
                            System.out.println("I am Performing: " + pageNumber);
                            pageNumber++;
                            updateProgress(i,limit-1);
                        }
                        document.close();
                        Desktop.getDesktop().open(new File(outputFolderPath));
                    }
                    else if(saveAs=="Single File") {
                        PDDocument outputDocument = new PDDocument();
                        for (int i = pageNumber - 1; i < limit; ++i) {
                            if (stopper) {
                                break;
                            }
                            outputDocument.addPage(document.getPage(i));
                            System.out.println("I am Performing: " + pageNumber);
                            updateProgress(i, limit - 1);
                        }
                        outputDocument.save(outputFolderPath + "/" + selectedFile.getName().replace(".pdf", "") + "_output.pdf");
                        document.close();
                        outputDocument.close();
                        Desktop.getDesktop().open(new File(outputFolderPath));
                    }
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
        inputPDFPath = null;
        outputFolderField.setText(null);
        outputFolderPath = null;
        fromField.setText(null);
        toField.setText(null);
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
