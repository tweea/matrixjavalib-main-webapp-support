/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp.servlet;

import javax.servlet.ServletContextEvent;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemInitializeListenerTest {
    @Test
    public void testContextInitialized() {
        MockServletContext servletContext = new MockServletContext();
        SystemInitializeListener listener = new SystemInitializeListener();

        listener.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(listener.servletContext).isNotNull();
        assertThat(listener.context).isNotNull();
    }
}
