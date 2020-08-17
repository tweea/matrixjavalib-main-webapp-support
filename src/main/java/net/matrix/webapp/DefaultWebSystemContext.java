/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import net.matrix.app.DefaultSystemContext;
import net.matrix.app.DefaultSystemController;
import net.matrix.app.SystemController;

/**
 * 默认的 Web 系统环境。
 */
public class DefaultWebSystemContext
    extends DefaultSystemContext
    implements WebSystemContext {
    /**
     * 日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebSystemContext.class);

    /**
     * 系统配置位置的 Servlet 上下文参数名。
     */
    private static final String CONFIG_LOCATION_PARAM = "systemConfigLocation";

    /**
     * 系统控制器类名的 Servlet 上下文参数名。
     */
    private static final String CONTROLLER_CLASS_PARAM = "systemControllerClass";

    /**
     * Servlet 上下文。
     */
    protected final ServletContext servletContext;

    /**
     * 默认构造器。
     */
    public DefaultWebSystemContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        if (resourceLoader == null) {
            resourceLoader = new WebSystemResourceLoader(servletContext);
        }
        return resourceLoader;
    }

    @Override
    public Configuration getConfig() {
        if (config == null) {
            String configLocationsParam = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
            if (configLocationsParam == null) {
                configLocationsParam = "/WEB-INF/sysconfig.cfg,/WEB-INF/sysconfig.dev.cfg";
            }

            String[] configLocations = StringUtils.split(configLocationsParam, ",; \t\n");
            configLocations = StringUtils.stripAll(configLocations);

            List<AbstractConfiguration> configList = new ArrayList<>();
            for (String configLocation : configLocations) {
                if (StringUtils.isBlank(configLocation)) {
                    continue;
                }
                Resource configResource = getResourceLoader().getResource(configLocation);
                if (!configResource.exists()) {
                    LOG.info("系统配置文件 {} 不存在", configResource);
                    continue;
                }
                try {
                    configList.add(new PropertiesConfiguration(configResource.getURL()));
                    LOG.info("系统配置文件 {} 加载完成", configResource);
                } catch (IOException e) {
                    throw new ConfigurationRuntimeException("系统配置文件 " + configResource + " 加载失败", e);
                } catch (ConfigurationException e) {
                    throw new ConfigurationRuntimeException("系统配置文件 " + configResource + " 加载失败", e);
                }
            }
            if (configList.isEmpty()) {
                LOG.info("系统配置文件未找到");
                config = new PropertiesConfiguration();
            } else if (configList.size() == 1) {
                config = configList.get(0);
            } else {
                CombinedConfiguration combinedConfig = new CombinedConfiguration(new OverrideCombiner());
                for (int index = configList.size() - 1; index >= 0; index--) {
                    combinedConfig.addConfiguration(configList.get(index));
                }
                config = combinedConfig;
            }
        }
        return config;
    }

    @Override
    public SystemController getController() {
        if (controller == null) {
            String controllerClassParam = servletContext.getInitParameter(CONTROLLER_CLASS_PARAM);
            if (controllerClassParam == null) {
                controller = new DefaultSystemController();
            } else {
                try {
                    Class<?> controllerClass = ClassUtils.getClass(controllerClassParam);
                    controller = (SystemController) controllerClass.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new ConfigurationRuntimeException("控制器类 " + controllerClassParam + " 实例化失败", e);
                }
            }
            controller.setContext(this);
        }
        return controller;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
