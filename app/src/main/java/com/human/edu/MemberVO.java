package com.human.edu;

import com.google.gson.annotations.SerializedName;

/**
 * 이 클래스는 RestAPI에서 받은 데이터를 Get/Set 하는 기능
 */
public class MemberVO {
    @SerializedName("user_id") //Json데이터의 Key와 같은 명칭
    private String user_id;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("email")
    private String email;

    public MemberVO(String user_id, String user_name, String email) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
