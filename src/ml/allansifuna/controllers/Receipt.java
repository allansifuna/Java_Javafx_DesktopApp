package ml.allansifuna.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;

import javax.print.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Receipt implements Initializable {
    @FXML
    TextArea txaReceipt;
    public Parent getContent() throws Exception{
        return FXMLLoader.load(AddAgent.class.getResource("/ml/allansifuna/fxmls/Receipt.fxml"));
    }
    public void goBack() throws Exception {
        txaReceipt.getScene().setRoot(new AgentDashboard().getContent());
    }
    public void printReceipt() throws PrintException {
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if(printService !=null){
            Doc doc = new SimpleDoc(txaReceipt.getText(),(DocFlavor) DocFlavor.STRING.TEXT_PLAIN,null);
            DocPrintJob printJob = printService.createPrintJob();
            printJob.print(doc,null);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txaReceipt.setText(AddRent.receipt);
    }
}
