/**
 * This file is part of mycollab-mobile.
 *
 * mycollab-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */
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
package com.esofthead.mycollab.mobile.module.user.ui.components;

import java.util.List;

import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.core.arguments.SearchRequest;
import com.esofthead.mycollab.core.arguments.SetSearchField;
import com.esofthead.mycollab.module.billing.RegisterStatusConstants;
import com.esofthead.mycollab.module.user.domain.SimpleUser;
import com.esofthead.mycollab.module.user.domain.criteria.UserSearchCriteria;
import com.esofthead.mycollab.module.user.service.UserService;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.UserAvatarControlFactory;
import com.vaadin.ui.ListSelect;

/**
 * 
 * @author MyCollab Ltd.
 * @since 2.0
 * 
 */
public class ActiveUserComboBox extends ListSelect {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ActiveUserComboBox() {
		super();
		this.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		this.setRows(1);

		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setSaccountid(new NumberSearchField(SearchField.AND,
				AppContext.getAccountId()));
		criteria.setRegisterStatuses(new SetSearchField<String>(
				SearchField.AND,
				new String[] { RegisterStatusConstants.ACTIVE }));

		UserService userService = ApplicationContextUtil
				.getSpringBean(UserService.class);
		List<SimpleUser> userList = userService
				.findPagableListByCriteria(new SearchRequest<UserSearchCriteria>(
						criteria, 0, Integer.MAX_VALUE));
		loadUserList(userList);

	}

	public ActiveUserComboBox(List<SimpleUser> userList) {
		super();
		this.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		loadUserList(userList);
	}

	private void loadUserList(List<SimpleUser> userList) {

		for (SimpleUser user : userList) {
			this.addItem(user.getUsername());
			this.setItemCaption(user.getUsername(), user.getDisplayName());
			this.setItemIcon(
					user.getUsername(),
					UserAvatarControlFactory.createAvatarResource(
							user.getAvatarid(), 16));
		}
	}
}
