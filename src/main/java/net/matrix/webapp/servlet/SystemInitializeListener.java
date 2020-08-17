/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.webapp.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.matrix.app.SystemController;
import net.matrix.app.message.CodedMessageDefinitionLoader;
import net.matrix.util.SLF4Js;
import net.matrix.webapp.DefaultWebSystemContext;
import net.matrix.webapp.WebSystemContext;
import net.matrix.webapp.WebSystemContexts;

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
     * 系统的 Servlet 上下文。
     */
    protected ServletContext servletContext;

    /**
     * 关联的系统环境。
     */
    protected WebSystemContext context;

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        servletContext = sce.getServletContext();

        LOG.info("{} 初始化开始", servletContext.getServletContextName());

        // JUL 配置
        SLF4Js.bridgeJUL();
        // 加载 jar 包中的消息定义
        CodedMessageDefinitionLoader.loadBuiltinDefinitions();

        // 初始化系统环境
        context = new DefaultWebSystemContext(servletContext);
        context.registerObject(ServletContext.class, servletContext);
        WebSystemContexts.setWebSystemContext(servletContext, context);

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

        LOG.info("{} 初始化完成", servletContext.getServletContextName());
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
    public void contextDestroyed(final ServletContextEvent sce) {
        context.getController().stop();
    }
}
