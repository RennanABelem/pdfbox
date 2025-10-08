package com.example.demoCarePlan.dto;

public class HospitalAdmission {

	private String admission_days;
	private boolean blood_reservation;
	private boolean uti_reservation;

	public HospitalAdmission(String admission_days, boolean blood_reservation, boolean uti_reservation) {
		super();
		this.admission_days = admission_days;
		this.blood_reservation = blood_reservation;
		this.uti_reservation = uti_reservation;
	}

	public String getAdmission_days() {
		return admission_days;
	}

	public void setAdmission_days(String admission_days) {
		this.admission_days = admission_days;
	}

	public boolean isBlood_reservation() {
		return blood_reservation;
	}

	public void setBlood_reservation(boolean blood_reservation) {
		this.blood_reservation = blood_reservation;
	}

	public boolean isUti_reservation() {
		return uti_reservation;
	}

	public void setUti_reservation(boolean uti_reservation) {
		this.uti_reservation = uti_reservation;
	}

}
