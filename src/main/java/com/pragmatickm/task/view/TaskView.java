/*
 * pragmatickm-task-view - SemanticCMS view of tasks in the current page and all children.
 * Copyright (C) 2016, 2017, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of pragmatickm-task-view.
 *
 * pragmatickm-task-view is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pragmatickm-task-view is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with pragmatickm-task-view.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.pragmatickm.task.view;

import com.aoapps.html.servlet.FlowContent;
import com.aoapps.servlet.http.Dispatcher;
import com.pragmatickm.task.model.Task;
import com.pragmatickm.task.model.User;
import com.pragmatickm.task.servlet.TaskUtil;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.PageUtils;
import com.semanticcms.core.servlet.SemanticCMS;
import com.semanticcms.core.servlet.View;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * View of all tasks in the current page and all children.
 */
public final class TaskView extends View {

	public static final String NAME = "tasks";

	private static final String JSPX_TARGET = "/pragmatickm-task-view/tasks.inc.jspx";

	@WebListener("Registers the \"" + NAME + "\" view in SemanticCMS.")
	public static class Initializer implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent event) {
			SemanticCMS.getInstance(event.getServletContext()).addView(new TaskView());
		}
		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

	private TaskView() {
		// Do nothing
	}

	@Override
	public Group getGroup() {
		return Group.VARIABLE;
	}

	@Override
	public String getDisplay() {
		return "Tasks";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isApplicable(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) throws ServletException, IOException {
		return PageUtils.hasElement(servletContext, request, response, page, Task.class, true);
	}

	@Override
	public Map<String, List<String>> getLinkParams(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = TaskUtil.getUser(request, response);
		if(user == null) {
			return Collections.emptyMap();
		} else {
			return Collections.singletonMap(
				"user",
				Collections.singletonList(user.name())
			);
		}
	}

	@Override
	public String getTitle(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) {
		StringBuilder title = new StringBuilder();
		{ // scoping block
			User user = TaskUtil.getUser(request, response);
			if(user == null) {
				title.append("All Tasks");
			} else if(user == User.Unassigned) {
				title.append("Unassigned Tasks");
			} else {
				title.append("Tasks for ").append(user);
			}
		}
		title.append(TITLE_SEPARATOR).append(page.getTitle());
		{ // scoping block
			String bookTitle = page.getPageRef().getBook().getTitle();
			if(bookTitle != null && !bookTitle.isEmpty()) {
				title.append(TITLE_SEPARATOR).append(bookTitle);
			}
		}
		return title.toString();
	}

	@Override
	public String getDescription(Page page) {
		return null;
	}

	@Override
	public String getKeywords(Page page) {
		return null;
	}

	/**
	 * All info from tasks is available on the individual pages.
	 */
	@Override
	public boolean getAllowRobots(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) {
		return false;
	}

	@Override
	public <__ extends FlowContent<__>> void doView(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, __ flow, Page page) throws ServletException, IOException, SkipPageException {
		Dispatcher.include(
			servletContext,
			JSPX_TARGET,
			request,
			response,
			Collections.singletonMap("page", page)
		);
	}
}
