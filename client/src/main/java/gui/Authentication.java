package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;

public class Authentication {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    private Controller mainController;

    public Authentication(Controller mainController){
        this.mainController = mainController;
    }

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authentication.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(
                new Scene(loader.load(), 400, 400)
        );
        stage.showAndWait();
    }


    public void btnConfirm(ActionEvent actionEvent) {
        mainController.setData(login.getText(), password.getText());
    }
}
