package com.seven.leanLife.engine;

import com.seven.leanLife.model.User;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 统一的数据处理类
 * @Author caijun.Li
 *
 */
public class MyDataBase implements DataModel {
    private boolean isAuthorized;
    private String name;
    private Connection conn = null;
    private String connectionUrl;
    private String dbUser;
    private String dbPassword;

    public MyDataBase(String driverClass, String connectionUrl, String user, String password){
        this.connectionUrl = connectionUrl;
        this.dbUser = user;
        this.dbPassword = password;
        // 连接数据库
        conn = getConnection();
    }

   /**
     *	得到数据连接
     */
   private Connection getConnection() {
       try {
           //创建连接
           conn = DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
           System.out.println("数据库连接成功");
           checkDatabase();
       }catch(SQLException e) {
           e.printStackTrace();
       }
       return conn;
   }
    /**
     *	释放连接
     */
    public void release(Statement pstmt, ResultSet res) {
        if(conn != null) {
            try {
                conn.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }

        if(res != null) {
            try {
                res.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean authorized(){
        return this.isAuthorized;
    }

    public String getName(){
        return name;
    }

    /**
     *  添加一个使用用户
     * @param user
     * @return
     */
    public boolean addNewUser(User user){
        boolean ret;
        System.out.println("Add New User");
        System.out.println(user.toString());
        String sql = "insert into user(name, password, secretQuestionId, secretAnswer, registerDate) values(?,?,?,?,?)";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        java.util.Date date = new Date();

        // 对用户密码进行加密
        user.setPassword(DataModel.securctPassword(user.getPassword()));
        user.setRegisterdate(df.format(date));
        ret = executeInsertDeleteUpdate(sql, user.getName(), user.getPassword(), user.getSecretquestionId(),
                user.getSecretanswer(), user.getRegisterdate());
        return ret;
    }

    private User getUserFromUserTab(String sql){
        User user = null;
        ResultSet res = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            res = pstmt.executeQuery();
            if(res.next()) {
                user = new User();
                user.setName(res.getString(1));
                user.setPassword(res.getString(2));
                user.setSecretquestionId(res.getInt(3));
                user.setSecretanswer(res.getString(4));
                user.setRegisterdate(res.getString(5));
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }finally {
        }
        return user;
    }

    public User getUserByName(String name){
        String sql = "SELECT * from user WHERE name='" + name + "'";
        return getUserFromUserTab(sql);
    }
    public User getUserWithAuthorize(String name, String password) throws DataException{
        /* 检测数据有效性 */
        if(!DataModel.isValidUserName(name)){
            throw new DataException("Invalid Name format");
        }

        if(!DataModel.isValidPassword(password)){
            throw new DataException("Invalid password format");
        }

        String securetPassword = DataModel.securctPassword(password);
        String sql = "SELECT * from user WHERE name='" + name + "' and password='" + securetPassword + "'";
        return getUserFromUserTab(sql);
    }

    /**
     * 检查数据库
     * @return
     */
    private boolean checkDatabase(){
        String sql ="SELECT COUNT(*) FROM user" ;
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
        }catch(Exception e){
            System.out.println(e.toString());
            /* 初始化数据库 */
            initDatabase();
        }finally{

        }
        return true;
    }

    private void initDatabase(){
        System.out.println("初始化数据库");
        String sql = "CREATE TABLE `user` (" +
                "`name` varchar(100) NOT NULL UNIQUE," +
                "`password` varchar(20) NOT NULL ," +
                "`secretQuestionId` int NOT NULL," +
                "`secretAnswer` varchar(100) NOT NULL ," +
                "`registerDate` date NOT NULL , " +
                "PRIMARY KEY (`name`) )";
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }


    /**
     *	更新数据 包括插入 删除 和更新
     */
    private boolean executeInsertDeleteUpdate(String sql, Object...args) {
        PreparedStatement pstmt = null;
        boolean isOk = false;
        try {
            //得到连接
            pstmt = conn.prepareStatement(sql);
            if(args != null) {
                //设置sql语句的占位符
                for(int i=0; i<args.length; i++) {
                    pstmt.setObject(i+1, args[i]);
                }
            }
            //影响的函数
            int n = pstmt.executeUpdate();
            if(n>0) {
                isOk = true;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }finally {
        }
        return isOk;
    }

}
