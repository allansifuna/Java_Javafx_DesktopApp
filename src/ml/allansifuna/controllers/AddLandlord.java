package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;
import ml.allansifuna.validations.Validations;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddLandlord implements Initializable, Controller {

    @FXML
    ComboBox<String> cbGender;

    @FXML
    TextField txtFname,txtLname,txtNatId,txtEmail;
    @FXML
    Label lblMessage,lblFnameError,lblLnameError,lblGenderError,lblEmailError,lblNatIdError;
    @FXML
    Button btnAddLandlord;
    PreparedStatement statement=null;
    String addAgentSql="insert into landlord (id,firstName,lastName,nationalId,gender,email) values(?,?,?,?,?,?)";
    TextField[] fields = {txtFname,txtLname,txtNatId,txtEmail};
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new Dashboard().getContent());
    }
    public void addLandlord(){
        if(allFilled()){
            clearLabels();
            if(doValidations()) {
                try {
                    statement = Main.conn.prepareStatement(addAgentSql);
                    UUID id=UUID.randomUUID();
                    statement.setString(1, id.toString());
                    statement.setString(2, txtFname.getText().trim());
                    statement.setString(3, txtLname.getText().trim());
                    statement.setString(4, txtNatId.getText().trim());
                    statement.setString(5, cbGender.getSelectionModel().getSelectedItem());
                    statement.setString(6, txtEmail.getText().trim());
                    int affectedRows = statement.executeUpdate();
                    printMessagge(affectedRows);
                    btnAddLandlord.setDisable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                displayErrors();
            }
        }else{
            fillErrors();
            lblMessage.setText("All Fields MUST be Filled.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
        }
    }

    private void displayErrors() {
        clearLabels();
        System.out.println("labels cleared");
        if(!Validations.validateNatId(txtNatId.getText())){
            fillLabel(lblNatIdError,"Enter A valid ID Number.",Color.ORANGERED);
        }
        if(!Validations.validateEmail(txtEmail.getText())){
            fillLabel(lblEmailError,"Enter A Valid Email.",Color.ORANGERED);

        }
    }

    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
    }

    private boolean doValidations() {
        return Validations.validateNatId(txtNatId.getText()) && Validations.validateEmail(txtEmail.getText());
    }


    private void fillErrors() {
        clearLabels();
        if(txtNatId.getText().isEmpty()){
            fillLabel(lblNatIdError,"This Field is Required.",Color.ORANGERED);
        } else if(!Validations.validateNatId(txtNatId.getText())){
            fillLabel(lblNatIdError,"Enter A valid ID Number.",Color.ORANGERED);
        }
        if(txtLname.getText().isEmpty()){
            fillLabel(lblLnameError,"This Field is Required.",Color.ORANGERED);
        }
        if(txtFname.getText().isEmpty()){
            fillLabel(lblFnameError,"This Field is Required.",Color.ORANGERED);
        }
        if(txtEmail.getText().isEmpty()){
            fillLabel(lblEmailError,"This Field is Required.",Color.ORANGERED);
        }else if(!Validations.validateEmail(txtEmail.getText())){
            fillLabel(lblEmailError,"Enter A Valid Email.",Color.ORANGERED);

        }
        if(cbGender.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblGenderError,"This Field is Required.",Color.ORANGERED);
        }
    }

    private void clearLabels() {
        lblGenderError.setText("");
        lblEmailError.setText("");
        lblFnameError.setText("");
        lblLnameError.setText("");
        lblNatIdError.setText("");
    }

    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully added a Landlord.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
        }else{
            lblMessage.setText("An Error has Occured when adding a Landlord.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
        }

    }

    private boolean allFilled() {
        return !(txtEmail.getText().isEmpty() || txtFname.getText().isEmpty() || txtLname.getText().isEmpty() ||
                txtNatId.getText().isEmpty() || cbGender.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.getItems().addAll("Male","Female","Other");

    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddLandlord.fxml"));
    }
}
