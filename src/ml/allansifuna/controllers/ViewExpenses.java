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

public class ViewExpenses implements Controller, Initializable {
    @FXML
    TableView tblExpenses;

    private ObservableList<ObservableList> data;
    java.util.Date thisMonth = new java.util.Date();
    Date toTime= new Date(thisMonth.getYear(),thisMonth.getMonth(),1);
    String expenses="select expenseType,expenseAmount,date from expense where date>? ";
    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewExpenses.fxml"));
    }
    public void goBack() throws Exception {
        tblExpenses.getScene().setRoot(new Dashboard().getContent());
    }
    public void fetColumnList(){
        try  {
            PreparedStatement statement = Main.conn.prepareStatement(expenses);
            statement.setDate(1,toTime);
            ResultSet rs = statement.executeQuery();
            System.out.println(rs.getMetaData().getColumnCount());
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==0){
                    col= new TableColumn("EXPENSE.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==1){
                    col= new TableColumn("AMOUNT.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else{
                    col= new TableColumn("EXPENSE MONTH.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }
                tblExpenses.getColumns().removeAll(col);
                tblExpenses.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(expenses);
        state.setDate(1,toTime);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            System.out.println(resultSet.getMetaData().getColumnCount());
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if("expenseAmount".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add("Ksh "+resultSet.getString(i));
                }else if("date".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add(getMonth(resultSet.getDate(i).getMonth())+" "+(1900+(int)resultSet.getDate(i).getYear()));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblExpenses.setItems(data);
    }
    public static String getMonth(int date){
        switch (date){
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Invalid Date";
        }
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
