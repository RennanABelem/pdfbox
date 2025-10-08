package com.example.demoCarePlan.dto;

import java.util.List;

public class PdfRequestDTO {

	private String requestId;
	private String type;
	private Beneficiary beneficiary;
	private Requester requester;
	private Practitioner practitioner;
	private PreviousTreatment previousTreatment;
	private Diagnosis diagnosis;
	private Procedure procedure;
	private HospitalAdmission hospitalAdmission;

	public PdfRequestDTO(boolean oi) {
		super();
		this.requestId = "requestId";
		this.type = "type";
		this.beneficiary = new Beneficiary("Rennan belem", "5xxx4xxx3xxx2xxx1xxx0");
		this.requester = new Requester("Dr Joao", "CRM-5xxx55555", "SP", "", "");
		this.practitioner = new Practitioner("", "", "", "", "");
		this.previousTreatment = new PreviousTreatment(true, List.of("1", "2", "3"));
		this.diagnosis = new Diagnosis("Dia", "type", "justificativa...");
		this.procedure = new Procedure("type", "type", "type", "requestId", null, "type", false, false);
		this.hospitalAdmission = new HospitalAdmission("type", false, false);
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Beneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	public Requester getRequester() {
		return requester;
	}

	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	public Practitioner getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(Practitioner practitioner) {
		this.practitioner = practitioner;
	}

	public PreviousTreatment getPreviousTreatment() {
		return previousTreatment;
	}

	public void setPreviousTreatment(PreviousTreatment previousTreatment) {
		this.previousTreatment = previousTreatment;
	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public HospitalAdmission getHospitalAdmission() {
		return hospitalAdmission;
	}

	public void setHospitalAdmission(HospitalAdmission hospitalAdmission) {
		this.hospitalAdmission = hospitalAdmission;
	}

	public PdfRequestDTO() {
		super();
	}

}
