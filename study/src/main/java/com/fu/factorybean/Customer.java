package com.fu.factorybean;

import java.time.LocalDateTime;

/**
 * 测试实体-
 *
 * @author Fu
 * @date 2021/6/16 10:43
 */
public class Customer {

	private String uid;

	private String msg;

	private LocalDateTime date;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
