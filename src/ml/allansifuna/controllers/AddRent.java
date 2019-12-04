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

public class AddRent implements Initializable, Controller {
    @FXML
    ComboBox<String> cbTenant,cbHouse,cbPaymentType;

    @FXML
    TextField txtName,txtPaymentAmount;

    @FXML
    Button btnLookUp,btnAddAgent,btnPrintReceipt;

    @FXML
    Label lblMessage,lblNameError,lblTenantError,lblHouseError,lblPaymentTypeError,lblPaymentAmountError;

    String getTenantSQL="select * from tenant where firstName=? or lastName=? and agentId=?";
    String getHouseSQL="select t.id,t.houseNumber,a.id ,t.rentAmount from tenant as a, house as t where a.firstName=? and a.lastName=? and a.id=t.tenantId";
    String getLastPayment="select * from rent where tenantId=?";
    String insertRent="insert into rent (id,date,paymentType,tenantid,houseId,amountDue,amountPayed,balance) values(?,?,?,?,?,?,?,?)";
    String checkThisMonthPay="select * from rent where tenantId=? and date > ?";
    String checkThisMonthAnyPay="select * from rent where tenantId=? and date > ? and paymentType=?";
    String hasBalance="select * from rent where tenantId=? and date>? and date<?";
    String firstPay="select * from rent where tenantId=? and date>?";
    String tenantId,houseId,rentAmount,lastPaymentId=null;
    public static String receipt;
    int month,lastBalance,currentBalance,balance,thisMonthPayments,amountDue;
    java.util.Date date = new java.util.Date();
    Date thisMonth = new Date(date.getYear(),date.getMonth(),1);
    Date prevMonth = new Date(date.getYear(),date.getMonth()-1,1);
    public void getBalance(){
        System.out.println("is first"+isFirstPay());
        if(isFirstPay()){

            amountDue=Integer.parseInt(rentAmount)+checkHasBalance();
            System.out.println(checkHasBalance());
            if(amountDue<=0) {
                lblPaymentAmountError.setText("You are supposed to pay Ksh" + String.valueOf(0));
                lblPaymentAmountError.setTextFill(Color.DARKGREEN);
            }else{
                lblPaymentAmountError.setText("You are supposed to pay Ksh"+String.valueOf(amountDue));
                lblPaymentAmountError.setTextFill(Color.DARKGREEN);
            }
            currentBalance=Integer.parseInt(rentAmount)+checkHasBalance();
        }else{
            currentBalance=checkMultiPays();
            if(checkMultiPays()<=0) {
                amountDue = checkMultiPays();
                lblPaymentAmountError.setText("You are supposed to pay Ksh"+String.valueOf(0));
                lblPaymentAmountError.setTextFill(Color.DARKGREEN);
            }else{
                amountDue=checkMultiPays();
                lblPaymentAmountError.setText("You are supposed to pay Ksh"+String.valueOf(amountDue));
                lblPaymentAmountError.setTextFill(Color.DARKGREEN);
            }
            System.out.println(amountDue);
        }

    }
    public boolean isFirstPay(){
        try {
            PreparedStatement statement = Main.conn.prepareStatement(firstPay);
            statement.setString(1,tenantId);
            statement.setDate(2,thisMonth);
            ResultSet rs = statement.executeQuery();
            boolean isFirst=true;
            while(rs.next()){
                isFirst=false;
            }
            return isFirst;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public int checkHasBalance(){
        try {
            PreparedStatement statement = Main.conn.prepareStatement(hasBalance);
            statement.setString(1,tenantId);
            statement.setDate(2,prevMonth);
            statement.setDate(3,thisMonth);
            ResultSet rs=statement.executeQuery();
            int bal=0;
            while(rs.next()){
                bal=rs.getInt("balance");
            }
            return bal;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

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
    public int checkMultiPays(){
        try {
            PreparedStatement statement = Main.conn.prepareStatement(checkThisMonthPay);
            statement.setString(1,tenantId);
            statement.setDate(2,thisMonth);
            ResultSet rs = statement.executeQuery();
            int i= 0;
            boolean in=false;
            while (rs.next()){
                in=true;
                i=rs.getInt("balance");
            }
            if(in) {
                return i;
            }else {
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }
    public void fillHouse() throws SQLException {
        cbHouse.getItems().clear();
        PreparedStatement stat = Main.conn.prepareStatement(getHouseSQL);
            String[] name = cbTenant.getSelectionModel().getSelectedItem().split(" ");
            stat.setString(1, name[0]);
            stat.setString(2, name[1]);
            ResultSet rts = stat.executeQuery();
            System.out.println(stat.isClosed());
            boolean check=false;
            while (rts.next()) {
                cbHouse.getItems().add(rts.getString("houseNumber"));
                tenantId = rts.getString(3);
                houseId = rts.getString(1);
                rentAmount = rts.getString(4);
            }
        /*System.out.println(tenantId+"|"+houseId + "|"+rentAmount)*/;
    }
    public void addRent(){
        if(allFilled()){
            clearLabels();
            if(doValidations()) {
                if(cbPaymentType.getSelectionModel().getSelectedItem().equals("Pay in Advance")){
                    if(Integer.parseInt(txtPaymentAmount.getText())>=amountDue){
                        transact();
                    }else{
                        lblPaymentAmountError.setText("You are required to pay " +String.valueOf(amountDue));
                        lblPaymentAmountError.setTextFill(Color.ORANGERED);
                    }

                }else if(cbPaymentType.getSelectionModel().getSelectedItem().equals("Pay in Instalments")){
                    transact();
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
    private void transact(){
//        System.out.println(checkMultiPays());
        String id=UUID.randomUUID().toString();
        if(checkMultiPays()<0) {
            try {
                balance = 0 - Integer.parseInt(txtPaymentAmount.getText())+checkMultiPays();
                PreparedStatement statement = Main.conn.prepareStatement(insertRent);
                statement.setString(1,id);
                java.util.Date date = new java.util.Date();
                statement.setDate(2, new Date(date.getTime()));
                statement.setString(3, cbPaymentType.getSelectionModel().getSelectedItem());
                statement.setString(4, tenantId);
                statement.setString(5, houseId);
                statement.setInt(6, currentBalance);
                statement.setInt(7, Integer.parseInt(txtPaymentAmount.getText()));
                statement.setInt(8, balance);
                int affectedRows = statement.executeUpdate();
                printMessagge(affectedRows);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                balance = currentBalance - Integer.parseInt(txtPaymentAmount.getText());
                PreparedStatement statement = Main.conn.prepareStatement(insertRent);
                statement.setString(1, id);
                java.util.Date date = new java.util.Date();
                statement.setDate(2, new Date(date.getTime()));
                statement.setString(3, cbPaymentType.getSelectionModel().getSelectedItem());
                statement.setString(4, tenantId);
                statement.setString(5, houseId);
                statement.setInt(6, currentBalance);
                statement.setInt(7, Integer.parseInt(txtPaymentAmount.getText()));
                statement.setInt(8, balance);
                int affectedRows = statement.executeUpdate();
                printMessagge(affectedRows);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        btnAddAgent.setVisible(false);
        btnPrintReceipt.setVisible(true);
        receipt="\n\n\n DoubleA Management Information System\n" +
                "\t     Rent Payment Receipt\t\n\n" +
                " Date : " +new java.util.Date().toString() +"\n" +
                " Transaction Id: "+ id +"\n\n" +
                " Name: "+cbTenant.getSelectionModel().getSelectedItem() +"\n" +
                " House Number: "+cbHouse.getSelectionModel().getSelectedItem()+"\n"+
                "\n" +
                " House Rent:    Ksh. "+rentAmount+".00\n" +
                " Amount Due:   Ksh. "+currentBalance+".00\n"+
                " Amount Paid:  Ksh. "+txtPaymentAmount.getText()+".00\n" +
                "\t      =============\n"+
                " Balance:     Ksh. "+balance+".00\n\n\n";

    }
    public void goBack() throws Exception {
        lblMessage.getScene().setRoot(new AgentDashboard().getContent());
    }
    public void printReceipts() throws Exception {
        lblMessage.getScene().setRoot(new Receipt().getContent());
    }
    private void displayErrors() {
        clearLabels();
        System.out.println("labels cleared");
        if(!Validations.validateRent(txtPaymentAmount.getText())){
            fillLabel(lblPaymentAmountError,"Enter A valid Amount.",Color.ORANGERED);
        }
    }

    private void fillLabel(Label lbl, String text, Color color) {
        lbl.setText(text);
        lbl.setTextFill(color);
    }

    private boolean doValidations() {
        return Validations.validateRent(txtPaymentAmount.getText());
    }


    private void fillErrors() {
        clearLabels();
        if(txtPaymentAmount.getText().isEmpty()){
            fillLabel(lblPaymentAmountError,"This Field is Required.",Color.ORANGERED);
        } else if(!Validations.validateNatId(txtPaymentAmount.getText())){
            fillLabel(lblPaymentAmountError,"Enter A valid Amount.",Color.ORANGERED);
        }

        if(cbTenant.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblTenantError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbPaymentType.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblPaymentTypeError,"This Field is Required.",Color.ORANGERED);
        }
        if(cbHouse.getSelectionModel().getSelectedItem() == null){
            fillLabel(lblHouseError,"This Field is Required.",Color.ORANGERED);
        }
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

    private void printMessagge(int affectedRows) {
        if(affectedRows == 1){
            lblMessage.setText("You have successfully Payed Rent.");
            Border border= new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.DARKGREEN);
        }else{
            lblMessage.setText("An Error has Occured when paying Rent.");
            Border border= new Border(new BorderStroke(Color.ORANGERED,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
            lblMessage.setBorder(border);
            lblMessage.setPadding(new Insets(4,4,4,4));
            lblMessage.setTextFill(Color.ORANGERED);
        }

    }

    private boolean allFilled() {
        return !(txtPaymentAmount.getText().isEmpty() || cbHouse.getSelectionModel().getSelectedItem() == null
                || cbPaymentType.getSelectionModel().getSelectedItem() == null || cbTenant.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbPaymentType.getItems().addAll("Pay in Advance","Pay in Instalments");
        btnLookUp.setTooltip(new Tooltip(("Click to look up tenant")));
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AddRent.fxml"));
    }
}
