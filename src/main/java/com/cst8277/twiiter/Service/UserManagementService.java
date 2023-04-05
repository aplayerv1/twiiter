package com.cst8277.twiiter.Service;

import com.auth0.jwt.JWT;
import com.cst8277.twiiter.db.dbconnect;
import com.cst8277.twiiter.security.Security;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

@EnableWebSecurity
public class UserManagementService {

    Connection con = dbconnect.getConnectionToDatabase();
    Security s = new Security();

    public Boolean verifyToken(String name, String uuid) throws SQLException {
        String sql = "select * from Session where Session.name=? and uuid=?";
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
    public HashMap<String, Object> getSubs(String name, String uuid) throws SQLException{
        if(verifyToken(name,uuid)){
            HashMap<String, Object> hm = new HashMap<>();
            String sql = "SELECT Registry.id, role.id, Registry.`user` FROM Registry INNER JOIN role ON Registry.id = role.id";
            PreparedStatement prst = con.prepareStatement(sql);
            ResultSet set = prst.executeQuery();
            int count = 0;
            while(set.next()){
                HashMap<String,Object> um = new HashMap<>();
                for(int i=0;i<set.getMetaData().getColumnCount();i++){
                    um.put(set.getMetaData().getColumnName(i+1), set.getObject(i+1));

                }
                hm.put(Integer.toString(count),um);
                count++;
            }
        return hm;

        }
        return null;
    }

    public HashMap<String, Object> addUser(String name, String uuid,String username,String password) throws SQLException{
        if(verifyToken(name,uuid)){
            HashMap<String, Object> hm = new HashMap<>();
            String sql = "INSERT INTO Registry (`USER`,`password`,isSubscriber,isProducer) VALUES (?,?,0,0)";
            PreparedStatement prst = con.prepareStatement(sql);
            prst.setString(1, username);
            prst.setString(2, password);
            prst.executeUpdate();
        }
        return null;
    }

    public HashMap<String, Object> getMsg (String name, String uuid, String username) throws SQLException{
        if(verifyToken(name,uuid)){
            if(username==""){
                HashMap<String,Object> hm = new HashMap<>();
                String sql = "SELECT Messages FROM Messaging";
                PreparedStatement prst = con.prepareStatement(sql);
                ResultSet set = prst.executeQuery();
                int count = 0;
                while(set.next()){
                    HashMap<String,Object> um = new HashMap<>();
                    for(int i=0;i<set.getMetaData().getColumnCount();i++){
                        um.put(set.getMetaData().getColumnName(i+1), set.getObject(i+1));

                    }
                    hm.put(Integer.toString(count),um);
                    count++;
                }
                return hm;
            }else{
                HashMap<String, Object> hm = new HashMap<>();
                String sql = "SELECT Messages, Registry.`user` FROM Messaging INNER JOIN Registry ON Messaging.Registry_id = Registry.id WHERE Registry.`user`=?";
                PreparedStatement prst = con.prepareStatement(sql);
                prst.setString(1,username);
                ResultSet set =  prst.executeQuery();
                int count = 0;
                while(set.next()){
                    HashMap<String,Object> um = new HashMap<>();
                    for(int i=0;i<set.getMetaData().getColumnCount();i++){
                        um.put(set.getMetaData().getColumnName(i+1), set.getObject(i+1));

                    }
                    hm.put(Integer.toString(count),um);
                    count++;
                }
                return hm;

            }

        }

        return null;
    }

    public String generateSession(String name, String uuid) throws SQLException {
        String sqla = "select * from Session where Session.name=?";
        PreparedStatement prep = con.prepareStatement(sqla);
        prep.setString(1,name);
        ResultSet set = prep.executeQuery();

        if(set.next()){
            String sqle = "select uuid from Session where Session.name=?";
            PreparedStatement pp = con.prepareStatement(sqle);
            pp.setString(1,name);
            ResultSet sets = prep.executeQuery();
            while(sets.next()){
                if(s.isJWTExpired(JWT.decode((String) sets.getObject(2))) == true){
                    removeSession(name);
                }
            }
            return null;
        }else{
            System.out.println("meh");
            String sqll = "insert into Session(name,uuid) values (?,?)";
            PreparedStatement prepd = con.prepareStatement(sqll);
            prepd.setString(1, name);
            prepd.setString(2, uuid);
            prepd.executeUpdate();
            return uuid;
        }
    }

    public String getToken(String name) throws SQLException {
        String sqlea = "select uuid from Session where Session.name=?";
        PreparedStatement ppp = con.prepareStatement(sqlea);
        ppp.setString(1,name);
        ResultSet getset = ppp.executeQuery();
        while(getset.next()){
            return (String) getset.getObject(1);
        }
        return null;
    }

}
