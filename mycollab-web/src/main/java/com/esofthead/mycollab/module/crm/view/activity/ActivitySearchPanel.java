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
package com.esofthead.mycollab.module.crm.view.activity;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchCriteria;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.criteria.ActivitySearchCriteria;
import com.esofthead.mycollab.module.crm.events.ActivityEvent;
import com.esofthead.mycollab.module.crm.ui.components.CrmViewHeader;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.peter.buttongroup.ButtonGroup;

/**
 * @author MyCollab Ltd.
 * @since 2.0
 */
public class ActivitySearchPanel extends DefaultGenericSearchPanel<ActivitySearchCriteria> {
    private static final long serialVersionUID = 1L;

    @Override
    protected SearchLayout<ActivitySearchCriteria> createAdvancedSearchLayout() {
        return null;
    }

    @Override
    protected HeaderWithFontAwesome buildSearchTitle() {
        return new CrmViewHeader(CrmTypeConstants.ACTIVITY, "Events");
    }

    @Override
    protected void buildExtraControls() {
        final SplitButton controlsBtn = new SplitButton();
        controlsBtn.setSizeUndefined();
        controlsBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_CALL)
                || AppContext.canWrite(RolePermissionCollections.CRM_MEETING));
        controlsBtn.addStyleName(UIConstants.THEME_GREEN_LINK);
        controlsBtn.setIcon(FontAwesome.PLUS);
        controlsBtn.setCaption("New Task");
        controlsBtn
                .addClickListener(new SplitButton.SplitButtonClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void splitButtonClick(
                            final SplitButton.SplitButtonClickEvent event) {
                        EventBusFactory.getInstance().post(
                                new ActivityEvent.TaskAdd(this, null));
                    }
                });

        final OptionPopupContent btnControlsLayout = new OptionPopupContent();
        controlsBtn.setContent(btnControlsLayout);

        final Button createMeetingBtn = new Button("New Meeting",
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final Button.ClickEvent event) {
                        controlsBtn.setPopupVisible(false);
                        EventBusFactory.getInstance().post(
                                new ActivityEvent.MeetingAdd(this, null));
                    }
                });
        btnControlsLayout.addOption(createMeetingBtn);
        createMeetingBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_MEETING));
        final Button createCallBtn = new Button("New Call",
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final Button.ClickEvent event) {
                        controlsBtn.setPopupVisible(false);
                        EventBusFactory.getInstance().post(
                                new ActivityEvent.CallAdd(this, null));
                    }
                });
        createCallBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_CALL));
        btnControlsLayout.addOption(createCallBtn);

        addHeaderRight(controlsBtn);

        ButtonGroup viewSwitcher = new ButtonGroup();

        Button calendarViewBtn = new Button("Calendar",
                new Button.ClickListener() {
                    private static final long serialVersionUID = -793215433929884575L;

                    @Override
                    public void buttonClick(ClickEvent evt) {
                        EventBusFactory.getInstance().post(
                                new ActivityEvent.GotoCalendar(this, null));
                    }
                });
        calendarViewBtn.addStyleName(UIConstants.THEME_GREEN_LINK);
        viewSwitcher.addButton(calendarViewBtn);

        Button activityListBtn = new Button("Activities");
        activityListBtn.setStyleName("selected");
        activityListBtn.addStyleName(UIConstants.THEME_GREEN_LINK);
        viewSwitcher.addButton(activityListBtn);

        addHeaderRight(viewSwitcher);
    }

    @Override
    protected SearchLayout<ActivitySearchCriteria> createBasicSearchLayout() {
        return new EventBasicSearchLayout();
    }

    @SuppressWarnings({"serial", "rawtypes"})
    private class EventBasicSearchLayout extends BasicSearchLayout {

        private TextField nameField;
        private CheckBox myItemCheckbox;

        @SuppressWarnings("unchecked")
        public EventBasicSearchLayout() {
            super(ActivitySearchPanel.this);
        }

        @Override
        public ComponentContainer constructHeader() {
            return ActivitySearchPanel.this.constructHeader();
        }

        @Override
        public ComponentContainer constructBody() {
            final MHorizontalLayout basicSearchBody = new MHorizontalLayout().withMargin(true);

            this.nameField = new TextField();
            this.nameField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
            basicSearchBody.with(nameField).withAlign(nameField,
                    Alignment.MIDDLE_CENTER);

            this.myItemCheckbox = new CheckBox(
                    AppContext
                            .getMessage(GenericI18Enum.SEARCH_MYITEMS_CHECKBOX));
            this.myItemCheckbox.setWidth("75px");
            basicSearchBody.with(myItemCheckbox).withAlign(myItemCheckbox,
                    Alignment.MIDDLE_CENTER);

            final Button searchBtn = new Button(
                    AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
            searchBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
            searchBtn.setIcon(FontAwesome.SEARCH);

            searchBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    EventBasicSearchLayout.this.callSearchAction();
                }
            });
            basicSearchBody.with(searchBtn).withAlign(searchBtn,
                    Alignment.MIDDLE_LEFT);

            final Button clearBtn = new Button(
                    AppContext.getMessage(GenericI18Enum.BUTTON_CLEAR));
            clearBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
            clearBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    EventBasicSearchLayout.this.nameField.setValue("");
                }
            });
            basicSearchBody.with(clearBtn).withAlign(clearBtn,
                    Alignment.MIDDLE_CENTER);
            return basicSearchBody;
        }

        @Override
        protected SearchCriteria fillUpSearchCriteria() {
            ActivitySearchCriteria searchCriteria = new ActivitySearchCriteria();
            searchCriteria
                    .setSaccountid(new NumberSearchField(SearchField.AND,
                            AppContext.getAccountId()));
            return searchCriteria;
        }
    }
}
