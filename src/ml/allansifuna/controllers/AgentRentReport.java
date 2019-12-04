package ml.allansifuna.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AgentRentReport implements Controller, Initializable {
    @FXML
    TableView tblHouses;
    java.util.Date date=new java.util.Date();
    Date firstDayThisMonth=new Date(date.getYear(),date.getMonth(),1);
    private ObservableList<ObservableList> data;
    String tenants="select tenant.firstName,tenant.lastName,house.Estate,house.size,house.houseNumber,rent.amountPayed,rent.balance from tenant inner join house on house.tenantId=tenant.id inner join rent on house.id = rent.houseId where tenant.agentId=? and date>=?";

    public void fillTable() throws Exception {
        fetColumnList();
        fetRowlist();
    }

    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AgentRentReport.fxml"));
    }
    public void goBack() throws Exception {
        tblHouses.getScene().setRoot(new AgentDashboard().getContent());
    }

    public void fetColumnList(){
        try  {
            tblHouses.getColumns().clear();
            PreparedStatement statement = Main.conn.prepareStatement(tenants);
            statement.setString(1,Main.loggedInId);
            statement.setDate(2,firstDayThisMonth);
            ResultSet rs = statement.executeQuery();
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==2){
                    col= new TableColumn("ESTATE");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==0){
                    col= new TableColumn("TENANT NAME");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==3){
                    col= new TableColumn("SIZE");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }else if(i==5){
                    col= new TableColumn("AMOUNT PAYED");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }else if(i==6){
                    col= new TableColumn("BALANCE");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }else if(i==4){
                    col= new TableColumn("HOUSE NUMBER");
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
                tblHouses.getColumns().removeAll(col);
                tblHouses.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(tenants);
        state.setString(1,Main.loggedInId);
        state.setDate(2,firstDayThisMonth);
        System.out.println(Main.loggedInId);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            System.out.println(resultSet.getMetaData().getColumnCount());
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==1){
                    row.add(resultSet.getString(1)+" "+resultSet.getString(2));

                }else if(i==6 || i==7){
                    row.add("Ksh."+resultSet.getString(i));

                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblHouses.getItems().clear();
        tblHouses.setItems(data);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fillTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
