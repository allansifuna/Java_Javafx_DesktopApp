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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ViewHouses implements Controller, Initializable {
    @FXML
    ComboBox<String> cbEstate,cbSize;

    @FXML
    TableView tblHouses;
    private ObservableList<ObservableList> data;
    String houses="select house.houseNumber,house.rentAmount,agent.firstName,agent.lastName,house.id from house inner join agent on house.agentId=agent.id where house.estate=? and house.size=? and house.tenantId is null";
    String tenant="select * from tenant where firstName=? or lastName=?";
    String getId="select id from tenant where firstname=? and lastName =?";
    public static String houseId;
    public void initSize(){
        cbSize.getItems().clear();
        if(cbEstate.getSelectionModel().getSelectedItem().equals("Low Income")){
            cbSize.getItems().addAll("Small","Medium");
        }else if(cbEstate.getSelectionModel().getSelectedItem().equals("Middle Income")){
            cbSize.getItems().addAll("Medium","Big");
        }else if(cbEstate.getSelectionModel().getSelectedItem().equals("High Income")){
            cbSize.getItems().addAll("Big");
        }
    }

    public void fillTable() throws Exception {
        tblHouses.setVisible(true);
        fetColumnList();
        fetRowlist();
    }

    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewHouses.fxml"));
    }
    public void goBack() throws Exception {
        tblHouses.getScene().setRoot(new Dashboard().getContent());
    }

    public void fetColumnList(){
        try  {
            tblHouses.getColumns().clear();
            PreparedStatement statement = Main.conn.prepareStatement(houses);
            statement.setString(1,cbEstate.getSelectionModel().getSelectedItem());
            statement.setString(2,cbSize.getSelectionModel().getSelectedItem());
            ResultSet rs = statement.executeQuery();
            System.out.println(rs.getMetaData().getColumnCount());
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                final int j =i;
                TableColumn col;
                if(i==1){
                    col= new TableColumn("RENT AMOUNT");
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
                }else if(i==4){
                    col= new TableColumn("ASSIGN.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, Button>, ObservableObjectValue<Button>>() {
                        @Override
                        public ObservableObjectValue<Button> call(TableColumn.CellDataFeatures<ObservableList, Button> param) {
                            return new SimpleObjectProperty<Button>((Button) param.getValue().get(j));
                        }
                    });

                }else if(i==3){
                    continue;
                }else if(i==2) {
                    col = new TableColumn("Agent");
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
                tblHouses.getColumns().removeAll(col);
                tblHouses.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist() throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(houses);
        state.setString(1,cbEstate.getSelectionModel().getSelectedItem());
        state.setString(2,cbSize.getSelectionModel().getSelectedItem());
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            System.out.println(resultSet.getMetaData().getColumnCount());
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==3){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(4));
                }else if("id".equals(resultSet.getMetaData().getColumnName(i))){
                    Button f = new Button("Assign House");
                    f.setId(resultSet.getString("id"));
                    f.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            houseId=f.getId();
                            /*System.out.println(houseId);*/
                            try {
                                cbEstate.getScene().setRoot(new AddTenant().getContent());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    row.add(f);
                }else if("rentAmount".equals(resultSet.getMetaData().getColumnName(i))) {
                    row.add("Ksh " + resultSet.getString(i));
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
        tblHouses.setVisible(false);
        cbEstate.getItems().addAll("Low Income","Middle Income","High Income");
    }
}
