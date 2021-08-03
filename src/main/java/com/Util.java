package com;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static String ftime() {
        SimpleDateFormat  ftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ftime.format(new Date());
    }

    public static String getEcho(String password){

        String echo = "";

        if (password != null && !password.equals("")){

            int length = password.length();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i=0;i<length;i++){
                stringBuffer.append("*");
            }

            echo = stringBuffer.toString();
        }

        return echo;
    }

    public static Connection getConn(String dbhost,int dbport,String dbuser ,String dbpass ){
        Connection conn = null;
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = null;
        // 注册 JDBC 驱动
        try {
            // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL

            DB_URL = String.format("jdbc:mysql://%s:%s/mysql",dbhost,dbport);
            Class.forName(JDBC_DRIVER);

        }catch (Exception e){
            // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
            JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
            DB_URL = String.format("jdbc:mysql://%s:%s/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",dbhost,dbport);
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }
        try{
            conn = DriverManager.getConnection(DB_URL,dbuser,dbpass);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public static void close(ResultSet rs, Statement stat, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
