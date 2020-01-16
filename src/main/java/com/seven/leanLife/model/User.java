package com.seven.leanLife.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String password;
    private Integer secretquestionId;
    private String secretanswer;
    private String registerdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSecretquestionId() {
        return secretquestionId;
    }

    public void setSecretquestionId(Integer secretquestionIndex) {
        this.secretquestionId = secretquestionIndex;
    }

    public String getSecretanswer() {
        return secretanswer;
    }

    public void setSecretanswer(String secretanswer) {
        this.secretanswer = secretanswer;
    }

    public String getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(String registerdate) {
        this.registerdate = registerdate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append(", secretquestion=").append(secretquestionId.toString());
        sb.append(", secretanswer=").append(secretanswer);
        sb.append(", registerdate=").append(registerdate);
        sb.append("]");
        return sb.toString();
    }
}