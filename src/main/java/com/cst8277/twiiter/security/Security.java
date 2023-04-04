package com.cst8277.twiiter.security;

import com.cst8277.twiiter.db.dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Security {
    String name;
    UUID uui;
    Connection con = dbconnect.getConnectionToDatabase();

    public boolean checkUUID(String name) throws SQLException {

        String prep = "SELECT date FROM Session WHERE Session.name=?";
        PreparedStatement preparedStatement = con.prepareStatement(prep);
        preparedStatement.setString(1,name);
        System.out.println(preparedStatement);
        ResultSet set = preparedStatement.executeQuery();
        while(set.next()){
            String ss = set.getString(1);
            System.out.println(ss);
        }


        return false;
    }

}
