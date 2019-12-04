package ml.allansifuna.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewReport implements Initializable {
    @FXML
    ComboBox<String> cbRoles;
    @FXML
    TableView tblRoles;
    @FXML
    Label lblTitle;
    java.util.Date today = new java.util.Date();
    String amountCollected;
    Date firstDayLastMont=new Date(today.getYear(),today.getMonth()-1,1);
    Date firstDayThisMonth=new Date(today.getYear(),today.getMonth(),1);
    private ObservableList<ObservableList> data;
    String allAgents="select firstName,lastName,id,email from agent";
    String allLandlords="select firstName,lastName,id,email from landlord";
    String allLandlordHouses="select sum(amountPayed) from rent inner join house on rent.houseId=house.id  where house.landlordId=? and rent.date>? and rent.date<?";
    ArrayList<String> allAgentIds= new ArrayList<>();
    String forEachAgent="select sum(amountPayed) from rent inner join house on rent.houseId=house.id inner join agent on house.agentId = agent.id where agent.id=? and rent.date>? and rent.date<?";
    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewReport.fxml"));
    }
    public void goBack() throws Exception {
        cbRoles.getScene().setRoot(new Dashboard().getContent());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbRoles.getItems().clear();
        cbRoles.getItems().addAll("Agents","Landlords");
        cbRoles.getSelectionModel().select(0);
        cbRoles.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doSwitch();
            }
        });
        fillAgents();
        fillTitle();
    }

    private void fillTitle() {
        StringBuilder builder = new StringBuilder();
        builder.append("Remmitance Summary for ");
        builder.append(ViewExpenses.getMonth(firstDayLastMont.getMonth()));
        builder.append(" ");
        builder.append(firstDayLastMont.getYear()+1900);

        lblTitle.setText(builder.toString().toUpperCase());
        lblTitle.setTextFill(Paint.valueOf("#0C39FF"));
    }

    private void doSwitch(){
        if(cbRoles.getSelectionModel().getSelectedIndex()==0){
            fillAgents();
        }else {
            fillLandlords();
        }
    }

    private void fillAgents() {
        fetColumnList();
        try {
            fetRowlist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fillLandlords() {
        fetColumnListL();
        try {
            fetRowlistL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void fetColumnList(){
        try  {
            tblRoles.getColumns().clear();
            PreparedStatement statement = Main.conn.prepareStatement(allAgents);
            ResultSet rs = statement.executeQuery();
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==0){
                    col= new TableColumn("AGENT");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==2){
                    col= new TableColumn("MONEY COLLECTED");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==3){
                    col= new TableColumn("AMOUNT PAYED.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==1){
                    continue;
                }else {
                    col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }
                tblRoles.getColumns().removeAll(col);
                tblRoles.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void fetColumnListL(){
        try  {
            tblRoles.getColumns().clear();
            PreparedStatement statement = Main.conn.prepareStatement(allLandlords);
            ResultSet rs = statement.executeQuery();
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==0){
                    col= new TableColumn("LandLord");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==2){
                    col= new TableColumn("MONEY COLLECTED");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==3){
                    col= new TableColumn("AMOUNT PAYED.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==1){
                    continue;
                }else {
                    col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }
                tblRoles.getColumns().removeAll(col);
                tblRoles.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(allAgents);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==1){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(2));
                }else if(i==3){
                    row.add("Ksh."+getPaid(resultSet.getString("id")));
                }else if(i==4){
                    row.add("Ksh."+String.valueOf((int)(Integer.parseInt(getPaid(resultSet.getString("id")))*0.01)));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblRoles.getItems().clear();
        tblRoles.setItems(data);
    }
    public  void fetRowlistL() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(allLandlords);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==1){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(2));
                }else if(i==3){
                    row.add("Ksh."+getPaidL(resultSet.getString("id")));
                }else if(i==4){
                    row.add("Ksh."+String.valueOf((int)(Integer.parseInt(getPaidL(resultSet.getString("id")))*0.95)));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblRoles.getItems().clear();
        tblRoles.setItems(data);
    }

    private String getPaid(String id) throws SQLException {
        PreparedStatement st = Main.conn.prepareStatement(forEachAgent);
        st.setString(1,id);
        st.setDate(2,firstDayLastMont);
        st.setDate(3,firstDayThisMonth);
        amountCollected= st.executeQuery().getString(1);
        if(amountCollected!=null) {
            return amountCollected;
        }else{
            return "0";
        }
    }
    private String getPaidL(String id) throws SQLException {
        PreparedStatement st = Main.conn.prepareStatement(allLandlordHouses);
        st.setString(1,id);
        st.setDate(2,firstDayLastMont);
        st.setDate(3,firstDayThisMonth);
        amountCollected= st.executeQuery().getString(1);
        if(amountCollected!=null) {
            return amountCollected;
        }else{
            return "0";
        }
    }

}
