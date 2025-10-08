package com.example.demoCarePlan.dto;

public class Diagnosis {

	private String cid;
	private String diagnosis_time;
	private String clinical_justification;

	public Diagnosis(String cid, String diagnosis_time, String clinical_justification) {
		super();
		this.cid = cid;
		this.diagnosis_time = diagnosis_time;
		this.clinical_justification = clinical_justification;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getDiagnosis_time() {
		return diagnosis_time;
	}

	public void setDiagnosis_time(String diagnosis_time) {
		this.diagnosis_time = diagnosis_time;
	}

	public String getClinical_justification() {
		return clinical_justification;
	}

	public void setClinical_justification(String clinical_justification) {
		this.clinical_justification = clinical_justification;
	}

}
