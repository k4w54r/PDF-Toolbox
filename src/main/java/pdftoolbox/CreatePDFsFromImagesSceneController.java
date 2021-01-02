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
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class CreatePDFsFromImagesSceneController implements Initializable {


    @FXML
    private String inputImages;
    private String outputFolderPath;
    boolean stopper;
    File selectedFile;
    PDDocument document;
    List<File> selectedFiles;
    Stage stage;

    @FXML
    private ComboBox<String> selectSaveAs;
    ObservableList<String> saveAsList = FXCollections.observableArrayList("Separate Files", "Single File");
    private String saveAs;

    @FXML
    private TextField imagesField, outputFolderField;

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
    public void selectImagesClicked(ActionEvent event) throws IOException {
        System.out.println("selectImagesClicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPEG","*.jpg"),new FileChooser.ExtensionFilter("PNG","*.png"));
        selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if(selectedFiles!=null && selectedFiles.size()>0)
        {
            inputImages = "";
            for(int i=0;i<selectedFiles.size();i++){
                inputImages+= "\"" + selectedFiles.get(i).getName() + "\"" + " ";
            }
            /*for(int i=0;i<selectedFiles.size();i++){
                System.out.println(selectedFiles.get(i).getName());
            }*/
            imagesField.setText(inputImages);
            System.out.println(inputImages);
        }else{
            imagesField.setText(null);
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
        if(inputImages == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("No Images Selected!");
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
                    if(saveAs=="Separate Files")
                    {
                        for(int i=0;i<selectedFiles.size();i++){
                            if (stopper) {
                                break;
                            }
                            Document document = new Document();
                            String fileName = selectedFiles.get(i).getName().substring(0, selectedFiles.get(i).getName().lastIndexOf("."))+".pdf";
                            PdfWriter.getInstance(document, new FileOutputStream(new File(outputFolderPath+"/"+fileName)));
                            document.open();
                            document.newPage();
                            Image image = Image.getInstance(selectedFiles.get(i).getAbsolutePath());
                            image.setAbsolutePosition(0, 0);
                            image.setBorderWidth(0);
                            image.scaleAbsolute(PageSize.A4);
                            document.add(image);
                            document.close();
                            updateProgress(i,selectedFiles.size()-1);
                        }
                        Desktop.getDesktop().open(new File(outputFolderPath));
                    }
                    else if(saveAs=="Single File") {
                        Document document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream(new File(outputFolderPath+"/"+"output.pdf")));
                        document.open();
                        for(int i=0;i<selectedFiles.size();i++){
                            if (stopper) {
                                break;
                            }
                            document.newPage();
                            Image image = Image.getInstance(selectedFiles.get(i).getAbsolutePath());
                            image.setAbsolutePosition(0, 0);
                            image.setBorderWidth(0);
                            image.scaleAbsolute(PageSize.A4);
                            document.add(image);
                            updateProgress(i,selectedFiles.size()-1);
                        }
                        document.close();
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
        imagesField.setText(null);
        inputImages = null;
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
