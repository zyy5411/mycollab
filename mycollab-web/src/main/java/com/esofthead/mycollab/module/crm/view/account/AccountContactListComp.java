/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file is part of mycollab-web.
 * <p>
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.view.account;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.module.crm.CrmLinkGenerator;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.Account;
import com.esofthead.mycollab.module.crm.domain.SimpleContact;
import com.esofthead.mycollab.module.crm.domain.criteria.ContactSearchCriteria;
import com.esofthead.mycollab.module.crm.i18n.ContactI18nEnum;
import com.esofthead.mycollab.module.crm.service.ContactService;
import com.esofthead.mycollab.module.crm.ui.CrmAssetsManager;
import com.esofthead.mycollab.module.crm.ui.components.RelatedListComp2;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.maddon.button.MButton;

/**
 * @author MyCollab Ltd.
 * @since 4.0
 */
public class AccountContactListComp extends
        RelatedListComp2<ContactService, ContactSearchCriteria, SimpleContact> {
    private static final long serialVersionUID = -6451639268047881135L;

    private Account account;

    public AccountContactListComp() {
        super(ApplicationContextUtil.getSpringBean(ContactService.class), 20);
        this.setBlockDisplayHandler(new AccountContactBlockDisplay());
    }

    @Override
    protected Component generateTopControls() {
        VerticalLayout controlsBtnWrap = new VerticalLayout();
        controlsBtnWrap.setWidth("100%");
        final SplitButton controlsBtn = new SplitButton();
        controlsBtn.setSizeUndefined();
        controlsBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_CONTACT));
        controlsBtn.addStyleName(UIConstants.THEME_GREEN_LINK);
        controlsBtn.setCaption(AppContext
                .getMessage(ContactI18nEnum.BUTTON_NEW_CONTACT));
        controlsBtn.setIcon(FontAwesome.PLUS);
        controlsBtn
                .addClickListener(new SplitButton.SplitButtonClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void splitButtonClick(
                            final SplitButton.SplitButtonClickEvent event) {
                        fireNewRelatedItem("");
                    }
                });
        final Button selectBtn = new Button("Select from existing contacts",
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final AccountContactSelectionWindow contactsWindow = new AccountContactSelectionWindow(
                                AccountContactListComp.this);
                        final ContactSearchCriteria criteria = new ContactSearchCriteria();
                        criteria.setSaccountid(new NumberSearchField(AppContext
                                .getAccountId()));
                        UI.getCurrent().addWindow(contactsWindow);
                        contactsWindow.setSearchCriteria(criteria);
                        controlsBtn.setPopupVisible(false);
                    }
                });
        selectBtn.setIcon(CrmAssetsManager.getAsset(CrmTypeConstants.CONTACT));
        OptionPopupContent buttonControlLayout = new OptionPopupContent();
        buttonControlLayout.addOption(selectBtn);
        controlsBtn.setContent(buttonControlLayout);

        controlsBtnWrap.addComponent(controlsBtn);
        controlsBtnWrap.setComponentAlignment(controlsBtn,
                Alignment.MIDDLE_RIGHT);
        return controlsBtnWrap;
    }

    public void displayContacts(final Account account) {
        this.account = account;
        loadContacts();
    }

    private void loadContacts() {
        final ContactSearchCriteria criteria = new ContactSearchCriteria();
        criteria.setSaccountid(new NumberSearchField(SearchField.AND,
                AppContext.getAccountId()));
        criteria.setAccountId(new NumberSearchField(SearchField.AND, account
                .getId()));
        setSearchCriteria(criteria);
    }

    @Override
    public void refresh() {
        loadContacts();
    }

    public class AccountContactBlockDisplay implements
            BlockDisplayHandler<SimpleContact> {

        @Override
        public Component generateBlock(final SimpleContact contact,
                                       int blockIndex) {
            CssLayout beanBlock = new CssLayout();
            beanBlock.addStyleName("bean-block");
            beanBlock.setWidth("350px");

            VerticalLayout blockContent = new VerticalLayout();
            HorizontalLayout blockTop = new HorizontalLayout();
            blockTop.setSpacing(true);
            CssLayout iconWrap = new CssLayout();
            iconWrap.setStyleName("icon-wrap");
            FontIconLabel contactAvatar = new FontIconLabel(CrmAssetsManager.getAsset(CrmTypeConstants.CONTACT));
            iconWrap.addComponent(contactAvatar);
            blockTop.addComponent(iconWrap);

            VerticalLayout contactInfo = new VerticalLayout();
            contactInfo.setSpacing(true);

            MButton btnDelete = new MButton(FontAwesome.TRASH_O);
            btnDelete.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent clickEvent) {
                    ConfirmDialogExt.show(
                            UI.getCurrent(),
                            AppContext.getMessage(
                                    GenericI18Enum.DIALOG_DELETE_TITLE,
                                    SiteConfiguration.getSiteName()),
                            AppContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                            AppContext.getMessage(GenericI18Enum.BUTTON_YES),
                            AppContext.getMessage(GenericI18Enum.BUTTON_NO),
                            new ConfirmDialog.Listener() {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public void onClose(ConfirmDialog dialog) {
                                    if (dialog.isConfirmed()) {
                                        ContactService contactService = ApplicationContextUtil
                                                .getSpringBean(ContactService.class);
                                        contact.setAccountid(null);
                                        contactService.updateWithSession(
                                                contact,
                                                AppContext.getUsername());
                                        AccountContactListComp.this.refresh();
                                    }
                                }
                            });
                }
            });
            btnDelete.addStyleName(UIConstants.BUTTON_ICON_ONLY);

            blockContent.addComponent(btnDelete);
            blockContent.setComponentAlignment(btnDelete, Alignment.TOP_RIGHT);

            Label contactName = new Label(String.format("Name: <a href='%s%s'>%s</a>", SiteConfiguration.getSiteUrl(AppContext.getUser()
                    .getSubdomain()), CrmLinkGenerator.generateCrmItemLink(
                    CrmTypeConstants.CONTACT, contact.getId()), contact.getContactName()), ContentMode.HTML);

            contactInfo.addComponent(contactName);

            Label contactTitle = new Label("Title: "
                    + (contact.getTitle() != null ? contact.getTitle() : ""));
            contactInfo.addComponent(contactTitle);

            Label contactEmail = new Label(String.format("Email: %s", contact.getEmail() != null ?
                    String.format("<a href='mailto:%s'>%s</a>", contact.getEmail(), contact.getEmail()) : ""), ContentMode.HTML);
            contactInfo.addComponent(contactEmail);

            Label contactOfficePhone = new Label(
                    String.format("Office Phone: %s", contact.getOfficephone() != null ? contact
                            .getOfficephone() : ""));
            contactInfo.addComponent(contactOfficePhone);

            blockTop.addComponent(contactInfo);
            blockTop.setExpandRatio(contactInfo, 1.0f);
            blockTop.setWidth("100%");
            blockContent.addComponent(blockTop);

            blockContent.setWidth("100%");

            beanBlock.addComponent(blockContent);
            return beanBlock;
        }

    }

}
