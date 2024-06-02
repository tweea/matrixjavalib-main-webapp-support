/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

import static org.assertj.core.api.Assertions.assertThat;

public class WebSystemResourceLoaderTest {
    @Test
    public void testGetResourceByPath() {
        MockServletContext context = new MockServletContext();
        WebSystemResourceLoader resourceLoader = new WebSystemResourceLoader(context);

        assertThat(resourceLoader.getResourceByPath("/WEB-INF/sysconfig.cfg")).isNotNull();
    }
}
