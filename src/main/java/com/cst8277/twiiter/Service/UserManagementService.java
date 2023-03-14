package com.cst8277.twiiter.Service;

import com.cst8277.twiiter.db.dbconnect;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserManagementService {

    Connection con = dbconnect.getConnectionToDatabase();
    String _x = "apjdc";
    public Boolean checkauth(String name,String uuid) throws SQLException {
        String sql = "select * from Session where name=? and uuid=?";
        PreparedStatement prst = con.prepareStatement(sql);

        prst.setString(1,name);
        prst.setString(2,uuid);
        ResultSet set = prst.executeQuery();

        while (set.next()){
            return true;
        }

        return false;
    }
    public UUID validate(String username, String password) throws SQLException {

        String sql = "select * from Registry where user=? and password=?";
        PreparedStatement prst = con.prepareStatement(sql);

        prst.setString(1,username);
        prst.setString(2,password);
        System.out.println(prst);
        ResultSet set = prst.executeQuery();

        while (set.next()){
            UUID uid = UUID.randomUUID();
            String sqla = "insert into Session(name,uuid) values (?,?)";
            PreparedStatement prep = con.prepareStatement(sqla);
            prep.setString(1,username);
            prep.setString(2,uid.toString());
            prep.executeUpdate();

            return uid;
        }

        return null;
    }

    public void removesession(String name) throws SQLException{
        String sqla = "delete from Session where name=?";
        PreparedStatement prep = con.prepareStatement(sqla);
        prep.setString(1,name);
        prep.executeUpdate();
    }

}
