package com.baizhiedu.plugins;

import com.baizhiedu.util.Page;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-21 15:03
 **/
@Intercepts(
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
)
public class PageHelperInterceptor1 extends MyMyBatisInterceptorAdapter{

    private static final Logger log = LoggerFactory.getLogger(PageHelperInterceptor1.class);

    private String queryMethodPrefix;
    private String queryMethodSuffix;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        log.info("----------PageHelperInterceptor1------------");

        // 获得 SQL 语句，拼接 limit
        MetaObject metaObject = SystemMetaObject.forObject(invocation);
        String sql = (String) metaObject.getValue("target.delegate.boundSql.sql");
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("target.delegate.mappedStatement");

        // 判断是否 id 以 query 开头
        String id = mappedStatement.getId();
        log.info("queryMethodPrefix:{}, queryMethodSuffix:{}",queryMethodPrefix, queryMethodSuffix);

        if (id.contains(queryMethodPrefix) && id.endsWith(queryMethodSuffix)) {

            // 获得page对象，并设置Page对象 totalSize 属性，并算出总页数

            Page page =  (Page) metaObject.getValue("target.delegate.boundSql.parameterObject");


            // 对查询条件下的数据量进行统计所有
            String countSql = "select count(*) " + sql.substring(sql.indexOf("from"));
            // JDBC 进行查询操作，这里已经到mybatis底层了
            // 1. Connection  PreparedStatement
            Connection conn = (Connection) invocation.getArgs()[0];
            PreparedStatement preparedStatement = conn.prepareStatement(countSql);

           /*
                如果存在查询条件 where id = ？  需要将参数替换为实际的值， MyBatis 给了解决方案，如下ParameterHandler处理
            preparedStatement.setString(1,?)
            preparedStatement.setString(2,?);*/
            ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("target.delegate.parameterHandler");
            parameterHandler.setParameters(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                page.setTotalSize(resultSet.getInt(1));
            }

            // 查询分页数据
            String newSql = sql + " limit " + page.getFirstItem() * page.getPageSize() + " , " + page.getPageCount();
            metaObject.setValue("target.delegate.boundSql.sql", newSql);
        }

        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        this.queryMethodSuffix = properties.getProperty("queryMethodsuffix");
        this.queryMethodPrefix = properties.getProperty("queryMethodPrefix");
    }
}
