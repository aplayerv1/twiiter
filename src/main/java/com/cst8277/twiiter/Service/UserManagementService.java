package com.cst8277.twiiter.Service;

import com.cst8277.twiiter.db.dbconnect;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserManagementService {
//    @GetMapping(path="/")
//    public Greeting getMainPage(@RequestParam(value = "name",defaultValue = "World") String name) {
//    return new Greeting(counter.incrementAndGet(), String.format(template, name));
//
//}
    Connection con = dbconnect.getConnectionToDatabase();
    String _x = "apjdc";
    public Boolean checkauth(String name,String uuid){

        return UUID.fromString(name+uuid).toString()==uuid;
    }
    public UUID validate(String username, String password) throws SQLException {

        String sql = "select * from Registry where user=? and password=?";
        PreparedStatement prst = con.prepareStatement(sql);

        prst.setString(1,username);
        prst.setString(2,password);
        ResultSet set = prst.executeQuery();

        while (set.next()){
            return UUID.fromString(set.getString("user")+_x);
        }
        return null;
    }



}
