package com.tarnovskiy.client;

import com.tarnovskiy.server.DB.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller {
    @FXML
    TextArea sendMsg, newLogin, newNick;
    @FXML
    TextField textField, loginField;
    @FXML
    HBox upperPanel, bottomPanel, sendPanel;
    @FXML
    PasswordField passwordField, newPassword;
    @FXML
    ListView<String> clientList;
    @FXML
    Button buttonRegistration, buttonRegistrationClient;
    @FXML
    Pane registrationPanel;

    private boolean isAuthorized;
    private boolean isRegistration;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADPRESS = "localhost";
    final int PORT = 8189;


    public void connect() {
        try {
            socket = new Socket(IP_ADPRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Controller.this.authorizationClient();
                    Controller.this.readText();
                    Controller.this.showPanelWhenLoggedIn(false);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authorizationClient() {
        try{
            while (true) {
                String str = in.readUTF();
                if (str.startsWith("/authok")){
                    showPanelWhenLoggedIn(true);
                    break;
                } else {
                    sendMsg.appendText(str + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readText() {
        try {
            while (true) {
                String str = in.readUTF();
                if (str.equals("/serverClosed")) {
                    break;
                }
                if (str.startsWith("/clientlist ")) {
                    final String[] tokens = str.split(" ");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientList.getItems().clear();
                            for (int i = 1; i < tokens.length; i++) {
                                clientList.getItems().add(tokens[i]);
                            }
                        }
                    });
                } else {
                    sendMsg.appendText(str + "\n");
                }
                if (str.startsWith("/clear")){
                    sendMsg.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showPanelWhenLoggedIn(false);
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed())
            connect();
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrationClient(ActionEvent actionEvent) {
        if (newLogin.getText() != null &&
                newPassword.getText() != null &&
                newNick.getText() != null &&
                !AuthService.isLoginAndNickDb(newLogin.getText(), newNick.getText())){
            AuthService.addClientDb(newLogin.getText(), newPassword.getText(), newNick.getText());
            clearAllWindows();
            back(actionEvent);
        } else {
            clearAllWindows();
            //TODO добавить появление "такой логин/никнейм существует"
        }

    }

    private void clearAllWindows() {
        newLogin.clear();
        newPassword.clear();
        newNick.clear();
    }

    public void Dispose() {
        System.out.println("Отправляем сообщение на сервер о завершении работы");
        try {
            if (out != null) {
                out.writeUTF("/end");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(ActionEvent actionEvent) {
        showPanelRegistration(true);
    }

    public void showPanelWhenLoggedIn(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
        if(!isAuthorized){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
            buttonRegistration.setVisible(true);
            buttonRegistration.setManaged(true);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
            buttonRegistration.setVisible(false);
            buttonRegistration.setManaged(false);
        }
    }

    private void showPanelRegistration(boolean isRegistration) {
        this.isRegistration = isRegistration;
        if(isRegistration){
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            buttonRegistration.setManaged(false);
            buttonRegistration.setVisible(false);
            registrationPanel.setVisible(true);
            registrationPanel.setManaged(true);
            sendPanel.setVisible(false);
            sendPanel.setManaged(false);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            buttonRegistration.setManaged(true);
            buttonRegistration.setVisible(true);
            registrationPanel.setVisible(false);
            registrationPanel.setManaged(false);
            sendPanel.setVisible(true);
            sendPanel.setManaged(true);
            newLogin.clear();
            newPassword.clear();
            newNick.clear();
        }
    }

    public void back(ActionEvent actionEvent) {
        showPanelRegistration(false);
    }
}

