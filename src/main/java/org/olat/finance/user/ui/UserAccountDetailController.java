package org.olat.finance.user.ui;

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
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.Identity;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.finance.user.manager.UserAccountService;
import org.olat.finance.user.payment.ui.UserAccountPaymentsListController;

public class UserAccountDetailController extends BasicController implements
Activateable2 {

	private static final String FEE_LIST_FOR_CATEGORY = "user.account.payment.list";
	
	protected Locale locale;
	protected VelocityContainer myContent;
	private Link backLink;

	Identity identity;
	protected UserAccountService userAccountService = null;
	private Controller uaPaymentList, uaDeails;
	private TabbedPane uaTabP;

	
	public UserAccountDetailController(UserRequest ureq, WindowControl wControl,
			Identity identity) {

		super(ureq, wControl);
		this.identity = identity;
		this.locale = ureq.getLocale();
		this.userAccountService  = CoreSpringFactory.getImpl(UserAccountService.class);
		
		myContent = createVelocityContainer("userAccountDetails");
		backLink = LinkFactory.createLinkBack(myContent, this);

		initTabbedPane(ureq);
		initContent();

		putInitialPanel(myContent);

	}

	private void initTabbedPane(UserRequest ureq) {
		
		uaTabP = new TabbedPane("uaTabP", locale);
		uaTabP.addListener(this);

		uaPaymentList = new UserAccountPaymentsListController(ureq,getWindowControl(), this.identity);
		listenTo(uaPaymentList);
		uaTabP.addTab(translate(FEE_LIST_FOR_CATEGORY),uaPaymentList.getInitialComponent());

		myContent.put("uaTabP", uaTabP);

	}
	
	private void initContent() {
		myContent.contextPut("name", this.identity.getName());
	}
	
	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == backLink) {
			fireEvent(ureq, Event.BACK_EVENT);
		}
	}

	@Override
	protected void doDispose() {
		if (uaPaymentList != null) {
			uaPaymentList.dispose();
			uaPaymentList= null;
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
			uaTabP.activate(ureq, entries, state);
		} else if (uaTabP != null) {
			uaTabP.setSelectedPane(translate(entryPoint));
		}

	}

}
