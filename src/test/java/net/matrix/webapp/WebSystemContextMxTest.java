/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

import static org.assertj.core.api.Assertions.assertThat;

class WebSystemContextMxTest {
    @Test
    void testSetWebSystemContext() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        WebSystemContextMx.setWebSystemContext(servletContext, context);
        assertThat(servletContext.getAttribute(WebSystemContext.class.getName() + ".ROOT")).isSameAs(context);
    }

    @Test
    void testSetWebSystemContext_attributeName() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        WebSystemContextMx.setWebSystemContext(servletContext, "test", context);
        assertThat(servletContext.getAttribute("test")).isSameAs(context);
    }

    @Test
    void testGetRequiredWebSystemContext() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);
        WebSystemContextMx.setWebSystemContext(servletContext, context);

        assertThat(WebSystemContextMx.getRequiredWebSystemContext(servletContext)).isSameAs(context);
    }

    @Test
    void testGetWebSystemContext() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);
        WebSystemContextMx.setWebSystemContext(servletContext, context);

        assertThat(WebSystemContextMx.getWebSystemContext(servletContext)).isSameAs(context);
    }

    @Test
    void testGetWebSystemContext_attributeName() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);
        WebSystemContextMx.setWebSystemContext(servletContext, "test", context);

        assertThat(WebSystemContextMx.getWebSystemContext(servletContext, "test")).isSameAs(context);
    }

    @Test
    void testFindWebSystemContext() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);
        WebSystemContextMx.setWebSystemContext(servletContext, "test", context);

        assertThat(WebSystemContextMx.findWebSystemContext(servletContext)).isSameAs(context);
    }
}
