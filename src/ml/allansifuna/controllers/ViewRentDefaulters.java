package ml.allansifuna.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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

public class ViewRentDefaulters implements Controller, Initializable {
    @FXML
    TableView tblRentDefaulters;

    private ObservableList<ObservableList> data;
    java.util.Date thisMonth = new java.util.Date();
    Date toTime= new Date(thisMonth.getYear(),thisMonth.getMonth(),1);
    String defaultedRents="select house.houseNumber, tenant.firstName,tenant.lastName ,r.balance,agent.firstName,agent.lastName from house " +
            "inner join tenant on house.tenantId= tenant.id inner join agent on tenant.agentId = agent.id inner join rent r on tenant.id = r.tenantId where r.balance>0 " +
            " and r.date> ?";
    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewRentDefaulters.fxml"));
    }
    public void goBack() throws Exception {
        tblRentDefaulters.getScene().setRoot(new Dashboard().getContent());
    }
    public void fetColumnList(){
        try  {
            PreparedStatement statement = Main.conn.prepareStatement(defaultedRents);
            statement.setDate(1,toTime);
            ResultSet rs = statement.executeQuery();
            System.out.println(rs.getMetaData().getColumnCount());
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==1){
                    col= new TableColumn("TENANT");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==2 || i==5) {
                    continue;
                }else if(i==4){
                    col= new TableColumn("AGENT");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==0){
                    col= new TableColumn("HOUSE N0.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else {
                    col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                }
                tblRentDefaulters.getColumns().removeAll(col);
                tblRentDefaulters.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(defaultedRents);
        state.setDate(1,toTime);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            System.out.println(resultSet.getMetaData().getColumnCount());
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==2){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(3));
                }else if(i==5){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(6));
                }else if("balance".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add("Ksh "+resultSet.getString(i));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblRentDefaulters.setItems(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        try {
            fetRowlist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
