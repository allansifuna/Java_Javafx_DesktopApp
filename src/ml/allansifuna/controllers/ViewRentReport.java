package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewRentReport implements Initializable {
    @FXML
    TextField txtRentCollcted,txtToLandlords,txtToAgents,txtExpenses,txtSalaries,txtProfit;
    @FXML
    Label lblTitle;
    int totalRent,toLandlords,toAgents,expenses,salaries,profit;
    java.util.Date today = new java.util.Date();
    Date firstDayLastMont=new Date(today.getYear(),today.getMonth()-1,1);
    Date firstDayThisMonth=new Date(today.getYear(),today.getMonth(),1);
    String allrent="select sum(amountPayed) from rent where date>? and date<?";
    String allSalaries="select sum(salaryAmount) from salary where date>? and date<?";
    String allExpenses="select sum(expenseAmount) from expense where date>? and date<?";
    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewRentReport.fxml"));
    }
    public void goBack() throws Exception {
        txtRentCollcted.getScene().setRoot(new Dashboard().getContent());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle();
        getCollectedRent();
        remitToLandlords(totalRent);
        remitToAgents(totalRent);
        getExpenses();
        getSalaries();
        getProfit();
    }

    private void setTitle() {
        StringBuilder builder = new StringBuilder();
        builder.append("Rent Summary for ");
        builder.append(ViewExpenses.getMonth(firstDayLastMont.getMonth()));
        builder.append(" ");
        builder.append(firstDayLastMont.getYear()+1900);

        lblTitle.setText(builder.toString().toUpperCase());
        lblTitle.setTextFill(Paint.valueOf("#0C39FF"));
    }

    private void getProfit() {
        int allExpenses=toAgents+toLandlords+salaries+expenses;
        txtProfit.setText("Ksh."+String.valueOf(totalRent-allExpenses));
    }

    private void remitToAgents(int totalRent) {
        toAgents= (int) Math.round(0.01*totalRent);
        txtToAgents.setText("Ksh."+String.valueOf(toAgents));
    }

    private void remitToLandlords(int totalRent) {
        toLandlords= (int) Math.round(0.95*totalRent);
        txtToLandlords.setText("Ksh."+String.valueOf(toLandlords));
    }

    public void getCollectedRent() {
        try {
            PreparedStatement st = Main.conn.prepareStatement(allrent);
            st.setDate(1,firstDayLastMont);
            st.setDate(2,firstDayThisMonth);
            ResultSet set = st.executeQuery();
            StringBuilder builder = new StringBuilder();
            builder.append("Ksh.");
            totalRent=set.getInt(1);
            builder.append(totalRent);
            txtRentCollcted.setText(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getExpenses() {
        try {
            PreparedStatement st = Main.conn.prepareStatement(allExpenses);
            st.setDate(1,firstDayLastMont);
            st.setDate(2,firstDayThisMonth);
            ResultSet set = st.executeQuery();
            StringBuilder builder = new StringBuilder();
            builder.append("Ksh.");
            expenses=set.getInt(1);
            builder.append(expenses);
            txtExpenses.setText(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getSalaries() {
        try {
            PreparedStatement st = Main.conn.prepareStatement(allSalaries);
            st.setDate(1,firstDayLastMont);
            st.setDate(2,firstDayThisMonth);
            ResultSet set = st.executeQuery();
            StringBuilder builder = new StringBuilder();
            builder.append("Ksh.");
            salaries=set.getInt(1);
            builder.append(salaries);
            txtSalaries.setText(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
