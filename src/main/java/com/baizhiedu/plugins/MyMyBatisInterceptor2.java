package com.baizhiedu.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-20 19:03
 **/
@Intercepts(
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
)
public class MyMyBatisInterceptor2 extends MyMyBatisInterceptorAdapter {

    private String testValue;
    private static final Logger log = LoggerFactory.getLogger(MyMyBatisInterceptor2.class);

    /**
     * 作用：执行的拦截功能，书写在这个方法里，然后放行原有方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("--------------拦截器中的  interceptor  方法执行--------------");
        // RoutingStatementHandler  -->  delegate --> 即 PreparedStatementHandler
        // RoutingStatementHandler target = (RoutingStatementHandler) invocation.getTarget();
        // BoundSql boundSql = target.getBoundSql();
        // String sql = boundSql.getSql();

        MetaObject metaObject = SystemMetaObject.forObject(invocation);
        String sql = (String) metaObject.getValue("target.delegate.boundSql.sql");
        log.debug("sql: {}",sql);
        return invocation.proceed();
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
