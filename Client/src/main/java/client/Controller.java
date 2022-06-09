package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import message.FileRequestMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Client client;

    @FXML
    public ListView<String> clientFileList;
    @FXML
    public ListView<String> serverFileList;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    private boolean authenticated;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new Client();
        setClientFileList(clientFileList);
        setServerFileList(serverFileList);
    }

    private void setAuthenticated (boolean authenticated){
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);

        clientFileList.setVisible(authenticated);
        clientFileList.setManaged(authenticated);
        serverFileList.setVisible(authenticated);
        serverFileList.setManaged(authenticated);

    }

    public void setClientFileList(ListView<String> clientFileList) {

        File file = new File("C:\\");

        String[] fileList = file.list();

        Platform.runLater(()->{
            clientFileList.getItems().clear();
            for (String str : fileList) {
                System.out.println(str);
                clientFileList.getItems().add(str);
            }
        });

    }

    public void setServerFileList(ListView<String> serverFileList) {
        File file = new File("Data");
        String path = file.getAbsolutePath();

        String[] fileList = file.list();

        Platform.runLater(()->{
            serverFileList.getItems().clear();
            for (String str : fileList) {
                System.out.println(str);
                serverFileList.getItems().add(str);
            }
        });

    }

    public void tryToAuth(ActionEvent actionEvent) {


    }

    public void tryToCopy(ActionEvent actionEvent) {

   final FileRequestMessage frm = new FileRequestMessage();
        String receiver = clientFileList.getSelectionModel().getSelectedItem();
       frm.setPath("C:\\" + receiver);
        System.out.println(frm.getPath());
        client.sendFileClient(frm);

    }

    public void tryToRemove(ActionEvent actionEvent) throws IOException {
        String receiver = clientFileList.getSelectionModel().getSelectedItem();
        Files.delete(Paths.get("C:\\" + receiver));
    }

    public void tryToCopyServer(ActionEvent actionEvent) {

//        final FileRequestMessage frm = new FileRequestMessage();
//        String receiver = serverFileList.getSelectionModel().getSelectedItem();
//        frm.setPath("Data\\" + receiver);
//        System.out.println(frm.getPath());
//        client.sendFileClient(frm);
    }

    public void tryToRemoveServer(ActionEvent actionEvent) throws IOException {
        String receiver = serverFileList.getSelectionModel().getSelectedItem();
        Files.delete(Paths.get("Data\\" + receiver));
    }

    public void tryToRefresh(ActionEvent actionEvent) {
        setClientFileList(clientFileList);
    }

    public void tryToRefreshServer(ActionEvent actionEvent) {
        setServerFileList(serverFileList);
    }
}
