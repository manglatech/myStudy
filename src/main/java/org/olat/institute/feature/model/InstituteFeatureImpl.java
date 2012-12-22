package org.olat.institute.feature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.olat.core.id.Persistable;

@Entity
@Table(name = "o_institute_features_control")
public class InstituteFeatureImpl implements InstituteFeature {

	private static final long serialVersionUID = 5201149276076998083L;
	
	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "institute_features_control_id", updatable = false, nullable = false)
	private Long key;

	@Column(name = "institute_id")
	private String instituteId;
	
	@Column(name = "feature_id")
	private Long featureId;
	
	@Column(name = "feature_name")
	private String featureName;

	@Column(name = "status")
	private Integer status;

	public String getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public void setKey(Long key) {
		this.key = key;
	}
	public Long getKey() {
		return key;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		if (this.getKey().compareTo(persistable.getKey()) == 0)
			return true;
		else
			return false;
	}
	public Integer getStatus(){
		return status;
	}
	public void setStatus(Integer status){
		this.status = status;
	}
}
