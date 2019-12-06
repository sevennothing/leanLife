package com.seven.leanLife.controller;

import java.util.Map;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.model.User;
import com.seven.leanLife.utils.CheckValidTool;
import com.seven.leanLife.utils.JDBCTool;
import com.seven.leanLife.utils.SingleValueTool;
import com.seven.leanLife.utils.VerificationCodeTool;
import com.seven.leanLife.utils.DialogTool;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Controller;

/**
 * @title: RegistViewController.java
 * @package application.regist
 * @description: (注册视图控制器)
 * @author caijun.Li
 * @date 2019/10/06
 * @version V1.0
 */

@Controller
public class RegistViewController {
	private ApplicationController parentController;

	//返回到登录界面
	@FXML
	private ImageView backToLoginView;

	//用户名错误提示信息
	@FXML
	private Label userNameErrorInfo;

	//密码错误提示信息
	@FXML
	private Label passwordErrorInfo;

	//密码答案为空
	@FXML
	private Label secretAnswerErrorInfo;

	//验证码答案错误提示信息
	@FXML
	private Label verificationAnswerErrorInfo;

	//用户名输入框
	@FXML
	private TextField userNameField;

	//密码输入框
	@FXML
	private TextField passwordField;

	//密保问题选择
	@FXML
	private ChoiceBox secretQuestion;

	//密保问题答案
	@FXML
	private  TextField secretAnswerField;

	//验证码
	@FXML
	private Label verificationCode;

	//验证码答案
	@FXML
	private TextField verificationCodeAnswerField;

	//注册按钮
	@FXML
	private Button registButton;

	//验证码答案
	private int realVerCodeAnswer;

	@FXML private Label usernameLabel;
	@FXML private Label passwordLabel;
	@FXML private Label secretAnswerLabel;
	@FXML private Label verificationCodeLabel;
	@FXML private Label secretQuestionLabel;
	@FXML private Label verificationCodeAnswerLabel;

	/**
	 * 刷新语言支持
	 */
	public void langFlush(){
		String value;
		value = parentController.languageConf.getFeild("username");
		usernameLabel.setText(value);
		value = parentController.languageConf.getFeild("password");
		passwordLabel.setText(value);

		value = parentController.languageConf.getFeild("secret.answer");
		secretAnswerLabel.setText(value);

		value = parentController.languageConf.getFeild("verificationCode");
		verificationCodeLabel.setText(value);

		value = parentController.languageConf.getFeild("secret.question");
		secretQuestionLabel.setText(value);

		value = parentController.languageConf.getFeild("verificationCode.answer");
		verificationCodeAnswerLabel.setText(value);

		value = parentController.languageConf.getFeild("reg.username.prompt");
		userNameField.setPromptText(value);

		value = parentController.languageConf.getFeild("reg.password.prompt");
		passwordField.setPromptText(value);

		value = parentController.languageConf.getFeild("reg.secret.answer.prompt");
		secretAnswerField.setPromptText(value);
	}

	/**
	 *	处理注册按钮提交事件
	 */
	@FXML
	private void HandleRegistButtonAction() {
		//System.out.println("HandleRegistButtonAction");

		//使前一次的错误提示清空
		initErroLabel();

		String userName = userNameField.getText();
		String password = passwordField.getText();

		//密保问题下标和密保答案
		int index = secretQuestion.getSelectionModel().getSelectedIndex();
		String secretAnswer = secretAnswerField.getText();

		//用户输入的验证码是否正确
		boolean isCorrectVerCodeAn = false;
		
                /* // 取消验证码
		//检测用户输入的验证码答案
		try {
			int userVerCodeAnswer = Integer.parseInt(verificationCodeAnswerField.getText());
			if(this.realVerCodeAnswer == userVerCodeAnswer) {
				isCorrectVerCodeAn = true;
			}else {
				this.verificationAnswerErrorInfo.setText("验证码错误");
			}
		}catch(Exception e) {
			this.verificationAnswerErrorInfo.setText("验证码错误");
		}
                */
		isCorrectVerCodeAn = true;

		//若验证码正确 继续进行用户注册
		if(isCorrectVerCodeAn) {
			if(userName!=null && CheckValidTool.isValidUserName(userName)) {
				if(password!=null && CheckValidTool.isValidPassword(password)) {
					if(secretAnswer!=null) {
						if(addUser(userName, password, index, secretAnswer)) {
							//弹出注册成功的对话框 并让用户选择是否直接进入系统主页
							boolean isGoToSystemView = DialogTool.confirmDialog("注册成功", "按确认登录主界面，取消返回登录界面");
							if(isGoToSystemView) {
								//设置当用户
								User user = new User(userName, password, index, secretAnswer);
								parentController.setUser(user);

								parentController.showSystemView();
							}else {
								parentController.showLoginView();
							}
						}else {
							DialogTool.errorDialog("注册失败", "请重新注册");
						}
					}else {
						secretAnswerErrorInfo.setText("密保答案为空");
					}
				}else {
					passwordErrorInfo.setText("密码不合法");
				}
			}else {
				userNameErrorInfo.setText("用户名不合法");
			}
		}
		//若是注册不成功，则每次都要更新验证码
		setVerCode();
	}

	@FXML
	private void HandleBackToLoginAction() {
		parentController.showLoginView();
	}

	/**
	 *	加载完fxml文件后 自动调用该函数
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		//设置返回图标
		backToLoginView.setImage(new Image("file:images/back.png"));
		//设置密保问题
//		secretQuestion.getItems().add("你最喜欢的人是谁？");
//		secretQuestion.setItems(FXCollections.observableArrayList(
//				"你最喜欢的动漫角色是谁？",
//				"你最喜欢的女明星是谁？",
//				"对你启发最大的一本书是什么？"
//				));
		secretQuestion.getItems().addAll(SingleValueTool.getSecQues());
		secretQuestion.getSelectionModel().selectFirst();

		//设置验证码	
		//setVerCode();
	}

	/**
	 *	清空错误提示信息
	 */
	private void initErroLabel() {
		this.userNameErrorInfo.setText("");
		this.passwordErrorInfo.setText("");
		this.verificationAnswerErrorInfo.setText("");
		this.secretAnswerErrorInfo.setText("");
		this.verificationAnswerErrorInfo.setText("");
	}

	/**
	 *	设置算术验证码
	 */
	private void setVerCode() {
		Map<String, Integer> verCodeAndAnswer = VerificationCodeTool.generateArithmeticVerification();
		verCodeAndAnswer.forEach((verCode,answer)->{
			verificationCode.setText(verCode);
			this.realVerCodeAnswer = answer;
		});
	}

	/**
	 *	设置对MainApp的引用
	 */
	public void setMainController(ApplicationController controller) {
		this.parentController = controller;
	}

	/**
	 *	新添用户
	 */
	public boolean addUser(String name, String password, int secQueIndex, String answer) {
		String sql = "insert into user(name, password, secretQuestion, secretAnswer) values(?,?,?,?)";
		if(JDBCTool.executeInsertDeleteUpdate(parentController.dbConf, sql, name,password,secQueIndex,answer)) {
			return true;
		}else {
			return false;
		}
	}
}
