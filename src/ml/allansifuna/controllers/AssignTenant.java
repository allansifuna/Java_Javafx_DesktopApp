package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class AssignTenant implements Initializable, Controller {
    String allTenants="select * from tenant";
    String tenant="select * from tenant where id=?";
    String agent ="select * from agent where id = ?";
    String houseNum="select * from house where agentId=? and estate =? and size =? and status=? ";
    String updateHouse="update house set tenantId=?, status=? where houseNumber=?";
    String tenantId;
    String agentId;
    @FXML
    ComboBox<String> cbHouseNumber,cbSize,cbEstate;

    @FXML
    Label lblMessage,lblEstateError,lblSizeError,lblHouseNumberError;

    @FXML
    TextField txtTenantName,txtAgent;
    String id;
    public AssignTenant() {
        this.id=AddTenant.teId;
    }
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new Dashboard().getContent());
    }
    public void initHouseNumber(){
        cbHouseNumber.getItems().clear();
        try {
            PreparedStatement statement = Main.conn.prepareStatement(houseNum);
            statement.setString(1,agentId);
            statement.setString(2,cbEstate.getSelectionModel().getSelectedItem());
            statement.setString(3,cbSize.getSelectionModel().getSelectedItem());
            statement.setString(4,"False");
            ResultSet set = statement.executeQuery();
            while (set.next()){
                cbHouseNumber.getItems().add(set.getString("houseNumber"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
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
    public void assignTenant(){
        if(allFilled()){
            try {

                PreparedStatement statement = Main.conn.prepareStatement(updateHouse);
                statement.setString(1,tenantId);
                statement.setString(2,"True");
                statement.setString(3,cbHouseNumber.getSelectionModel().getSelectedItem());
                int affectedRows = statement.executeUpdate();
                printMessagge(affectedRows);
            } catch (Exception e) {
                e.printStackTrace();
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
    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully Assigned a House.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
        }else{
            lblMessage.setText("An Error has Occured when Assigning a House.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
        }

    }
    private void fillErrors() {
        clearLabels();
        if(cbSize.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblSizeError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbEstate.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblEstateError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbHouseNumber.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblHouseNumberError,"This Field is Required.",Color.ORANGERED);
        }
    }
    private void clearLabels() {
        lblEstateError.setBackground(Background.EMPTY);
        lblSizeError.setBackground(Background.EMPTY);
        lblHouseNumberError.setBackground(Background.EMPTY);
        lblEstateError.setText("");
        lblSizeError.setText("");
        lblHouseNumberError.setText("");
    }
    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
        lbl.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
    }
    private boolean allFilled() {
        return !(cbEstate.getSelectionModel().getSelectedItem() == null || cbSize.getSelectionModel().getSelectedItem() == null
                || cbHouseNumber.getSelectionModel().getSelectedItem() == null
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEstate.getItems().addAll("Low Income","Middle Income","High Income");
        txtTenantName.setText(getTenantName());
        txtAgent.setText(getAgentName());

    }

    private String getAgentName() {
        PreparedStatement tenants,lastTenant,agentName;
        ResultSet resultSet,rSet;
        try {
            lastTenant=Main.conn.prepareStatement(tenant);
            lastTenant.setString(1,id);

            resultSet=lastTenant.executeQuery();
            agentName=Main.conn.prepareStatement(agent);
            agentName.setString(1,resultSet.getString("agentId"));
            rSet= agentName.executeQuery();
            agentId=rSet.getString("id");
            return rSet.getString("firstName")+" "+rSet.getString("lastName");
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getTenantName(){
        PreparedStatement tenants,lastTenant;
        ResultSet resultSet;
        try {
            lastTenant=Main.conn.prepareStatement(tenant);
            lastTenant.setString(1,id);

            resultSet=lastTenant.executeQuery();
            tenantId=resultSet.getString("id");
            return  resultSet.getString("firstName")+" "+resultSet.getString("lastName");
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AssignTenant.fxml"));
    }
}
