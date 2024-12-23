package com.baizhiedu.plugins;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-20 19:03
 **/
@Intercepts(
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
)
public class MyMyBatisInterceptor3 extends MyMyBatisInterceptorAdapter {

    private String testValue;
    private static final Logger log = LoggerFactory.getLogger(MyMyBatisInterceptor3.class);

    /**
     * 作用：执行的拦截功能，书写在这个方法里，然后放行原有方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("--------------  interceptor  --------------");

        MetaObject metaObject = SystemMetaObject.forObject(invocation);
        String sql = (String) metaObject.getValue("target.delegate.boundSql.sql");
        String methonName = (String) metaObject.getValue("method.name");
        log.debug("sql: {}",sql);
        log.debug("methodName: {}",methonName);
        return invocation.proceed();
    }


    /**
     * 作用：获取拦截器相关的参数
     */
    @Override
    public void setProperties(Properties properties) {
    }
}
