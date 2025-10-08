package com.example.demoCarePlan.build.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demoCarePlan.dto.PdfRequestDTO;

@Service
public class MapTemplateColunaHelper {

	Map<String, Object> dados = new HashMap<String, Object>();

	public Map<String, Object> gerarDados(PdfRequestDTO dto) {
		dados.put("type", "Plano de Cuidado Terapêutico");
		
		dados.put("titulo/beneficiary.name", "Solicito para " + dto.getBeneficiary().getName());
		dados.put("beneficiary.name", dto.getBeneficiary().getName()); 
		dados.put("beneficiary.code", dto.getBeneficiary().getCode());

		dados.put("requester.name", dto.getRequester().getName()); 
		dados.put("requester.crm/state", dto.getRequester().getCrm() + "/" + dto.getRequester().getState());

		dados.put("practitioner.name", ""); 
		dados.put("practitioner.crm", "");
		dados.put("practitioner.state", ""); 
		dados.put("practitioner.phone_number", "");
		dados.put("practitioner.email", "");

		dados.put("previous_treatment.had_previous_treatment", true);
		dados.put("previous_treatment.treatments", List.of());

		dados.put("diagnosis.cid", ""); 
		dados.put("diagnosis.diagnosis_time", "");
		dados.put("diagnosis.clinical_justification", "");

		dados.put("procedure.surgery_approach", ""); 
		dados.put("procedure.tuss", "");
		dados.put("procedure.procedure_package", ""); 
		dados.put("procedure.hospital_package", "");
		dados.put("procedure.additional_packages", List.of()); 
		dados.put("procedure.level", "");
		dados.put("procedure.arthrodesis", true); 
		dados.put("procedure.distraction", false);

		dados.put("hospital_admission.admission_days", "");
		dados.put("hospital_admission.blood_reservation", true);
		dados.put("hospital_admission.uti_reservation", true);

		return dados;
	}

}
