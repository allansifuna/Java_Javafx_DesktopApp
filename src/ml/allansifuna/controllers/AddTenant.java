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
import java.util.ResourceBundle;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class AddTenant implements Initializable, Controller {
    PreparedStatement statement;
    String queryAgents="select agent.id,agent.firstName,agent.lastName,house.houseNumber from agent inner join house on agent.id = house.agentId where house.id=?";
    String getAgent="select * from agent where firstName= ? and lastName= ?";
    String insetTenant="insert into tenant (id,firstName,lastName,gender,nationalId,agentId) values(?,?,?,?,?,?)";
    String updateHouse="update house set status=?,tenantId=? where id=?";
    ResultSet resultSet;
    public static String teId;
    String houseId,agentId;

    @FXML
    ComboBox<String>cbGender;

    @FXML
    TextField txtFname,txtLname,txtNatId,txtAgentName,txtHouseNumber;

    @FXML
    Label lblMessage,lblFnameError,lblLnameError,lblNatIdError,lblGenderError;

    @FXML
    Button btnAddTenant;
    public void addTenant(){
        if(allFilled()){
            clearLabels();
            if(doValidations()) {
                try{
                    PreparedStatement statement= Main.conn.prepareStatement(insetTenant);
                    String id =UUID.randomUUID().toString();
                    statement.setString(1,id );
                    statement.setString(2,txtFname.getText().trim());
                    statement.setString(3,txtLname.getText().trim());
                    statement.setString(4,cbGender.getSelectionModel().getSelectedItem());
                    statement.setString(5,txtNatId.getText().trim());
                    statement.setString(6, agentId);
                    int myRows=statement.executeUpdate();
                    System.out.println(myRows);
                    PreparedStatement st= Main.conn.prepareStatement(updateHouse);
                    st.setString(1,"True");
                    st.setString(2,id);
                    st.setString(3,ViewHouses.houseId);
                    int affectedRows=st.executeUpdate();
                    printMessage(affectedRows);
                    btnAddTenant.setDisable(true);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }else{
                displayErrors();
            }

        }else{
            fillErrors();
            lblMessage.setText("All Fields MUST be Filled.");
            Border border= new Border(new BorderStroke(Color.ORANGERED, BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(2),new Insets(0,0,0,0))));
        }

    }
    private void printMessage(int affectedRows) throws InterruptedException {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully added a Tenant.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(2),new Insets(0,0,0,0))));
        }else{
            lblMessage.setText("An Error has Occured when adding a Tenant.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
            lblMessage.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(2),new Insets(0,0,0,0))));
        }

    }

    private boolean doValidations() {
        return Validations.validateNatId(txtNatId.getText());
    }
    private void displayErrors() {
        clearLabels();
        System.out.println("labels cleared");
        if(!Validations.validateNatId(txtNatId.getText())){
            fillLabel(lblNatIdError,"Enter A valid ID Number.",Color.ORANGERED);
        }

    }
    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
        lbl.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
    }
    private void fillErrors() {
        clearLabels();
        if(txtNatId.getText().isEmpty()){
            fillLabel(lblNatIdError,"This Field is Required.",Color.ORANGERED);
        }else if(!Validations.validateNatId(txtNatId.getText())){
            fillLabel(lblNatIdError,"Enter A valid ID Number.",Color.ORANGERED);
        }
        if(txtLname.getText().isEmpty()){
            fillLabel(lblLnameError,"This Field is Required.",Color.ORANGERED);
        }
        if(txtFname.getText().isEmpty()){
            fillLabel(lblFnameError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbGender.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblGenderError,"This Field is Required.",Color.ORANGERED);
        }
    }

    private void clearLabels() {
        lblGenderError.setBackground(Background.EMPTY);
        lblFnameError.setBackground(Background.EMPTY);
        lblLnameError.setBackground(Background.EMPTY);
        lblNatIdError.setBackground(Background.EMPTY);
        lblGenderError.setText("");
        lblFnameError.setText("");
        lblLnameError.setText("");
        lblNatIdError.setText("");
    }
    private boolean allFilled() {
        return !(txtFname.getText().isEmpty() || txtLname.getText().isEmpty() ||
                txtNatId.getText().isEmpty() || cbGender.getSelectionModel().getSelectedItem() == null);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.getItems().addAll("Male","Female","Other");
        try{
            PreparedStatement st = Main.conn.prepareStatement(queryAgents);
            st.setString(1,ViewHouses.houseId);
            ResultSet rSet= st.executeQuery();
            txtHouseNumber.setText(rSet.getString("houseNumber"));
            txtAgentName.setText(rSet.getString("firstName")+" "+rSet.getString("lastName"));
            agentId=rSet.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddTenant.fxml"));
    }
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new Dashboard().getContent());
    }
}
