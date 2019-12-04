package ml.allansifuna.penalties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Penalty {
    private static Date date = new Date();
    private static ArrayList<String> ids= new ArrayList<String>();
    private static java.sql.Date firstDay= new java.sql.Date(date.getYear(),date.getMonth(),1);
    private static String getAllBalances="select * from  rent where balance > ? and date >= ?";
    private static String getEachBalance="select balance from rent where id =?";
    private static String updatePenalty="Update rent set balance = ? where id=?";
    private static String getAllTenantIds="select * from house where tenantId is not null";
    private static String getLastPay="select * from rent where tenantId=? and date>=?";
    private static String putEmptyPay="insert into rent (id,date,paymentType,tenantId,houseId,amountDue,amountPayed,balance) values(?,?,?,?,?,?,?,?)";
    private static String getTenantHouseId="select id,rentAmount from house where tenantId=?";
    private static String getPendingBalance="select balance from rent where tenantId =? and date>=? and date<=?";
    private static ArrayList<String> allTenantIds = new ArrayList<>();
    private static int houseRent;

    public static void  penalize(Connection conn) throws Exception {
        PreparedStatement statement = conn.prepareStatement(getAllBalances);
        statement.setInt(1,0);
        System.out.println(firstDay.getTime());
        statement.setLong(2,firstDay.getTime());
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            ids.add(resultSet.getString("id"));
        }
        System.out.println("I was Here");

        penalizeEach(conn,ids);
    }


    private static void penalizeEach(Connection conn,ArrayList<String> ids)throws Exception {
        System.out.println(ids.size());
        for (String id:ids) {
            System.out.println(id);
            PreparedStatement stat = conn.prepareStatement(getEachBalance);
            stat.setString(1,id);
            ResultSet rSet= stat.executeQuery();
            int balance = rSet.getInt("balance");
            System.out.println(balance);
            onePenalty(conn,balance,id);
            System.out.println("am Out");

        }

    }
    private static void onePenalty(Connection conn,int balance,String id) throws Exception{
        int newBalance=(int) (balance * 1.005);
        PreparedStatement st = conn.prepareStatement(updatePenalty);
        st.setInt(1,newBalance);
        st.setString(2,id);
        st.execute();
        System.out.println(newBalance);
    }


    public static void updateUnpaidRent(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(getAllTenantIds);
        ResultSet rs = statement.executeQuery();
        while (rs.next()){
            allTenantIds.add(rs.getString("tenantId"));
        }
        checkEachLastPay(conn,allTenantIds);
    }

    private static void checkEachLastPay(Connection conn, ArrayList<String> allTenantIds) throws SQLException {
        for (String id:allTenantIds) {
            PreparedStatement st=conn.prepareStatement(getLastPay);
            st.setString(1,id);
            st.setDate(2,firstDay);
            ResultSet anyRows=st.executeQuery();
            boolean ifAnyPayment=false;
            while(anyRows.next()){
                ifAnyPayment=true;
            }
            if(!ifAnyPayment){
                fillEmptyRent(id,conn);
            }
        }
    }

    private static void fillEmptyRent(String id, Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(putEmptyPay);
        statement.setString(1, UUID.randomUUID().toString());
        statement.setDate(2,new java.sql.Date(date.getTime()));
        statement.setString(3,"Pay in Advance");
        statement.setString(4,id);
        statement.setString(5,getHouseId(conn,id));
        statement.setInt(6,getAmountDue(conn,id));
        statement.setInt(7,0);
        statement.setInt(8,getAmountDue(conn, id));
        int affectedRows= statement.executeUpdate();
        System.out.println("Auto Fill rent "+String.valueOf(affectedRows));

    }

    private static int getAmountDue(Connection conn, String id) throws SQLException {
        java.sql.Date lastMonth = new java.sql.Date(date.getYear(),date.getMonth()-1,1);
        PreparedStatement st = conn.prepareStatement(getPendingBalance);
        st.setString(1,id);
        st.setDate(2,lastMonth);
        st.setDate(3,firstDay);
        ResultSet set = st.executeQuery();
        int balance=0;
        while (set.next()){
            balance=set.getInt("balance");
        }
        return balance+houseRent;
    }

    private static String getHouseId(Connection conn, String id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(getTenantHouseId);
        statement.setString(1,id);
        ResultSet rs = statement.executeQuery();
        houseRent=Integer.parseInt(rs.getString("rentAmount"));
        return rs.getString("id");
    }
}
