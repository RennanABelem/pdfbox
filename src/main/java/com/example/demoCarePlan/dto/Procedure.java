package com.example.demoCarePlan.dto;

import java.util.List;

public class Procedure {

	private String surgery_approach;
	private String tuss;
	private String procedure_package;
	private String hospital_package;
	private List<String> additional_packages;
	private String level;
	private boolean arthrodesis;
	private boolean distraction;

	public Procedure(String surgery_approach, String tuss, String procedure_package, String hospital_package,
			List<String> additional_packages, String level, boolean arthrodesis, boolean distraction) {
		super();
		this.surgery_approach = surgery_approach;
		this.tuss = tuss;
		this.procedure_package = procedure_package;
		this.hospital_package = hospital_package;
		this.additional_packages = additional_packages;
		this.level = level;
		this.arthrodesis = arthrodesis;
		this.distraction = distraction;
	}

	public String getSurgery_approach() {
		return surgery_approach;
	}

	public void setSurgery_approach(String surgery_approach) {
		this.surgery_approach = surgery_approach;
	}

	public String getTuss() {
		return tuss;
	}

	public void setTuss(String tuss) {
		this.tuss = tuss;
	}

	public String getProcedure_package() {
		return procedure_package;
	}

	public void setProcedure_package(String procedure_package) {
		this.procedure_package = procedure_package;
	}

	public String getHospital_package() {
		return hospital_package;
	}

	public void setHospital_package(String hospital_package) {
		this.hospital_package = hospital_package;
	}

	public List<String> getAdditional_packages() {
		return additional_packages;
	}

	public void setAdditional_packages(List<String> additional_packages) {
		this.additional_packages = additional_packages;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isArthrodesis() {
		return arthrodesis;
	}

	public void setArthrodesis(boolean arthrodesis) {
		this.arthrodesis = arthrodesis;
	}

	public boolean isDistraction() {
		return distraction;
	}

	public void setDistraction(boolean distraction) {
		this.distraction = distraction;
	}

}
