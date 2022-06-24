package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import message.AuthMessage;
import message.FileRequestMessage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField statementClient;
    public TextField statementServer;
    @FXML
    public HBox workPanel;
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
    String clientPath = ("C:\\geekbrains\\");
    String serverPath = ("Data\\");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Control.setController(this);
        client = new Client();
        statementClient.setText(clientPath);
        statementServer.setText(serverPath);
        workPanel.setVisible(false);
//        setClientFileList(clientFileList);
//        setServerFileList(serverFileList);
    }

//    private void setAuthenticated (boolean authenticated){
//        this.authenticated = authenticated;
//        authPanel.setVisible(!authenticated);
//        authPanel.setManaged(!authenticated);
//
//        clientFileList.setVisible(authenticated);
//        clientFileList.setManaged(authenticated);
//        serverFileList.setVisible(authenticated);
//        serverFileList.setManaged(authenticated);
//
//    }


    public void setClientFileList(ListView<String> clientFileList) {
        String path = statementClient.getText();
        File folder = new File(path);

        clientFileList.getItems().clear();
        if (!path.equals(clientPath)){
            clientFileList.getItems().add("..");
        }
        List<File>fileList = new ArrayList<>();
        List<File>directoryList = new ArrayList<>();

        for (File file : folder.listFiles()){

            if (file.isDirectory()){
                directoryList.add(file);
            } else {
                fileList.add(file);
            }
        }

        Platform.runLater(()->{
            for (File file : directoryList) {
                clientFileList.getItems().add(file.getName());
            }

            for (File file : fileList) {
                clientFileList.getItems().add(file.getName());
            }
        });
    }

    public void setServerFileList(ListView<String> serverFileList) {
        String path = statementServer.getText();
        File folder = new File(path);

        serverFileList.getItems().clear();
        if (!path.equals(serverPath)){
            serverFileList.getItems().add("..");
        }
        List<File>fileList = new ArrayList<>();
        List<File>directoryList = new ArrayList<>();

        for (File file : folder.listFiles()){

            if (file.isDirectory()){
                directoryList.add(file);
            } else {
                fileList.add(file);
            }
        }

        Platform.runLater(()->{
            for (File file : directoryList) {
                serverFileList.getItems().add(file.getName());
            }

            for (File file : fileList) {
                serverFileList.getItems().add(file.getName());
            }
        });
    }

    public void tryToAuth(ActionEvent actionEvent) {

        AuthMessage authMessage = new AuthMessage();
        authMessage.setLogin(String.format("%s", loginField.getText().trim()));
        authMessage.setPassword(String.format("%s", passwordField.getText().trim()));
        passwordField.clear();
        client.sendMessage(authMessage);

    }

    public void tryToAuth() {
        authPanel.setVisible(false);
        workPanel.setVisible(true);
        setClientFileList(clientFileList);
        setServerFileList(serverFileList);
    }

    public void tryToCopy(ActionEvent actionEvent) {

   final FileRequestMessage frm = new FileRequestMessage();
        String receiver = clientFileList.getSelectionModel().getSelectedItem();
       frm.setPathFrom(statementClient.getText() + receiver);
        frm.setPathTo(statementServer.getText());
        client.sendFileForCopy(frm);

    }

    public void tryToRemove(ActionEvent actionEvent) throws IOException {

        String receiver = clientFileList.getSelectionModel().getSelectedItem();
        Files.delete(Paths.get(statementClient.getText() + receiver));
        tryToRefreshAll();
    }

    public void tryToCopyServer(ActionEvent actionEvent) {

        final FileRequestMessage frm = new FileRequestMessage();
        String receiver = serverFileList.getSelectionModel().getSelectedItem();
        frm.setPathFrom(statementServer.getText() + receiver);
        frm.setPathTo(statementClient.getText());
        client.sendFileForCopy(frm);
    }

    public void tryToRemoveServer(ActionEvent actionEvent) throws IOException {
        String receiver = serverFileList.getSelectionModel().getSelectedItem();
        Files.delete(Paths.get(statementServer.getText() + receiver));
        tryToRefreshAll();
    }

    public void tryToRefresh(ActionEvent actionEvent) {
        setClientFileList(clientFileList);
    }

    public void tryToRefreshServer(ActionEvent actionEvent) {
        setServerFileList(serverFileList);
    }

    public void tryToRefreshAll(){
        setServerFileList(serverFileList);
        setClientFileList(clientFileList);
    }


    public void clickClientFileList(MouseEvent mouseEvent) {
        String receiver = clientFileList.getSelectionModel().getSelectedItem();
        String path = statementClient.getText();
        if (receiver.equals("..")){

            String name = path.substring(0, path.lastIndexOf("\\"));
            String name1 = name.substring(0, name.lastIndexOf("\\") + 1);
            statementClient.setText(name1);
            setClientFileList(clientFileList);
        } else {
            File file = new File(path + receiver);

            if (file.isDirectory()){
                statementClient.setText(file.getPath() + "\\");
                setClientFileList(clientFileList);

            }
        }
    }
}


