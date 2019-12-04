package ml.allansifuna.validations;

import com.sun.deploy.util.StringUtils;
import ml.allansifuna.database.ConnectionUtill;
import ml.allansifuna.home.Main;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class Auth{
    public static void main(String[] args) throws SQLException {

        doInsert();
    }

    private static void doInsert() throws SQLException {
        String pas="Insert into employee(id,firstName,lastName,gender,email,password,nationalId,employeeType,salaryAmount) values(" +
                "?,?,?,?,?,?,?,?,?)";

        String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt());
        PreparedStatement st = ConnectionUtill.getConn().prepareStatement(pas);
        st.setString(1,UUID.randomUUID().toString());
        st.setString(2,"Jane");
        st.setString(3,"Doe");
        st.setString(4,"Female");
        st.setString(5,"janedoe@gmail.com");
        st.setString(6,hashed);
        st.setInt(7,13866750);
        st.setString(8,"Receptionist");
        st.setInt(9,1000);
        int aff=st.executeUpdate();
        System.out.println(aff);
    }


}