package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;
import ml.allansifuna.validations.Validations;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MyProfile implements Initializable, Controller {
    @FXML
    TextField txtFname,txtLname,txtEmail,txtNatId,txtPassword;
    @FXML
    Label lblTitle,lblPasswordError,lblEmailError;
    String update="Update agent set firstName=?,lastName=?,email=?,nationalId=? where id =?";
    String updatePassword="Update agent set firstName=?,lastName=?,email=?,nationalId=?,password=? where id =?";
    String getAgent="Select * from agent where id=?";

    public void  update() throws SQLException {
        clearLabels();
        if(validateFields()) {
            PreparedStatement statement;
            if (txtPassword.getText().isEmpty() || txtPassword.getText().trim().length() == 0) {
                statement = Main.conn.prepareStatement(update);
                statement.setString(1, txtFname.getText().trim());
                statement.setString(2, txtLname.getText().trim());
                statement.setString(3, txtEmail.getText().trim());
                statement.setString(4, txtNatId.getText().trim());
                statement.setString(5, Main.loggedInId);
            } else {
                statement = Main.conn.prepareStatement(updatePassword);
                statement.setString(1, txtFname.getText().trim());
                statement.setString(2, txtLname.getText().trim());
                statement.setString(3, txtEmail.getText().trim());
                statement.setString(4, txtNatId.getText().trim());
                statement.setString(5, BCrypt.hashpw(txtPassword.getText().trim(), BCrypt.gensalt()));
                statement.setString(6, Main.loggedInId);
            }


            int aff = statement.executeUpdate();
            if (aff == 1) {
                lblTitle.setText("You have successfully updated your Profile");
                lblTitle.setTextFill(Color.DARKGREEN);
            } else {
                lblTitle.setText("An Error occurred when updating your profile");
                lblTitle.setTextFill(Color.CORAL);
            }
        }else{
            fillErrors();
        }
    }

    private void fillErrors() {
        if(!Validations.validateEmail(txtEmail.getText())){
            lblEmailError.setText("Enter a valid Email");
            lblEmailError.setTextFill(Color.CORAL);
        }
        if(txtPassword.getText().isEmpty()){

        }else if(!(txtPassword.getText().length()>=8)){
            lblPasswordError.setText("Too Short password");
            lblPasswordError.setTextFill(Color.CORAL);
        }
    }

    private void clearLabels() {
        lblTitle.setText("");
        lblEmailError.setText("");
        lblPasswordError.setText("");

    }

    private boolean validateFields() {
        boolean password = false;
        if(txtPassword.getText().isEmpty()){
            password=true;
        }else{
            password=txtPassword.getText().length()>=8;
        }
        return password && Validations.validateEmail(txtEmail.getText());
    }

    public void fillFields() throws SQLException {
        PreparedStatement statement = Main.conn.prepareStatement(getAgent);
        statement.setString(1,Main.loggedInId);
        ResultSet set = statement.executeQuery();
        txtEmail.setText(set.getString("email"));
        txtFname.setText(set.getString("firstName"));
        txtLname.setText(set.getString("lastName"));
        txtNatId.setText(set.getString("nationalId"));

    }

    public void goBack() throws Exception {
        lblTitle.getScene().setRoot(new AgentDashboard().getContent());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fillFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/MyProfile.fxml"));
    }
}
