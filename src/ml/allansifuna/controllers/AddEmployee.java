package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddEmployee implements Initializable, Controller {

    @FXML
    TextField txtFname,txtLname,txtSalaryAmount,txtEmail,txtNatId;

    @FXML
    Label lblFnameError,lblLnameError,lblSalaryAmountError,lblEmpTypeError,lblEmailError,lblNatIdError,lblGenderError,lblMessage,lblPassword;

    @FXML
    ComboBox<String> cbGender,cbEmpType;

    String insertEmployee="insert into employee (id,firstName,lastName,email,nationalId,gender,employeeType,salaryAmount) values(?,?,?,?,?,?,?,?)";
    String insertReceptionist="insert into employee (id,firstName,lastName,email,nationalId,gender,employeeType,salaryAmount,password) values(?,?,?,?,?,?,?,?,?)";

    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new Dashboard().getContent());
    }
    public void addEmployee() {
        if(allFilled()){
            clearLabels();
            if(doValidations()) {
                if(cbEmpType.getSelectionModel().getSelectedItem().equals("Receptionist")){
                    try {
                        String password="admin123";
                        PreparedStatement statement = Main.conn.prepareStatement(insertReceptionist);
                        statement.setString(1, UUID.randomUUID().toString());
                        statement.setString(2, txtFname.getText());
                        statement.setString(3, txtLname.getText());
                        statement.setString(4, txtEmail.getText());
                        statement.setString(5, txtNatId.getText());
                        statement.setString(6, cbGender.getSelectionModel().getSelectedItem());
                        statement.setString(7, cbEmpType.getSelectionModel().getSelectedItem());
                        statement.setString(8, txtSalaryAmount.getText());
                        statement.setString(9, BCrypt.hashpw(password,BCrypt.gensalt()));

                        int affectedRows = statement.executeUpdate();
                        printMessagge(affectedRows);
                        lblPassword.setTextFill(Color.DARKGREEN);
                        Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
                        lblMessage.setBorder(border);
                        lblMessage.setPadding(new Insets(4,4,4,4));
                        lblPassword.setText("Receptionist Password is:"+password);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        PreparedStatement statement = Main.conn.prepareStatement(insertEmployee);
                        statement.setString(1, UUID.randomUUID().toString());
                        statement.setString(2, txtFname.getText());
                        statement.setString(3, txtLname.getText());
                        statement.setString(4, txtEmail.getText());
                        statement.setString(5, txtNatId.getText());
                        statement.setString(6, cbGender.getSelectionModel().getSelectedItem());
                        statement.setString(7, cbEmpType.getSelectionModel().getSelectedItem());
                        statement.setString(8, txtSalaryAmount.getText());

                        int affectedRows = statement.executeUpdate();
                        printMessagge(affectedRows);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            fillLabel(lblNatIdError,"Enter A valid ID Number.", Color.ORANGERED);
        }
        if(!Validations.validateEmail(txtEmail.getText())){
            fillLabel(lblEmailError,"Enter A Valid Email.",Color.ORANGERED);

        }
        if(!Validations.validateRent(txtSalaryAmount.getText())){
            fillLabel(lblSalaryAmountError,"Enter A Valid Salary Amount.",Color.ORANGERED);

        }
    }

    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
    }

    private boolean doValidations() {
        return Validations.validateNatId(txtNatId.getText()) && Validations.validateEmail(txtEmail.getText()) && Validations.validateRent(txtSalaryAmount.getText());
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
        if(txtSalaryAmount.getText().isEmpty()){
            fillLabel(lblSalaryAmountError,"This Field is Required.",Color.ORANGERED);
        }else if(!Validations.validateRent(txtSalaryAmount.getText())){
            fillLabel(lblSalaryAmountError,"Enter A Valid Salary Amount.",Color.ORANGERED);

        }
        if(cbGender.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblGenderError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbEmpType.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblEmpTypeError,"This Field is Required.",Color.ORANGERED);
        }
    }

    private void clearLabels() {
        lblGenderError.setText("");
        lblEmailError.setText("");
        lblFnameError.setText("");
        lblLnameError.setText("");
        lblNatIdError.setText("");
        lblEmpTypeError.setText("");
        lblSalaryAmountError.setText("");
        lblMessage.setText("");
        lblMessage.setBorder(Border.EMPTY);
    }

    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully added an Employee.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
        }else{
            lblMessage.setText("An Error has Occured when adding an Employee.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
        }

    }

    private boolean allFilled() {
        return !(txtEmail.getText().isEmpty() || txtFname.getText().isEmpty() || txtSalaryAmount.getText().isEmpty() || txtLname.getText().isEmpty() ||
                txtNatId.getText().isEmpty() || cbGender.getSelectionModel().getSelectedItem() == null || cbEmpType.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.getItems().addAll("Male","Female","Other");
        cbEmpType.getItems().addAll("Receptionist","Accountant","Office Assistant","Manager");
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddEmployee.fxml"));
    }
}
