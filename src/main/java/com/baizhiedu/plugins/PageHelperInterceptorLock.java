package com.baizhiedu.plugins;

import com.baizhiedu.util.Page;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-21 15:03
 **/
@Intercepts(
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
)
public class PageHelperInterceptorLock extends MyMyBatisInterceptorAdapter{

    private static final Logger log = LoggerFactory.getLogger(PageHelperInterceptorLock.class);


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        log.info("----------PageHelperInterceptorLock------------");

        // 获得 SQL 语句，拼接  比较 version
        MetaObject metaObject = SystemMetaObject.forObject(invocation);
        String sql = (String) metaObject.getValue("target.delegate.boundSql.sql");
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("target.delegate.mappedStatement");

        // 判断是否 id 以 query 开头
        String id = mappedStatement.getId();

        /*
         * 保存操作， version = 0
         *
         * 现有的sql： insert into t_user (name) values (#{name});
         * 需要实现的： insert into t_user (name, version) values (#{name}, #{version});
         *
         * 如何实现获取现有的sql：
         *      metaObject.getValue("target.delegate.boundSql.sql");
         * 如何对sql进行改造？
         *      拆串解串  jsqlparser
         *
         */
        if (id.contains("save")) {
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Insert insert = (Insert) parserManager.parse(new StringReader(sql));

            // 列名
            List<Column> columns = insert.getColumns();
            columns.add(new Column("version"));
            insert.setColumns(columns);

            // 列值
            // 插入的列 version  对应的默认值 0
            ExpressionList itemsList = (ExpressionList) insert.getItemsList();
            List<Expression> expressions = itemsList.getExpressions();
            expressions.add(new LongValue(0));
            insert.setSetExpressionList(expressions);

            // 将mybatis 执行的sql语句替换成新的
            metaObject.setValue("target.delegate.boundSql.sql", insert.toString());
        }

        /**
         * sql = update xxx set xxx=xxx , version = version + 1 where id = ?
         * 更新操作，比较 version 与数据库中的version 是否相同
         *
         * 不相同，存在并发，当前线程的数据不是最新的数据，不可以进行更新了
         * 相同，进行更新操作的同时 将 version + 1
         */
        if (id.contains("update")) {

            // jsqlparser 解析sql 获得表明以及vers 属性
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Update update = (Update) parserManager.parse(new StringReader(sql));
            // 操作的表明
            Table table = update.getTable();
            String tableNam = table.getName();

            // 更新的记录id
            Integer rowId = (Integer) metaObject.getValue("target.delegate.parameterHandler.parameterObject.id");
            Integer version = (Integer) metaObject.getValue("target.delegate.parameterHandler.parameterObject.version");


            Connection conn = (Connection) invocation.getArgs()[0];

            String selectSql = "select version from " + tableNam + " where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(selectSql);

            preparedStatement.setInt(1, rowId);

            ResultSet resultSet = preparedStatement.executeQuery();

            int vers = 0;

            if (resultSet.next()) {
                vers = resultSet.getInt(1);
            }



            // mybatis 进行更新的时候，User对象参数传入， ---> version 上一步的vers 进行比较

            // 不同，抛出异常， 更新失败
            if (vers != version) {
                throw new RuntimeException("版本不一致");
            } else {
                // 相同 version + 1 ， 进行数据库更新
                // 列名
                List<Column> columns = update.getColumns();
                columns.add(new Column("version"));
                update.setColumns(columns);

                // 列值
                // 更新的列 version + 1
                List<Expression> expressions = update.getExpressions();
                expressions.add(new LongValue(version + 1));
                update.setExpressions(expressions);

                metaObject.setValue("target.delegate.boundSql.sql", update.toString());

            }
        }


        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
