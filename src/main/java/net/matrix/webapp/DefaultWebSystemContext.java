/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import jakarta.servlet.ServletContext;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import net.matrix.app.DefaultSystemContext;
import net.matrix.app.DefaultSystemController;
import net.matrix.app.SystemController;
import net.matrix.java.lang.reflect.ReflectionMx;
import net.matrix.text.ResourceBundleMessageFormatter;

/**
 * 默认的基于 Web 的系统环境。
 */
public class DefaultWebSystemContext
    extends DefaultSystemContext
    implements WebSystemContext {
    /**
     * 日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebSystemContext.class);

    /**
     * 区域相关资源。
     */
    private static final ResourceBundleMessageFormatter RBMF = new ResourceBundleMessageFormatter(DefaultWebSystemContext.class).useCurrentLocale();

    /**
     * 系统配置位置的 Servlet 上下文参数名。
     */
    private static final String CONFIG_LOCATION_PARAM = "systemConfigLocation";

    /**
     * 默认的系统配置位置。
     */
    private static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/sysconfig.cfg,/WEB-INF/sysconfig.dev.cfg";

    /**
     * 系统控制器类名的 Servlet 上下文参数名。
     */
    private static final String CONTROLLER_CLASS_PARAM = "systemControllerClass";

    /**
     * Servlet 上下文。
     */
    @Nonnull
    protected final ServletContext servletContext;

    /**
     * 构造器。
     */
    public DefaultWebSystemContext(@Nonnull ServletContext servletContext) {
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
            String configLocationsParam = StringUtils.defaultIfBlank(servletContext.getInitParameter(CONFIG_LOCATION_PARAM), DEFAULT_CONFIG_LOCATION);
            String[] configLocations = StringUtils.split(configLocationsParam, ",; \t\n");
            configLocations = StringUtils.stripAll(configLocations);

            List<AbstractConfiguration> configList = new ArrayList<>();
            for (String configLocation : configLocations) {
                if (StringUtils.isBlank(configLocation)) {
                    continue;
                }

                Resource configResource = getResourceLoader().getResource(configLocation);
                if (!configResource.exists()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(RBMF.get("未找到系统配置文件 {}"), configResource);
                    }
                    continue;
                }

                try {
                    PropertiesConfiguration memberConfig = new PropertiesConfiguration();
                    FileHandler fileHandler = new FileHandler(memberConfig);
                    fileHandler.load(configResource.getInputStream());
                    configList.add(memberConfig);
                    LOG.info(RBMF.get("系统配置文件 {} 加载完成"), configResource);
                } catch (IOException | ConfigurationException e) {
                    throw new ConfigurationRuntimeException(RBMF.format("系统配置文件 {0} 加载失败", configResource), e);
                }
            }
            if (configList.isEmpty()) {
                LOG.info(RBMF.get("未加载系统配置文件"));
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
                    Constructor<?> controllerConstructor = controllerClass.getDeclaredConstructor();
                    ReflectionMx.makeAccessible(controllerConstructor);
                    controller = (SystemController) controllerConstructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new ConfigurationRuntimeException(RBMF.format("控制器类 {0} 实例化失败", controllerClassParam), e);
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
