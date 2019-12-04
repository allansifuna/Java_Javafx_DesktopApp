package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;
import ml.allansifuna.validations.Validations;

import javax.print.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.UUID;

public class RemoveTenant implements Initializable, Controller {
    @FXML
    ComboBox<String> cbTenant;

    @FXML
    TextField txtName,txtHouse;

    @FXML
    Button btnLookUp,btnRemoveTenant;

    @FXML
    Label lblMessage,lblNameError,lblTenantError,lblHouseError,lblPaymentTypeError,lblPaymentAmountError;

    String getTenantSQL="select * from tenant where firstName=? or lastName=? and agentId=?";
    String removeTenant="Update house set tenantId=?, status=? where tenantId=?";
    String removeRentTenant="Update rent set tenantId=? where tenantId=?";
    String deleteTenant="Delete from tenant where id=?";
    String getHouseSQL="select t.id,t.houseNumber,a.id ,t.rentAmount from tenant as a, house as t where a.firstName=? and a.lastName=? and a.id=t.tenantId";
    String tenantId,houseId,rentAmount,lastPaymentId=null;
    java.util.Date date = new java.util.Date();


    public void activateLookUp(){
        btnLookUp.setVisible(true);
        txtName.setLayoutY(14.0);
        lblNameError.setLayoutY(55.0);
    }
    public void fillTenant() throws SQLException {
        cbTenant.getItems().clear();
        lblNameError.setText("");
        PreparedStatement statement = Main.conn.prepareStatement(getTenantSQL);
        statement.setString(1,txtName.getText().trim());
        statement.setString(2,txtName.getText().trim());
        statement.setString(3,Main.loggedInId);
        ResultSet rs= statement.executeQuery();
        boolean has=false;
        while (rs.next()){
            has=true;
            cbTenant.getItems().add(rs.getString("firstName")+" "+rs.getString("lastName"));
        }
        if (!has){
            lblNameError.setText("Tenant "+txtName.getText()+" does not Exist");
            lblNameError.setTextFill(Color.ORANGERED);
        }
        btnLookUp.setVisible(false);
        txtName.setLayoutY(61.0);
        lblNameError.setLayoutY(97.0);
    }
    public void fillHouse() throws SQLException {
        PreparedStatement stat = Main.conn.prepareStatement(getHouseSQL);
        String[] name = cbTenant.getSelectionModel().getSelectedItem().split(" ");
        stat.setString(1, name[0]);
        stat.setString(2, name[1]);
        ResultSet rts = stat.executeQuery();
        System.out.println(stat.isClosed());
        boolean check=false;
        while (rts.next()) {
            txtHouse.setText(rts.getString("houseNumber"));
            tenantId = rts.getString(3);
            houseId = rts.getString(1);
            rentAmount = rts.getString(4);
        }
        btnRemoveTenant.setVisible(true);
        /*System.out.println(tenantId+"|"+houseId + "|"+rentAmount)*/;
    }
    public void removeTenant() throws SQLException {
        PreparedStatement statement= Main.conn.prepareStatement(removeTenant);
        statement.setString(1,null);
        statement.setString(2,"False");
        statement.setString(3,tenantId);

        statement.executeUpdate();
        PreparedStatement statements= Main.conn.prepareStatement(removeRentTenant);
        statements.setString(1,null);
        statements.setString(2,tenantId);

        statements.executeUpdate();
        PreparedStatement st =Main.conn.prepareStatement(deleteTenant);
        st.setString(1,tenantId);
        st.executeUpdate();
        lblMessage.setText("You have successfully deleted "+cbTenant.getSelectionModel().getSelectedItem());
        lblMessage.setTextFill(Color.DARKGREEN);
        btnRemoveTenant.setVisible(false);

    }

    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new AgentDashboard().getContent());
    }
    public void printReceipts() throws Exception {
        lblMessage.getScene().setRoot(new Receipt().getContent());
    }

    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
    }

    private void clearLabels() {
        lblNameError.setText("");
        lblHouseError.setText("");
        lblPaymentAmountError.setText("");
        lblPaymentTypeError.setText("");
        lblTenantError.setText("");
        lblMessage.setText("");
        lblMessage.setBorder(Border.EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/RemoveTenant.fxml"));
    }
}
