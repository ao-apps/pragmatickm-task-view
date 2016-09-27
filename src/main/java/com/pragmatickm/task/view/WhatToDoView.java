/*
 * pragmatickm-task-view - SemanticCMS view of tasks in the current page and all children.
 * Copyright (C) 2016  AO Industries, Inc.
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
 * along with pragmatickm-task-view.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pragmatickm.task.view;

import com.aoindustries.servlet.http.Dispatcher;
import com.pragmatickm.task.model.User;
import com.pragmatickm.task.servlet.TaskUtil;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.View;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * View of tasks to do in the current page and all children.
 */
public class WhatToDoView extends View {

	static final String VIEW_NAME = "what-to-do";

	private static final String JSPX_TARGET = "/pragmatickm-task-view/what-to-do.inc.jspx";

	@Override
	public Group getGroup() {
		return Group.VARIABLE;
	}

	@Override
	public String getDisplay() {
		return "What To Do";
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}

	@Override
	public boolean isApplicable(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) throws ServletException, IOException {
		return TaskUtil.hasAssignedTask(
			servletContext,
			request,
			response,
			page,
			TaskUtil.getUser(
				request,
				response
			),
			true
		);
	}

	@Override
	public Map<String,List<String>> getLinkParams(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) {
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
		User user = TaskUtil.getUser(request, response);
		if(user == null) {
			return "Everything To Do" + TITLE_SEPARATOR + page.getTitle() + TITLE_SEPARATOR + page.getPageRef().getBook().getTitle();
		} else if(user == User.Unassigned) {
			return "Unassigned What To Do" + TITLE_SEPARATOR + page.getTitle() + TITLE_SEPARATOR + page.getPageRef().getBook().getTitle();
		} else {
			return "What To Do for " + user + TITLE_SEPARATOR + page.getTitle() + TITLE_SEPARATOR + page.getPageRef().getBook().getTitle();
		}
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
	public void doView(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) throws ServletException, IOException, SkipPageException {
		Dispatcher.include(
			servletContext,
			JSPX_TARGET,
			request,
			response,
			Collections.singletonMap("page", page)
		);
	}
}
