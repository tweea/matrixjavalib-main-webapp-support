/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.matrix.app.SystemController;
import net.matrix.app.message.CodedMessageDefinitionLoader;
import net.matrix.org.slf4j.SLF4Jmx;
import net.matrix.text.ResourceBundleMessageFormatter;
import net.matrix.webapp.DefaultWebSystemContext;
import net.matrix.webapp.WebSystemContext;
import net.matrix.webapp.WebSystemContextMx;

/**
 * 系统初始化监听器，注册在 web.xml 中被容器调用初始化、启动和停止。
 */
public class SystemInitializeListener
    implements ServletContextListener {
    /**
     * 日志记录器。
     */
    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializeListener.class);

    /**
     * 区域相关资源。
     */
    private static final ResourceBundleMessageFormatter RBMF = new ResourceBundleMessageFormatter(SystemInitializeListener.class).useCurrentLocale();

    /**
     * Servlet 上下文。
     */
    protected ServletContext servletContext;

    /**
     * 系统环境。
     */
    protected WebSystemContext context;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();

        LOG.info(RBMF.get("系统环境 {} 初始化开始"), servletContext.getServletContextName());

        // JUL 配置
        SLF4Jmx.bridgeJUL();
        // 加载 jar 包中的消息定义
        CodedMessageDefinitionLoader.loadBuiltinDefinitions();

        // 初始化系统环境
        context = new DefaultWebSystemContext(servletContext);
        context.registerObject(ServletContext.class, servletContext);
        WebSystemContextMx.setWebSystemContext(servletContext, context);

        // 配置资源加载
        setupResourceLoader();
        // 加载消息定义
        loadMessageDefinitions();
        // 加载配置
        loadConfig();

        // 初始化控制器
        SystemController controller = context.getController();
        controller.init();
        controller.start();

        LOG.info(RBMF.get("系统环境 {} 初始化完成"), servletContext.getServletContextName());
    }

    /**
     * 装配系统资源加载器。
     */
    protected void setupResourceLoader() {
        // 空实现
    }

    /**
     * 加载消息定义。
     */
    protected void loadMessageDefinitions() {
        // 空实现
    }

    /**
     * 加载系统配置。
     */
    protected void loadConfig() {
        // 空实现
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        context.getController().stop();
    }
}
