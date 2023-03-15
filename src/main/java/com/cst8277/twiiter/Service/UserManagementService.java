package com.cst8277.twiiter.Service;

import com.cst8277.twiiter.db.dbconnect;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManagementService {

    Connection con = dbconnect.getConnectionToDatabase();

    public Boolean verifyToken(String name, String uuid) throws SQLException {
        String sql = "select * from Session where name=? and uuid=?";
        PreparedStatement prst = con.prepareStatement(sql);

        prst.setString(1, name);
        prst.setString(2, uuid);
        ResultSet set = prst.executeQuery();

        while (set.next()) {
            return true;
        }

        return false;
    }

    public String login(String username, String password) throws SQLException {

        String sql = "select * from Registry where user=? and password=?";
        PreparedStatement prst = con.prepareStatement(sql);

        prst.setString(1, username);
        prst.setString(2, password);

        ResultSet set = prst.executeQuery();

        while (set.next()) {
            removeSession(username);
            String uid = generateToken().toString();
            String sqla = "insert into Session(name,uuid) values (?,?)";
            PreparedStatement prep = con.prepareStatement(sqla);
            prep.setString(1, username);
            prep.setString(2, uid);
            prep.executeUpdate();

            return uid;
        }

        return null;
    }

    public void removeSession(String name) throws SQLException {
        String sqlr = "delete from Session where name=?";
        PreparedStatement prep = con.prepareStatement(sqlr);
        prep.setString(1, name);
        prep.executeUpdate();
    }

    public UUID generateToken() {
        return UUID.randomUUID();
    }

    public HashMap<String, Object> getUsers(String name, String uuid) throws SQLException {

        if (verifyToken(name, uuid)) {
            HashMap<String, Object> hm = new HashMap<>();
            String sql = "select * from Registry";
            PreparedStatement prst = con.prepareStatement(sql);
            int count =0;
            ResultSet set = prst.executeQuery();

            while (set.next()) {
                HashMap<String, Object> um = new HashMap<>();
                for(int i =0;i<set.getMetaData().getColumnCount();i++){
                    um.put(set.getMetaData().getColumnLabel(i+1).toLowerCase(),set.getObject(i+1));
                }
                hm.put(Integer.toString(count),um);
                count++;
            }
            return hm;

        }
        return null;
    }

}
