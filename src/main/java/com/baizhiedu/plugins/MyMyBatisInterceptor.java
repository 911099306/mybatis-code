package com.baizhiedu.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-20 19:03
 **/
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,RowBounds.class, ResultHandler.class})

        }
)
public class MyMyBatisInterceptor implements Interceptor {

    private String testValue;
    private static final Logger log = LoggerFactory.getLogger(MyMyBatisInterceptor.class);

    /**
     * 作用：执行的拦截功能，书写在这个方法里，然后放行原有方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("--------------拦截器中的  interceptor  方法执行--------------");
        log.debug("testValue: {}",testValue);
        return invocation.proceed();
    }

    /**
     * 作用：将这个拦截器 传递给 下一个拦截器
     */
    @Override
    public Object plugin(Object target) {
        // 几乎不需要变动，这么写死就可以
        return Plugin.wrap(target, this);
    }

    /**
     * 作用：获取拦截器相关的参数
     */
    @Override
    public void setProperties(Properties properties) {
        System.out.println("properties = " + properties);
        this.testValue = properties.getProperty("testProperty");

    }
}
