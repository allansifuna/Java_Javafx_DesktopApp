package ml.allansifuna.home;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import ml.allansifuna.database.ConnectionUtill;
import ml.allansifuna.penalties.Penalty;
import ml.allansifuna.salary.PaySalaries;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;


public class Main extends Application {
    public static String name= "/ml/allansifuna/fxmls/Login.fxml";
    public static String loggedInId;
    public static Connection conn = null;
    {
        /*String receptText="Allan Sifuna Namasaka";
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if(printService !=null){
            Doc doc = new SimpleDoc(receptText,(DocFlavor) DocFlavor.STRING.TEXT_PLAIN,null);
            DocPrintJob printJob = printService.createPrintJob();
            try {
                printJob.print(doc,null);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updatePenalties();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void updatePenalties() throws Exception {


        Date today= new Date();
        try {
            conn= ConnectionUtill.getConn();

        }catch (Exception e){
            e.printStackTrace();
        }
        String justSelect="select * from penalty";
        String insertPenalty="insert into penalty (id,date) values(?,?)";
        PreparedStatement st=null;
        ResultSet set=null;
        try {
            st=conn.prepareStatement(justSelect);
            set=st.executeQuery();
            if(!set.next()){
                PreparedStatement statement=conn.prepareStatement(insertPenalty);
                statement.setString(1,UUID.randomUUID().toString());
                statement.setDate(2,new java.sql.Date(today.getTime()));
                statement.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(today.getDate()>=10) {
            String getToday = "select * from penalty";
            String updateTime = "update penalty set date = ? where id = ?";
//            System.out.println(today.getDate());
            try {
                PreparedStatement statement = conn.prepareStatement(getToday);
                ResultSet rs = statement.executeQuery();
                if (rs.getDate("date").getDate() == today.getDate() - 1) {
//                    System.out.println("I was here");
                    Penalty.penalize(conn);
                    PreparedStatement stat = conn.prepareStatement(updateTime);
                    stat.setDate(1, new java.sql.Date(today.getTime()));
                    stat.setString(2, rs.getString("id"));
                    int affectedRows = stat.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
            }
        } else  if(today.getDate()==9) {
            Penalty.updateUnpaidRent(conn);
        }else  if(today.getDate()==28) {
            PaySalaries.paySalaries();
        }else  if(checkSalary(today.getMonth())) {
            PaySalaries.paySalaries();
        }
    }

    private boolean checkSalary(int month) throws SQLException {
        String lastPay="select max(date) from salary";
        PreparedStatement statement = conn.prepareStatement(lastPay);
        ResultSet set = statement.executeQuery();
        return set.getDate(1).getMonth() ==month-1;
    }

    public Main() throws IOException, Exception {
    }

    public static void main(String[] args) throws IOException {
	  launch(args);

    }


    @Override
    public void start(Stage primaryStage){
        Scene scene = null;
        try {
            scene= new Scene(FXMLLoader.load(getClass().getResource(name)), 975, 575);
        }catch (Exception e){
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.getIcons().add(new Image("/ml/allansifuna/images/icon8.png"));
        primaryStage.setTitle("DoubleA Management Information System");
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

}
