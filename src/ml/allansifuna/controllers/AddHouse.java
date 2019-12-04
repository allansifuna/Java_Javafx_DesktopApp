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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;
import ml.allansifuna.validations.Validations;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddHouse implements Initializable, Controller {
    String getLandlords="select * from landlord";
    String getLandlordId="Select * from landlord where firstName=? and lastName=?";
    String insertHouse="Insert into house (id,estate,size,landlordId,agentId,status,rentAmount,houseNumber) values(?,?,?,?,?,?,?,?)";

    @FXML
    ComboBox<String> cbLandlord,cbSize,cbEstate;

    @FXML
    Label lblMessage,lblEstateError,lblSizeError,lblLandlordError,lblRentAmountError,lblHouseNumberError;

    @FXML
    TextField txtRentAmount,txtHouseNumber;
    @FXML
    Button btnAddHouse;
    public void initSize(){
        cbSize.getItems().clear();
        if(cbEstate.getSelectionModel().getSelectedItem().equals("Low Income")){
            cbSize.getItems().addAll("Small","Medium");
        }else if(cbEstate.getSelectionModel().getSelectedItem().equals("Middle Income")){
            cbSize.getItems().addAll("Medium","Big");
        }else if(cbEstate.getSelectionModel().getSelectedItem().equals("High Income")){
            cbSize.getItems().addAll("Big");
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResultSet res;

        cbEstate.getItems().addAll("Low Income","Middle Income","High Income");
        try {
            PreparedStatement lStat=Main.conn.prepareStatement(getLandlords);
            res=lStat.executeQuery();
            while (res.next()){
                cbLandlord.getItems().add(res.getString("firstName")+" "+res.getString("lastName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public void addHouse() throws SQLException{
        if(allFilled()){
            if(doValidations()) {

                try {

                    PreparedStatement statement = Main.conn.prepareStatement(insertHouse);
                    UUID id = UUID.randomUUID();
                    statement.setString(1, id.toString());
                    statement.setString(2, cbEstate.getSelectionModel().getSelectedItem());
                    statement.setString(3, cbSize.getSelectionModel().getSelectedItem());
                    statement.setString(4, LandlordId());
                    statement.setString(5,Main.loggedInId);
                    statement.setString(6, "False");
                    statement.setInt(7, Integer.parseInt(txtRentAmount.getText()));
                    statement.setString(8, txtHouseNumber.getText().trim());
                    int affectedRows = statement.executeUpdate();
                    printMessagge(affectedRows);
                    btnAddHouse.setDisable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{

            }
        }else{
            fillErrors();
            lblMessage.setText("All Fields MUST be Filled.");
            Border border= new Border(new BorderStroke(Color.ORANGERED, BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));

        }
    }

    private boolean doValidations() {
        if(validateRent()) {
            clearLabels();
            switch (cbSize.getSelectionModel().getSelectedItem()) {
                case "Small":
                    if (Integer.parseInt(txtRentAmount.getText()) >= 3000 && Integer.parseInt(txtRentAmount.getText()) <= 8000) {
                        return true;
                    } else {
                        fillLabel(lblRentAmountError, "Rent should range between 3000-8000.", Color.ORANGERED);
                        return false;
                    }
                case "Medium":
                    if (Integer.parseInt(txtRentAmount.getText()) >= 8000 && Integer.parseInt(txtRentAmount.getText()) <= 15000) {
                        return true;
                    } else {
                        fillLabel(lblRentAmountError, "Rent should range between 8000-15000.", Color.ORANGERED);
                        return false;
                    }
                case "Big":
                    if (Integer.parseInt(txtRentAmount.getText()) > 8000) {
                        return true;
                    } else {
                        fillLabel(lblRentAmountError, "Rent should be greater than 15000", Color.ORANGERED);
                        return false;
                    }
                default:
                    return false;
            }
        }else{
            fillLabel(lblRentAmountError, "Enter a valid Amount.", Color.ORANGERED);
            return false;
        }
    }

    private boolean validateRent() {
        return Validations.validateRent(txtRentAmount.getText().trim());
    }

    private void fillErrors() {
        clearLabels();
        if(cbSize.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblSizeError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbLandlord.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblLandlordError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbEstate.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblEstateError,"This Field is Required.",Color.ORANGERED);
        }
        if(txtRentAmount.getText().isEmpty()){
            fillLabel(lblRentAmountError,"This Field is Required.",Color.ORANGERED);
        }
        if(txtHouseNumber.getText().isEmpty()){
            fillLabel(lblHouseNumberError,"This Field is Required.",Color.ORANGERED);
        }
    }

    private void clearLabels() {
        lblEstateError.setBackground(Background.EMPTY);
        lblLandlordError.setBackground(Background.EMPTY);
        lblSizeError.setBackground(Background.EMPTY);
        lblRentAmountError.setBackground(Background.EMPTY);
        lblHouseNumberError.setBackground(Background.EMPTY);
        lblEstateError.setText("");
        lblLandlordError.setText("");
        lblSizeError.setText("");
        lblRentAmountError.setText("");
        lblHouseNumberError.setText("");
    }
    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
        lbl.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
    }
    public String LandlordId() throws SQLException{
        ResultSet res;
        PreparedStatement stat=Main.conn.prepareStatement(getLandlordId);
        String[] name=cbLandlord.getSelectionModel().getSelectedItem().split(" ");
        stat.setString(1,name[0]);
        stat.setString(2,name[1]);
        res=stat.executeQuery();
        return res.getString("id");
    }
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new AgentDashboard().getContent());
    }
    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully added a House.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
        }else{
            lblMessage.setText("An Error has Occured when adding a House.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
        }

    }

    private boolean allFilled() {
        return !(txtRentAmount.getText().isEmpty() || cbEstate.getSelectionModel().getSelectedItem() == null ||
                cbSize.getSelectionModel().getSelectedItem() == null || cbLandlord.getSelectionModel().getSelectedItem() == null || txtHouseNumber.getText().isEmpty()
        );
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddHouse.fxml"));
    }
}
