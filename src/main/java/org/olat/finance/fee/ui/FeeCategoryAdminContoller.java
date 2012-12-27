package org.olat.finance.fee.ui;

import java.util.List;
import java.util.Locale;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.finance.fee.manager.FeeService;
import org.olat.finance.fee.model.FeeCategory;

public class FeeCategoryAdminContoller extends BasicController implements
		Activateable2 {

	private static final String FEE_LIST_FOR_CATEGORY = "admin.feelist";
	private static final String FEE_IDENTITY_MAPPING_DETAILS = "admin.fee.identity.mapping.details";

	protected FeeCategoryListTableDataModel feeModel;
	protected FeeCategory chosenFeeCategory = null;
	protected FeeService feeService = null;

	private TabbedPane feeCategoryTabP;
	private Controller fcFeeList, fcFeeIdentityList, fcView;

	protected Locale locale;
	protected CloseableModalController cmc;

	protected VelocityContainer myContent;
	private Link backLink;

	public FeeCategoryAdminContoller(UserRequest ureq, WindowControl wControl,
			FeeCategory feeCategory) {

		super(ureq, wControl);
		this.locale = ureq.getLocale();
		this.feeService = CoreSpringFactory.getImpl(FeeService.class);
		this.chosenFeeCategory = feeCategory;
		this.fcView = new FeeCategoryViewContoller(ureq, wControl, feeCategory);

		myContent = createVelocityContainer("feeCategoryAdmin");
		myContent.put("feeCategoryDetails", fcView.getInitialComponent());
		backLink = LinkFactory.createLinkBack(myContent, this);

		initTabbedPane(ureq);
		initButtons(ureq);
		initContent();

		putInitialPanel(myContent);

	}

	private void initTabbedPane(UserRequest ureq) {
		
		feeCategoryTabP = new TabbedPane("fcTabP", locale);
		feeCategoryTabP.addListener(this);

		fcFeeList = new FeeCategoryMappingListController(ureq,
				getWindowControl(), this.chosenFeeCategory);
		listenTo(fcFeeList);

		feeCategoryTabP.addTab(translate(FEE_LIST_FOR_CATEGORY),
				fcFeeList.getInitialComponent());

		// Sample Another One Only:
		fcFeeIdentityList = new FeeIdentityMappingListController(ureq, getWindowControl(),
				this.chosenFeeCategory);
		feeCategoryTabP.addTab(translate(FEE_IDENTITY_MAPPING_DETAILS),
				fcFeeIdentityList.getInitialComponent());

		myContent.put("fcTabP", feeCategoryTabP);

	}

	private void initContent() {
		myContent.contextPut("name", this.chosenFeeCategory.getName());
		myContent.contextPut("description",
				this.chosenFeeCategory.getDescription());
	}

	private void initButtons(UserRequest ureq) {

	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == backLink) {
			fireEvent(ureq, Event.BACK_EVENT);
		}
	}
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		fireEvent(ureq, event);
	}

	@Override
	protected void doDispose() {
		if (fcFeeList != null) {
			fcFeeList.dispose();
			fcFeeList = null;
		}
		if (fcFeeIdentityList != null) {
			fcFeeIdentityList.dispose();
			fcFeeIdentityList = null;
		}
	}

	public void setBackButtonEnabled(boolean backButtonEnabled) {
		if (myContent != null)
			myContent.contextPut("showButton",
					Boolean.valueOf(backButtonEnabled));
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries,
			StateEntry state) {
		if (entries == null || entries.isEmpty())
			return;

		String entryPoint = entries.get(0).getOLATResourceable()
				.getResourceableTypeName();
		if ("tab".equals(entryPoint)) {
			feeCategoryTabP.activate(ureq, entries, state);
		} else if (feeCategoryTabP != null) {
			feeCategoryTabP.setSelectedPane(translate(entryPoint));
		}

	}

}
