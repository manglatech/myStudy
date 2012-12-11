package org.olat.finance.fee.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.olat.core.commons.persistence.OLATPersistenceObject;

@Entity
@Table(name = "o_fee_mapping")
public class FeeMappingImpl extends OLATPersistenceObject implements FeeMapping {

	@Transient
	private static final long serialVersionUID = -3038675094628430416L;
	
	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hilo-strategy", strategy = "hilo")
	@GeneratedValue(generator = "hilo-strategy")
	@Column(name = "fee_mapping_id", updatable = false, nullable = false)
	private Long key;

	@Column(name = "price")
	private Integer price;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity = FeeImpl.class)
    @JoinColumn(name="fk_fee_id")
	private Fee fee;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity = FeeCategoryImpl.class)
    @JoinColumn(name="fk_fee_category_id")
	private FeeCategory feeCategory;

	@Column(name = "lastmodified")
	private Date lastModified;
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Fee getFee() {
		return fee;
	}

	public void setFee(Fee fee) {
		this.fee = fee;
	}

	public FeeCategory getFeeCategory() {
		return feeCategory;
	}

	public void setFeeCategory(FeeCategory feeCategory) {
		this.feeCategory = feeCategory;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
