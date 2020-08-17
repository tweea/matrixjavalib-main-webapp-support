/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.webapp;

import javax.servlet.ServletContext;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * ResourceLoader implementation that resolves paths as ServletContext resources.
 */
public class WebSystemResourceLoader
    extends DefaultResourceLoader {
    private final ServletContext servletContext;

    /**
     * Create a new WebSystemResourceLoader.
     * 
     * @param servletContext
     *     the ServletContext to load resources with
     */
    public WebSystemResourceLoader(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected Resource getResourceByPath(final String path) {
        String realPath = servletContext.getRealPath(path);
        if (realPath == null) {
            return super.getResourceByPath(path);
        }
        return new FileSystemResource(realPath);
    }
}
