package com.luwei.domain;

import java.io.Serializable;

public class UserInfo implements Serializable{
	/**
	 * 用户信息
	 */
	private static final long serialVersionUID = 1L;
	int id;
	String preId;
	String really_name;
	String nick_name;
	String email;
	String avatar_url;
    String city;
    String province;

	public UserInfo(){}
	
	public UserInfo(int id, String preId, String really_name, String nick_name,
			String email, String avatar_url) {
		super();
		this.id = id;
		this.preId = preId;
		this.really_name = really_name;
		this.nick_name = nick_name;
		this.email = email;
		this.avatar_url = avatar_url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPreId() {
		return preId;
	}

	public void setPreId(String preId) {
		this.preId = preId;
	}

	public String getReally_name() {
		return really_name;
	}

	public void setReally_name(String really_name) {
		this.really_name = really_name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}
