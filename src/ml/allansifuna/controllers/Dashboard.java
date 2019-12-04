package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;


public class Dashboard implements Controller, Initializable {
    @FXML
    Label lblTitle,lblTime;

    @FXML
    Hyperlink hlProfile;

    String loggedIn="Select firstName,lastName from employee where id=?";
    public void addAgent() throws Exception {
        lblTitle.getScene().setRoot(new AddAgent().getContent());
    }
    public void viewVaccantHouses() throws Exception {
        lblTitle.getScene().setRoot(new ViewVaccantHouses().getContent());
    }
    public void viewReport() throws Exception {
        lblTitle.getScene().setRoot(new ViewReport().getContent());
    }
    public void viewRentReport() throws Exception {
        lblTitle.getScene().setRoot(new ViewRentReport().getContent());
    }
    public void viewDefaultedRent() throws Exception {
        lblTitle.getScene().setRoot(new ViewRentDefaulters().getContent());
    }
    public void viewPaymentHistory() throws Exception {
        lblTitle.getScene().setRoot(new ViewPaymentHistory().getContent());
    }
    public void startExpenses() throws Exception {
        lblTitle.getScene().setRoot(new ViewExpenses().getContent());
    }
    public void addEmployee() throws Exception {
        lblTitle.getScene().setRoot(new AddEmployee().getContent());
    }
    public void addTenant() throws Exception {
        lblTitle.getScene().setRoot(new ViewHouses().getContent());
    }
    public void addLandlord() throws Exception {
        lblTitle.getScene().setRoot(new AddLandlord().getContent());
    }
    public void addExpense() throws Exception {
        lblTitle.getScene().setRoot(new AddExpense().getContent());
    }
    public void myProfile() throws Exception {
        lblTitle.getScene().setRoot(new EmployeeProfile().getContent());
    }
    public void logout() throws Exception {
        Main.loggedInId=null;
        lblTitle.getScene().setRoot(new Login().getContent());
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/Dashboard.fxml"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            PreparedStatement st = Main.conn.prepareStatement(loggedIn);
            st.setString(1,Main.loggedInId);
            ResultSet set = st.executeQuery();
            Paint paint = Paint.valueOf("0c39ff");
            hlProfile.setText(set.getString(1)+" "+set.getString(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Date date = new Date();
                    lblTime.setText(date.toString());
                    Paint paint = Paint.valueOf("0c39ff");
                    lblTime.setTextFill(paint);
                    lblTime.setFont(Font.font(15));
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();*/
    }
}
