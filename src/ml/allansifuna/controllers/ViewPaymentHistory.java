package ml.allansifuna.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import ml.allansifuna.home.Controller;
import ml.allansifuna.home.Main;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;



public class ViewPaymentHistory implements Controller, Initializable {
    @FXML
    TableView tblPaymentHistory;

    @FXML
    TextField txtName;

    @FXML
    Label lblName;

    @FXML
    Button btnLookup;
    @FXML
    ComboBox<String> cbTenant;
    private ObservableList<ObservableList> data;
    java.util.Date thisMonth = new java.util.Date();
    Date toTime= new Date(thisMonth.getYear(),thisMonth.getMonth(),1);
    String rentHistory="select house.houseNumber, tenant.firstName,tenant.lastName ,r.amountPayed,r.balance,r.date from house " +
            "inner join tenant on house.tenantId= tenant.id inner join rent r on tenant.id = r.tenantId where tenant.id=?";
    String tenant="select * from tenant where firstName=? or lastName=?";
    String getId="select id from tenant where firstname=? and lastName =?";
    public void activateLookup(){
        btnLookup.setVisible(true);
        txtName.setLayoutY(38.0);
        lblName.setLayoutY(71.0);
    }
    public void updateTenant(){
        try {
            cbTenant.getItems().clear();
            PreparedStatement statement = Main.conn.prepareStatement(tenant);
            statement.setString(1,txtName.getText());
            statement.setString(2,txtName.getText());
            ResultSet resultSet = statement.executeQuery();
            boolean has=false;
            while(resultSet.next()){
                has=true;
                cbTenant.getItems().add(resultSet.getString("firstName")+" "+resultSet.getString("lastName"));
            }
            if(!has){
                tblPaymentHistory.setVisible(false);
               lblName.setTextFill(Color.ORANGERED);
                lblName.setText("Tenant "+txtName.getText()+" Does not exist");
            }else{
                lblName.setText("");
            }
            btnLookup.setVisible(false);
            txtName.setLayoutY(79);
            lblName.setLayoutY(114.0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void fillTable(){
        try {
            if(cbTenant.getSelectionModel().getSelectedItem()!=null) {
                PreparedStatement statement = Main.conn.prepareStatement(getId);
                String[] name = cbTenant.getSelectionModel().getSelectedItem().split(" ");
                statement.setString(1, name[0]);
                statement.setString(2, name[1]);
                String id = statement.executeQuery().getString("id");
                fetColumnList(id);
                try {
                    fetRowlist(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tblPaymentHistory.setVisible(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/ViewPaymentHistory.fxml"));
    }
    public void goBack() throws Exception {
        tblPaymentHistory.getScene().setRoot(new Dashboard().getContent());
    }

    public void fetColumnList(String id){
        try  {
            tblPaymentHistory.getColumns().clear();
            PreparedStatement statement = Main.conn.prepareStatement(rentHistory);
            statement.setString(1,id);
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
                }else if(i==0){
                    col= new TableColumn("HOUSE N0.");
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
                }/*else if(i==3){
                    col= new TableColumn("AMOUNT PAYED.");
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, Button>, ObservableObjectValue<Button>>() {
                        @Override
                        public ObservableObjectValue<Button> call(TableColumn.CellDataFeatures<ObservableList, Button> param) {
                            return new SimpleObjectProperty<Button>((Button) param.getValue().get(j));
                        }
                    });

                }*/else if(i==2){
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
                tblPaymentHistory.getColumns().removeAll(col);
                tblPaymentHistory.getColumns().addAll(col);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  void fetRowlist(String id) throws Exception{
        data= FXCollections.observableArrayList();
        PreparedStatement state = Main.conn.prepareStatement(rentHistory);
        state.setString(1,id);
        ResultSet resultSet = state.executeQuery();
        while(resultSet.next()){
            ObservableList row = FXCollections.observableArrayList();
            System.out.println(resultSet.getMetaData().getColumnCount());
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                if(i==2){
                    row.add(resultSet.getString(i)+" "+resultSet.getString(3));
                }else if("amountPayed".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add("Ksh "+resultSet.getString(i));
                    /*Button f = new Button("Add House");
                    f.setId(resultSet.getString("amountPayed"));
                    f.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                                printFuck(f.getId());
                                f.setVisible(false);
                        }
                    });
                    row.add(f);*/
                }else if("balance".equals(resultSet.getMetaData().getColumnName(i))){
                    row.add("Ksh "+resultSet.getString(i));
                }else if("date".equals(resultSet.getMetaData().getColumnName(i))){

                    row.add(getMonth(resultSet.getDate(i).getMonth())+" "+(1900+(int)resultSet.getDate(i).getYear()));
                }else {
                    row.add(resultSet.getString(i));
                }
            }
            data.add(row);
        }
        tblPaymentHistory.getItems().clear();
        tblPaymentHistory.setItems(data);
    }

    private void  printFuck(String balance) {
        lblName.setText(balance);
        lblName.setTextFill(Color.DARKGREEN);
    }


    public String getMonth(int date){
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
        tblPaymentHistory.setVisible(false);

    }
}
