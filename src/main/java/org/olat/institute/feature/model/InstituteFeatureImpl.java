package org.olat.institute.feature.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.olat.core.commons.persistence.OLATPersistenceObject;

public class InstituteFeatureImpl extends OLATPersistenceObject implements InstituteFeature {

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
}
