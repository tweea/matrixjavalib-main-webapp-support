/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import javax.servlet.ServletContext;

import net.matrix.app.SystemContext;

/**
 * 基于 Web 的系统环境。
 */
public interface WebSystemContext
    extends SystemContext {
    /**
     * Return the standard Servlet API ServletContext for this application.
     */
    ServletContext getServletContext();
}
