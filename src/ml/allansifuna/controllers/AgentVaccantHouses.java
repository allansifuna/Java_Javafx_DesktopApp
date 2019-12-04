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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AgentVaccantHouses implements Controller, Initializable {
    @FXML
    TableView tblVaccantHouses;

    private ObservableList<ObservableList> data;
    String vaccavtHouses="select house.houseNumber,house.size,house.estate,agent.firstName ,agent.lastName,landlord.firstName,landlord.lastName ,house.rentAmount from house inner join agent on house.agentId=agent.id inner join landlord on house.landlordId = landlord.id where house.status=? and house.tenantId is null and house.agentId=?";
    public  Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/AgentVaccantHouses.fxml"));
    }

    public void fetColumnList(){
        try  {
            PreparedStatement statement = Main.conn.prepareStatement(vaccavtHouses);
            statement.setString(1,"False");
            statement.setString(2,Main.loggedInId);
            ResultSet rs = statement.executeQuery();
            /*System.out.println(rs.getMetaData().getColumnCount());*/
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==3){
                    col= new TableColumn("AGENT");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                }else if(i==4 || i==6) {
                    continue;
                }else if(i==5){
                    col= new TableColumn("LANDLORD");
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
                tblVaccantHouses.getColumns().removeAll(col);
                tblVaccantHouses.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(vaccavtHouses);
        state.setString(1,"False");
        state.setString(2,Main.loggedInId);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            /*System.out.println(resultSet.getMetaData().getColumnCount());*/
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==4){
                    row.add(resultSet.getString(i)+" "+resultSet.getString("LastName"));
                }else if(i==6){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(7));
                }else if("rentAmount".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add("Ksh "+resultSet.getString(i));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblVaccantHouses.setItems(data);
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
    public void goBack() throws Exception {
        tblVaccantHouses.getScene().setRoot(new AgentDashboard().getContent());
    }
}
