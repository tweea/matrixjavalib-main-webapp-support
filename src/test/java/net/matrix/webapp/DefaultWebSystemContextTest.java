/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

import net.matrix.app.DefaultSystemController;
import net.matrix.app.SystemController;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultWebSystemContextTest {
    @Test
    void testGetResourceLoader() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        assertThat(context.getResourceLoader()).isNotNull();
    }

    @Test
    void testGetConfig() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        Configuration config = context.getConfig();
        assertThat(config).isNotNull();
        assertThat(config.getString("test")).isEqualTo("a");
        assertThat(config.getString("xyz")).isEqualTo("1");
    }

    @Test
    void testGetConfig2() {
        MockServletContext servletContext = new MockServletContext();
        servletContext.setInitParameter("systemConfigLocation", "/WEB-INF/sysconfig.cfg,/WEB-INF/sysconfig2.cfg");
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        Configuration config = context.getConfig();
        assertThat(config).isNotNull();
        assertThat(config.getString("test")).isEqualTo("b");
        assertThat(config.getString("xyz")).isEqualTo("1");
    }

    @Test
    void testGetController() {
        MockServletContext servletContext = new MockServletContext();
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        SystemController controller = context.getController();
        assertThat(controller).isInstanceOf(DefaultSystemController.class);
        assertThat(controller.getContext()).isSameAs(context);
    }

    @Test
    void testGetController2() {
        MockServletContext servletContext = new MockServletContext();
        servletContext.setInitParameter("systemControllerClass", TestController.class.getName());
        DefaultWebSystemContext context = new DefaultWebSystemContext(servletContext);

        SystemController controller = context.getController();
        assertThat(controller).isInstanceOf(TestController.class);
        assertThat(controller.getContext()).isSameAs(context);
    }

    static class TestController
        extends DefaultSystemController {
    }
}
