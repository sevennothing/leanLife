package com.seven.leanLife.engine;

import com.seven.leanLife.config.LangConfigBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 数据模型定义
 */
public interface DataModel {
    /**
     *	检查用户输入的邮箱是否合法
     *
     *	@param mail 邮箱
     *	@return boolean
     */
    public static boolean isValidMail(String mail) {
        String patternMail = "^\\w[\\w_-]+@[\\w_-]+(\\.[\\w]+)+$";
        boolean  matchMail = Pattern.matches(patternMail, mail);
        if(matchMail) {
            return true;
        }
        return false;
    }
    /**
     *	检查用户输入的密码是否合法
     *
     *	@param password 密码
     *	@return boolean
     */
    public static boolean isValidPassword(String password) {
        /* 密码包含字母、数字和特殊字符 */
        String patternPasswd = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{8,30}$";
        boolean matchPasswd = Pattern.matches(patternPasswd, password);
        if(matchPasswd) {
            return true;
        }
        return false;
    }

    /**
     *  对明文密码进行加密
     * @param password
     * @return
     */
    public static String securctPassword(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] bytes = md.digest(password.getBytes());
            String str = Base64.getEncoder().encodeToString(bytes);
            return str;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


   /**
     *  检查用户输入用户名的合法性
     *
     *  @param userName 用户名
     *  @return boolean
     */
    public  static boolean isValidUserName(String userName) {
        String patternName = "^[a-zA-Z]\\w{5,19}$";
        boolean matchName = Pattern.matches(patternName, userName);
        if(matchName) {
            return true;
        }
        return false;
    }

    /**
     *	获取天气情况数组
     */
    public static String[] getWeaStrs() {
        String[] weaStrs = new String[] {"天晴","下雨","多云","阴天"} ;
        return weaStrs;
    }

    /**
     *	获取被选中的天气的字符串值
     */
    public static String getWeaStrSelected(int index) {
        String[] weaStrs = new String[] {"天晴","下雨","多云","阴天"} ;
        return weaStrs[index];
    }

    /**
     *	获取密保问题
     */
    public static String[] getSecQues() {
        String[] ques = new String[]{"你的幸运数字是多少？","你最喜欢的明星是谁？","最喜欢的一本书的书名？"};
        return ques;
    }


}
