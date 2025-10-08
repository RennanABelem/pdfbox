package com.example.demoCarePlan.dto;

import java.util.List;

public class PreviousTreatment {

	private boolean had_previous_treatment;
	private List<String> treatments;

	public PreviousTreatment(boolean had_previous_treatment, List<String> treatments) {
		super();
		this.had_previous_treatment = had_previous_treatment;
		this.treatments = treatments;
	}

	public boolean isHad_previous_treatment() {
		return had_previous_treatment;
	}

	public void setHad_previous_treatment(boolean had_previous_treatment) {
		this.had_previous_treatment = had_previous_treatment;
	}

	public List<String> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<String> treatments) {
		this.treatments = treatments;
	}

}
