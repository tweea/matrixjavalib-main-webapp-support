/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.webapp;

import javax.servlet.ServletContext;

import net.matrix.app.DefaultSystemContext;

/**
 * 默认的 Web 系统环境。
 */
public class DefaultWebSystemContext
	extends DefaultSystemContext
	implements WebSystemContext {
	/**
	 * Servlet 上下文。
	 */
	private final ServletContext servletContext;

	/**
	 * 默认构造器。
	 */
	public DefaultWebSystemContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}
}
