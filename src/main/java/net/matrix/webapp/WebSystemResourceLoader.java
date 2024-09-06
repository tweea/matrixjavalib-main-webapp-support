/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import javax.annotation.Nonnull;

import jakarta.servlet.ServletContext;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * ResourceLoader implementation that resolves paths as ServletContext resources.
 */
public class WebSystemResourceLoader
    extends DefaultResourceLoader {
    @Nonnull
    private final ServletContext servletContext;

    /**
     * Create a new WebSystemResourceLoader.
     *
     * @param servletContext
     *     the ServletContext to load resources with.
     */
    public WebSystemResourceLoader(@Nonnull ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        String realPath = servletContext.getRealPath(path);
        if (realPath == null) {
            return super.getResourceByPath(path);
        }
        return new FileSystemResource(realPath);
    }
}
