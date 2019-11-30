package com.seven.leanLife.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;
import com.seven.leanLife.model.Dairy;
import com.seven.leanLife.model.User;

/**   
* @title: JDBCTool.java 
* @package application.tools 
* @description: (数据库连接工具类) 
* @author 夏靖雯  
* @date 2018年12月19日 下午7:59:03 
* @version V1.0   
*/

public class JDBCTool {
        public static void initDatabase(Connection conn){
            System.out.println("初始化数据库");
            String sql ="CREATE TABLE user(name varchar(20),"
                    + "password varchar(20),secretQuestion varchar(20),"
                    + "secretAnswer varchar(20),regDate date)";
            try{
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                
            }
        }
        /**
         * 检查数据库
         * @return 
         */
        private static boolean checkDatabase(Connection conn){
            String sql ="SELECT COUNT(*) FROM user" ;
            try{
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
            }catch(Exception e){
                e.printStackTrace();
                /* 初始化数据库 */
                initDatabase(conn);
            }finally{
                
            }
            return true;
        }
        
	/**
	 *	得到数据连接
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Properties properties = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream("conf/db.properties");
				properties.load(fis);
                                String url = null;
                                String dbType = properties.getProperty("databaseType");
                                if(dbType.equals("mysql")){
                                    //加载驱动
                                    Class.forName("com.mysql.jdbc.Driver");
                                    url = "jdbc:mysql://"+properties.getProperty("host")+
                                                    ":"+properties.getProperty("port")+
                                                    "/"+properties.getProperty("database")+
                                                    "?/useUnicode=true&characterEncoding=utf-8";
                                    //创建连接
                                    conn = DriverManager.getConnection(url, properties);
                                }else if(dbType.equals("sqlite")){
                                    //加载驱动
                                    Class.forName("org.sqlite.JDBC");
                                    url = "jdbc:sqlite:"+properties.getProperty("dataPath")+
                                            "/"+properties.getProperty("database");
                                    //创建连接
                                    conn = DriverManager.getConnection(url, properties);
                                }else{
                                    url = null;
                                    System.out.println("不支持的数据库" + properties.getProperty("databaseType"));
                                    return null;
                                }
                                checkDatabase(conn);
				
//				System.out.println("数据库连接成功");
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				if(fis != null) {
					try {
						fis.close();
					}catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 *	释放连接
	 */
	public static void release(Connection conn, Statement pstmt, ResultSet res) {
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
	
	/**
	 *	更新数据 包括插入 删除 和更新
	 */
	public static boolean executeInsertDeleteUpdate(String sql, Object...args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isOk = false;
		
		try {
			//得到连接
			conn = JDBCTool.getConnection();
			
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
			//释放连接
			JDBCTool.release(conn, pstmt, null);
		}
		return isOk;
	}
	
	/**
	 *	得到指定要求的日记列表结果
	 */
	public static ArrayList<Dairy> getDairyList(String sql, Object...args){
		ArrayList<Dairy> dList = new ArrayList<>();
		
		ResultSet res = null;
		PreparedStatement pstmt = null;
		Connection conn = JDBCTool.getConnection();
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			if(args != null) {
				for(int i=0; i<args.length; i++) {
					pstmt.setObject(i+1, args[i]);
				}
			}
			res = pstmt.executeQuery();
			
			while(res.next()) {
				String title = res.getString(1);
				String feeling = res.getString(2);
				int weaIndex = res.getInt(3);
				//将包含日期和时间的Date转换为值包含日期的LocalDate
				LocalDate date = res.getDate(4).toLocalDate();
				String content = res.getString(5);
				Dairy dairy = new Dairy(title, feeling, weaIndex, date, content);
				dList.add(dairy);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCTool.release(conn, pstmt, res);
		}
		
		return dList;
	}
	
	
	/**
	 *	检查用户是否存在
	 */
	public static User getUser(String sql, Object...args) {
		User user = null;
		ResultSet res = null;
		PreparedStatement pstmt = null;
		Connection conn = JDBCTool.getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			
			if(args != null) {
				//设置sql语句的占位符
				for(int i=0; i<args.length; i++) {
					pstmt.setObject(i+1, args[i]);
				}
			}
			res = pstmt.executeQuery();
			
			if(res.next()) {
				String userName = res.getString(1);
				String password = res.getString(2);
				int secQuesIndex = res.getInt(3);
				String secAnswer = res.getString(4);
				user = new User(userName,password,secQuesIndex,secAnswer);
			}
		
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCTool.release(conn, pstmt, res);
		}
		return user;
	}
	
}
