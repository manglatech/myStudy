package org.olat.institute.manager;

import java.util.List;

import org.olat.institute.model.Institute;


public interface InstituteService {
	
	public Institute createInstitute(String id, String name, String numberOfUsers);
	public List<Institute> findAllInstitute();
	public Institute updateInstitute(Long key, String name, String numberOfUsers);
	public void deleteInstitute(List<Institute> selectedItems);
	
}
