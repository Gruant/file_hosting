package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AuthenticationController {

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    private Stage dialogStage;
    @FXML
    private Button confirm;
    protected Controller main;


    @FXML
    public void initialize() {

    }

    public void btnConfirm(ActionEvent actionEvent) {
        if(isInputValid()) {
            main.setLogin(login.getText());
            main.setPassword(password.getText());
            Stage stage = (Stage) confirm.getScene().getWindow();
            stage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (login.getText() == null || login.getText().length() == 0){
            errorMessage += "No valid login!\n";
        }
        if (password.getText() == null || password.getText().length() == 0){
            errorMessage += "No valid password!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
