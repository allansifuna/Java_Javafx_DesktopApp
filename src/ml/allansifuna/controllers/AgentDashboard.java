package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;

import javax.swing.text.View;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AgentDashboard implements Controller, Initializable {
    @FXML
    Label lblTitle,lblTime;
    @FXML
    Hyperlink hlProfile;
    String loggedIn="Select firstName,lastName from agent where id=?";
    public void viewVaccantHouses() throws Exception {
        lblTitle.getScene().setRoot(new AgentVaccantHouses().getContent());
    }
    public void logout() throws Exception {
        lblTitle.getScene().setRoot(new Login().getContent());
    }
    public void myProfile() throws Exception {
        lblTitle.getScene().setRoot(new MyProfile().getContent());
    }
    public void viewRentReport() throws Exception {
        lblTitle.getScene().setRoot(new ViewRentReport().getContent());
    }
    public void agentRentReport() throws Exception {
        lblTitle.getScene().setRoot(new AgentRentReport().getContent());
    }
    public void viewDefaultedRent() throws Exception {
        lblTitle.getScene().setRoot(new AgentRentDefaulters().getContent());
    }
    public void addRent() throws Exception {
        lblTitle.getScene().setRoot(new AddRent().getContent());
    }
    public void removeTenant() throws Exception {
        lblTitle.getScene().setRoot(new RemoveTenant().getContent());
    }
    public void addHouse() throws Exception {
        lblTitle.getScene().setRoot(new AddHouse().getContent());
    }
    public void viewTenants() throws Exception {
        lblTitle.getScene().setRoot(new ViewTenants().getContent());
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AgentDashboard.fxml"));
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
