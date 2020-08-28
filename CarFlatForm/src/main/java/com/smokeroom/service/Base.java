package com.smokeroom.service;
 

import org.springframework.context.ApplicationContext;

/**变更：2020-04-15将其更改为interface。也就是利用java 8对接口支持静态方法的特性。
 * 这样实用类。
 * 原来：是一个lcass。
 * @author Administrator
 *
 */
public interface Base {
	//static ApplicationContext cxt = null;//cxt实际上是final无法再次赋值。所以只能包一层。
	static final ApplicationContextHolder holder = new ApplicationContextHolder();
	public static void setApplicationContext(ApplicationContext context) {
		holder.setContext(context);
	}
	public static <T> T getBean( Class<T> clas) {
		return holder.getContext().getBean(clas);
	}
	static class ApplicationContextHolder{
		private ApplicationContext context;

		
		public void setContext(ApplicationContext context) {
			this.context = context;
		}


		public ApplicationContext getContext() {
			return context;
		}
 
	}

}
