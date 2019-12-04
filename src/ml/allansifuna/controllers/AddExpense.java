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
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.UUID;
public class AddExpense implements Initializable, Controller {

    @FXML
    Label lblExpenseTypeError,lblExpenseAmountError,lblMessage;

    @FXML
    TextField txtExpenseAmount;

    @FXML
    ComboBox<String> cbExpenseType;

    @FXML
    Button btnAddExpense;
    String insertExpense="insert into expense (id,expenseType,date,expenseAmount) values(?,?,?,?)";


    public void addExpense(){
        if(allFilled()){
            clearLabels();
            if(doValidations()) {
                try {
                    PreparedStatement statement = Main.conn.prepareStatement(insertExpense);
                    statement.setString(1,UUID.randomUUID().toString());
                    statement.setString(2,cbExpenseType.getSelectionModel().getSelectedItem());
                    java.util.Date date = new java.util.Date();
                    statement.setDate(3, new Date(date.getTime()));
                    statement.setInt(4,Integer.parseInt(txtExpenseAmount.getText()));
                    int affectedRows=statement.executeUpdate();
                    printMessagge(affectedRows);
                    btnAddExpense.setDisable(true);

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
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new Dashboard().getContent());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbExpenseType.getItems().addAll("Office Equipment Expense","Office Rent","Fuel","Vehicle Maintainance","Miscellaneous Costs");
    }
    private void displayErrors() {
        clearLabels();
        System.out.println("labels cleared");
        if(!Validations.validateRent(txtExpenseAmount.getText())){
            fillLabel(lblExpenseAmountError,"Enter A valid Amount.", Color.ORANGERED);
        }

    }

    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
    }

    private boolean doValidations() {
        return Validations.validateRent(txtExpenseAmount.getText());
    }


    private void fillErrors() {
        clearLabels();
        if(txtExpenseAmount.getText().isEmpty()){
            fillLabel(lblExpenseAmountError,"This Field is Required.",Color.ORANGERED);
        } else if(!Validations.validateRent(txtExpenseAmount.getText())) {
            fillLabel(lblExpenseAmountError, "Enter A valid Amount.", Color.ORANGERED);
        }
        if(cbExpenseType.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblExpenseTypeError,"This Field is Required.",Color.ORANGERED);
        }
    }

    private void clearLabels() {
        lblExpenseAmountError.setText("");
        lblExpenseTypeError.setText("");
        lblMessage.setText("");
        lblMessage.setBorder(Border.EMPTY);
    }

    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully added an Expense.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
        }else{
            lblMessage.setText("An Error has Occured when adding an Expense.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
        }

    }

    private boolean allFilled() {
        return !(txtExpenseAmount.getText().isEmpty() || cbExpenseType.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddExpense.fxml"));
    }
}
