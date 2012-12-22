package org.olat.finance.fee.ui.wizard;

import java.util.Date;
import java.util.Set;

import org.olat.core.id.Persistable;
import org.olat.finance.fee.model.FeeCategory;
import org.olat.finance.fee.model.FeeMapping;

public class FCCopyFeeCategory implements FeeCategory{

	private static final long serialVersionUID = 8977115773421971253L;
	private String name;
	private String description;
	
	private final FeeCategory original;

	public FCCopyFeeCategory(FeeCategory original) {
		this.original = original;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FeeCategory getOriginal() {
		return original;
	}

	@Override
	public Long getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getLastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastModified(Date date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstituteId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInstituteId(String instituteId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<FeeMapping> getFeeMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFeeMapping(Set<FeeMapping> feeMapping) {
		// TODO Auto-generated method stub
		
	}
	
}
