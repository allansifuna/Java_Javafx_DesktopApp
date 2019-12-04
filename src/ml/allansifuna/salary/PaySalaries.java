package ml.allansifuna.salary;

import ml.allansifuna.home.Main;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PaySalaries {
    static String allEmployees="select id from employee";
    static String employeeSalary="select salaryAmount from employee where id=?";
    static String insertSalary="insert into salary (id,employeeId,salaryAmount,date) values(?,?,?,?)";
    static ArrayList<String> allEmployeesId = new ArrayList<>();
    public static void paySalaries() throws SQLException {
        PreparedStatement statement = Main.conn.prepareStatement(allEmployees);
        ResultSet st = statement.executeQuery();
        while (st.next()){
            allEmployeesId.add(st.getString(1));
        }
        payEachSalary(allEmployeesId);
    }

    private static void payEachSalary(ArrayList<String> allEmployeesId) throws SQLException {
        for (String id:allEmployeesId) {
            PreparedStatement st = Main.conn.prepareStatement(employeeSalary);
            st.setString(1,id);
            PreparedStatement stat=Main.conn.prepareStatement(insertSalary);
            stat.setString(1, UUID.randomUUID().toString());
            stat.setString(2,id);
            stat.setString(3,st.executeQuery().getString(1));
            stat.setDate(4,new Date(new java.util.Date().getTime()));
            stat.executeUpdate();
        }
    }
}
