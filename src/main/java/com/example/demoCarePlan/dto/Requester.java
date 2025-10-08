package com.example.demoCarePlan.dto;

public class Requester {

	private String name;
	private String crm;
	private String state;
	private String phone_number;
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Requester(String name, String crm, String state, String phone_number, String email) {
		super();
		this.name = name;
		this.crm = crm;
		this.state = state;
		this.phone_number = phone_number;
		this.email = email;
	}

}
