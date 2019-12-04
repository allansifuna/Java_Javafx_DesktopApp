package ml.allansifuna.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;
import ml.allansifuna.validations.Validations;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Login implements Controller {
    @FXML
    TextField txtEmail;

    @FXML
    PasswordField txtPassword;

    @FXML
    Label lblError,lblEmailError,lblPasswordError;



    public void login() {
        /*WritableImage snapshot = lblError.getScene().snapshot(null);*/
        System.out.println(Main.loggedInId);
        String loginEm = "Select * from employee where email=?";
        String loginA = "Select * from agent where email=?";
        clearLabels();
        if (validateFields()) {
            try {
                PreparedStatement st = Main.conn.prepareStatement(loginA);
                st.setString(1, txtEmail.getText());
                ResultSet set = st.executeQuery();
                boolean has = false;
                String password = null;
                String id = null;
                while (set.next()) {
                    id = set.getString("id");
                    password = set.getString("password");
                    has = true;
                }
                if (has) {
                    System.out.println("hasAgent");
                    boolean auth = BCrypt.checkpw(txtPassword.getText(), password);
                    if (auth) {
                        Main.loggedInId = id;
                        txtEmail.getScene().setRoot(new AgentDashboard().getContent());
                    } else {
                        fillLabel(lblError,"Wrong email or password",Color.RED);
                    }
                } else {
                    System.out.println("pass Agent");
                    PreparedStatement stat = Main.conn.prepareStatement(loginEm);
                    stat.setString(1, txtEmail.getText().trim());
                    ResultSet rSet = stat.executeQuery();
                    boolean hase = false;
                    String ide=null;
                    String passworde = null;
                    while (rSet.next()) {
                        ide = rSet.getString("id");
                        passworde = rSet.getString("password");
                        hase = true;
                    }
                    System.out.println(hase);
                    if (hase) {
                        boolean auth = BCrypt.checkpw(txtPassword.getText(), passworde);
                        if (auth) {
                            Main.loggedInId = ide;
                            txtEmail.getScene().setRoot(new Dashboard().getContent());
                        } else {
                            fillLabel(lblError,"Wrong email or password",Color.CORAL);
                        }
                    } else {
                        fillLabel(lblError,"Wrong email or password",Color.CORAL);
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            fillErrors();
        }

    }
    public void close(){
        Platform.exit();
    }
    private void clearLabels() {
        lblPasswordError.setBackground(Background.EMPTY);
        lblEmailError.setBackground(Background.EMPTY);
        lblError.setBackground(Background.EMPTY);
        lblPasswordError.setBorder(Border.EMPTY);
        lblEmailError.setBorder(Border.EMPTY);
        lblError.setBorder(Border.EMPTY);
        lblEmailError.setText("");
        lblError.setText("");
        lblPasswordError.setText("");
    }
    private void fillLabel(Label lbl, String text, Color color) {
        Border border= new Border(new BorderStroke(color,BorderStrokeStyle.SOLID, new CornerRadii(2),BorderStroke.THIN));
        lbl.setBorder(border);
        lbl.setPadding(new Insets(4,4,4,4));
        lbl.setText(text);
        lbl.setTextFill(color);
        lbl.setBackground(new Background(new BackgroundFill( Color.WHITE,new CornerRadii(0),new Insets(1,0,1,0))));
    }
    public void fillErrors(){
        if(txtEmail.getText().isEmpty()){
            fillLabel(lblEmailError,"This Field is Required",Color.CORAL);
        }
        if(txtPassword.getText().isEmpty()){
            fillLabel(lblPasswordError,"This Field is Required",Color.CORAL);
        }else if(txtPassword.getText().length()<8){
            fillLabel(lblPasswordError,"Too short password",Color.CORAL);
        }
        if(!Validations.validateEmail(txtEmail.getText())){
            fillLabel(lblEmailError,"Enter valid Email",Color.CORAL);
        }

    }

    private boolean validateFields() {
        return (!txtEmail.getText().isEmpty() && !txtPassword.getText().isEmpty() && Validations.validateEmail(txtEmail.getText()) && txtPassword.getText().length()>7);
    }

    @Override
    public Parent getContent() throws Exception {
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/Login.fxml"));
    }
}
