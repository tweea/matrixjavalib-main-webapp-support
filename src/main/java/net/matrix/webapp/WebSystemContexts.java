/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import java.util.Enumeration;

import javax.servlet.ServletContext;

/**
 * Web 系统环境工具。
 */
public final class WebSystemContexts {
    private static final String ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE = WebSystemContext.class.getName() + ".ROOT";

    /**
     * 阻止实例化。
     */
    private WebSystemContexts() {
    }

    /**
     * Set the root {@link WebSystemContext} for this web app.
     * 
     * @param sc
     *     ServletContext to set the web application context for
     * @param wsc
     *     the root WebSystemContext for this web app, or {@code null} for remove
     */
    public static void setWebSystemContext(final ServletContext sc, final WebSystemContext wsc) {
        setWebSystemContext(sc, ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE, wsc);
    }

    /**
     * Set a custom {@link WebSystemContext} for this web app.
     * 
     * @param sc
     *     ServletContext to set the web application context for
     * @param attrName
     *     the name of the ServletContext attribute to set
     * @param wsc
     *     the desired WebSystemContext for this web app, or {@code null} for remove
     */
    public static void setWebSystemContext(final ServletContext sc, final String attrName, final WebSystemContext wsc) {
        sc.setAttribute(attrName, wsc);
    }

    /**
     * Find the root {@link WebSystemContext} for this web app.
     * <p>
     * Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     * 
     * @param sc
     *     ServletContext to find the web application context for
     * @return the root WebSystemContext for this web app
     * @throws IllegalStateException
     *     if the root WebSystemContext could not be found
     */
    public static WebSystemContext getRequiredWebSystemContext(final ServletContext sc)
        throws IllegalStateException {
        WebSystemContext wsc = getWebSystemContext(sc);
        if (wsc == null) {
            throw new IllegalStateException("No WebSystemContext found");
        }
        return wsc;
    }

    /**
     * Find the root {@link WebSystemContext} for this web app.
     * <p>
     * Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     * 
     * @param sc
     *     ServletContext to find the web application context for
     * @return the root WebSystemContext for this web app, or {@code null} if none
     */
    public static WebSystemContext getWebSystemContext(final ServletContext sc) {
        return getWebSystemContext(sc, ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE);
    }

    /**
     * Find a custom {@link WebSystemContext} for this web app.
     * 
     * @param sc
     *     ServletContext to find the web application context for
     * @param attrName
     *     the name of the ServletContext attribute to look for
     * @return the desired WebSystemContext for this web app, or {@code null} if none
     */
    public static WebSystemContext getWebSystemContext(final ServletContext sc, final String attrName) {
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException) attr;
        }
        if (attr instanceof Error) {
            throw (Error) attr;
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Exception) attr);
        }
        if (!(attr instanceof WebSystemContext)) {
            throw new IllegalStateException("Context attribute is not of type WebSystemContext: " + attr);
        }
        return (WebSystemContext) attr;
    }

    /**
     * Find a unique {@link WebSystemContext} for this web app: either the
     * root web app context (preferred) or a unique {@link WebSystemContext}
     * among the registered {@link ServletContext} attributes.
     * 
     * @param sc
     *     ServletContext to find the web application context for
     * @return the desired WebSystemContext for this web app, or {@code null} if none
     * @see #getWebSystemContext(ServletContext)
     * @see ServletContext#getAttributeNames()
     */
    public static WebSystemContext findWebSystemContext(final ServletContext sc) {
        WebSystemContext wac = getWebSystemContext(sc);
        if (wac == null) {
            Enumeration<String> attrNames = sc.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = attrNames.nextElement();
                Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof WebSystemContext) {
                    if (wac != null) {
                        throw new IllegalStateException("No unique WebSystemContext found");
                    }
                    wac = (WebSystemContext) attrValue;
                }
            }
        }
        return wac;
    }
}
