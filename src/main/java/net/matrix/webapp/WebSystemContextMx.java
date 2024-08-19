/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletContext;

import net.matrix.java.lang.UncheckedException;
import net.matrix.java.util.EnumerationIterable;
import net.matrix.text.ResourceBundleMessageFormatter;

/**
 * 基于 Web 的系统环境工具。
 */
@ThreadSafe
public final class WebSystemContextMx {
    /**
     * 区域相关资源。
     */
    private static final ResourceBundleMessageFormatter RBMF = new ResourceBundleMessageFormatter(WebSystemContextMx.class).useCurrentLocale();

    /**
     * the root name of the ServletContext attribute to set.
     */
    private static final String ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE = WebSystemContext.class.getName() + ".ROOT";

    /**
     * 阻止实例化。
     */
    private WebSystemContextMx() {
    }

    /**
     * Set the root {@link WebSystemContext} for this web app.
     *
     * @param servletContext
     *     ServletContext to set the web application context for.
     * @param context
     *     the root WebSystemContext for this web app, or {@code null} for remove.
     */
    public static void setWebSystemContext(@Nonnull ServletContext servletContext, @Nullable WebSystemContext context) {
        setWebSystemContext(servletContext, ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE, context);
    }

    /**
     * Set a custom {@link WebSystemContext} for this web app.
     *
     * @param servletContext
     *     ServletContext to set the web application context for.
     * @param attributeName
     *     the name of the ServletContext attribute to set.
     * @param context
     *     the desired WebSystemContext for this web app, or {@code null} for remove.
     */
    public static void setWebSystemContext(@Nonnull ServletContext servletContext, @Nonnull String attributeName, @Nullable WebSystemContext context) {
        servletContext.setAttribute(attributeName, context);
    }

    /**
     * Find the root {@link WebSystemContext} for this web app.<br>
     * Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     *
     * @param servletContext
     *     ServletContext to find the web application context for.
     * @return the root WebSystemContext for this web app.
     * @throws IllegalStateException
     *     if the root WebSystemContext could not be found.
     */
    @Nonnull
    public static WebSystemContext getRequiredWebSystemContext(@Nonnull ServletContext servletContext) {
        WebSystemContext context = getWebSystemContext(servletContext);
        if (context == null) {
            throw new IllegalStateException(RBMF.get("No WebSystemContext found"));
        }
        return context;
    }

    /**
     * Find the root {@link WebSystemContext} for this web app.<br>
     * Will rethrow an exception that happened on root context startup,
     * to differentiate between a failed context startup and no context at all.
     *
     * @param servletContext
     *     ServletContext to find the web application context for.
     * @return the root WebSystemContext for this web app, or {@code null} if none.
     */
    @Nullable
    public static WebSystemContext getWebSystemContext(@Nonnull ServletContext servletContext) {
        return getWebSystemContext(servletContext, ROOT_WEB_SYSTEM_CONTEXT_ATTRIBUTE);
    }

    /**
     * Find a custom {@link WebSystemContext} for this web app.
     *
     * @param servletContext
     *     ServletContext to find the web application context for.
     * @param attributeName
     *     the name of the ServletContext attribute to look for.
     * @return the desired WebSystemContext for this web app, or {@code null} if none.
     */
    @Nullable
    public static WebSystemContext getWebSystemContext(@Nonnull ServletContext servletContext, @Nonnull String attributeName) {
        Object attribute = servletContext.getAttribute(attributeName);
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof RuntimeException) {
            throw (RuntimeException) attribute;
        }
        if (attribute instanceof Error) {
            throw (Error) attribute;
        }
        if (attribute instanceof Exception) {
            throw new UncheckedException((Exception) attribute);
        }
        if (!(attribute instanceof WebSystemContext)) {
            throw new IllegalStateException(RBMF.format("Context attribute {0} is not of type WebSystemContext", attributeName));
        }
        return (WebSystemContext) attribute;
    }

    /**
     * Find a unique {@link WebSystemContext} for this web app: either the
     * root web app context (preferred) or a unique {@link WebSystemContext}
     * among the registered {@link ServletContext} attributes.
     *
     * @param servletContext
     *     ServletContext to find the web application context for.
     * @return the desired WebSystemContext for this web app, or {@code null} if none.
     * @see #getWebSystemContext(ServletContext)
     * @see ServletContext#getAttributeNames()
     */
    @Nullable
    public static WebSystemContext findWebSystemContext(@Nonnull ServletContext servletContext) {
        WebSystemContext context = getWebSystemContext(servletContext);
        if (context == null) {
            for (String attributeName : new EnumerationIterable<>(servletContext.getAttributeNames())) {
                Object attribute = servletContext.getAttribute(attributeName);
                if (!(attribute instanceof WebSystemContext)) {
                    continue;
                }

                if (context != null) {
                    throw new IllegalStateException(RBMF.get("Nonunique WebSystemContext found"));
                }
                context = (WebSystemContext) attribute;
            }
        }
        return context;
    }
}
