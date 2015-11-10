/*
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.webapp.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.matrix.app.DefaultSystemController;
import net.matrix.app.GlobalSystemContext;
import net.matrix.app.SystemContext;
import net.matrix.app.SystemController;
import net.matrix.app.message.CodedMessageDefinitionLoader;

// TODO 模仿 WebApplicationContextUtils
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
	protected SystemContext context;

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		servletContext = sce.getServletContext();

		// 默认与全局系统环境关联
		context = GlobalSystemContext.get();

		// 加载消息
		loadMessageDefinitions();

		// 初始化系统环境
		context.registerObject(ServletContext.class, servletContext);
		setupResourceLoader();
		loadConfig();

		// 初始化控制器
		SystemController controller = getController();
		controller.setContext(context);
		context.setController(controller);
		controller.init();
		controller.start();

		LOG.info("{} 初始化完成", servletContext.getServletContextName());
	}

	/**
	 * 加载消息定义。
	 */
	protected void loadMessageDefinitions() {
		CodedMessageDefinitionLoader.loadDefinitions(context.getResourcePatternResolver());
	}

	/**
	 * 装配系统资源加载器。
	 */
	protected void setupResourceLoader() {
		// 空实现
	}

	/**
	 * 加载系统配置。
	 */
	protected void loadConfig() {
		// 空实现
	}

	/**
	 * 初始化系统控制器。
	 * 
	 * @return 系统控制器
	 */
	protected SystemController getController() {
		return new DefaultSystemController();
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		context.getController().stop();
	}
}
