package org.olat.finance.fee.model;

import java.util.List;

import org.olat.core.gui.control.Event;

public class MultipleFeeSelectEvent extends Event {

	private List<Fee> fee;
	
	public MultipleFeeSelectEvent(List<Fee> fee) {
		super("MultipleFeeSelected");
		this.fee = fee;
	}
		
	public List<Fee> getChosenFees() {
		return fee;
	}
}
