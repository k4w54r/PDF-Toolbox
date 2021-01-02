package pdftoolbox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ExtractImagesFromPDFSceneController implements Initializable {


    @FXML
    private String inputPDFPath;
    private String outputFolderPath;
    boolean stopper;
    String fileExtension;
    File selectedFile;
    PDDocument document;
    Stage stage;

    @FXML
    private ComboBox<String> selectOutputFormat;
    ObservableList<String> outputFormatList = FXCollections.observableArrayList("JPEG", "PNG");
    private String outputFormat;

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
        selectOutputFormat.setItems(outputFormatList);
        selectOutputFormat.setValue("JPEG");
        outputFormat = selectOutputFormat.getValue();
        fileExtension = ".jpg";
        stopper=true;
        progress.setVisible(false);
        progressBar.setVisible(false);
    }

    @FXML
    public void outputFormatSelected(ActionEvent event) {
        outputFormat = selectOutputFormat.getValue();
        System.out.println(outputFormat);
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
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    int dpi = 300;
                    int pageNumber = Integer.parseInt(fromField.getText());
                    int limit = Integer.parseInt(toField.getText());
                    if(outputFormat=="JPEG")
                        fileExtension=".jpg";
                    else if(outputFormat=="PNG")
                        fileExtension=".png";
                    for (int i = pageNumber-1; i < limit; ++i) {
                        if (stopper) {
                            break;
                        }
                        File outputImageFile = new File(outputFolderPath + "/" + pageNumber + fileExtension);
                        BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                        ImageIO.write(bImage, outputFormat, outputImageFile);
                        System.out.println("I am Performing: " + pageNumber);
                        pageNumber++;
                        updateProgress(i,limit-1);
                    }
                    document.close();
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
