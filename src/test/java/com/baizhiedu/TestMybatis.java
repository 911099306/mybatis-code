package com.baizhiedu;

import com.baizhiedu.dao.UserDAO;
import com.baizhiedu.entity.User;
import com.baizhiedu.proxy.MyMapperProxy;
import org.apache.ibatis.io.Resources;

import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Serendipity
 * @description 测试mybatis文件
 * @date 2024-12-11 23:07
 **/
public class TestMybatis {

    /**
     * 测试基本的mybatis开发步骤：6大步骤
     */
    @Test
    public void test1() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        List<User> users = userDAO.queryAllUsers();

        for (User user : users) {
            System.out.println("user = " + user);
        }
    }


    /**
     * 测试 mybatis SQL Session 的第二种用法
     */
    @Test
    public void test2() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 传入调用的sql 在xml文件中的 id
        List<Object> users = sqlSession.selectList("com.baizhiedu.dao.UserDAO.queryAllUsers");
        // sqlSession.insert()
        // sqlSession.update()
        // sqlSession.delete();
        for (Object user : users) {
            System.out.println("user = " + (User) user);
        }

    }



    @Test
    public void testJDBC() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Drvier");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/suns?useSSL=false", "root", "123456");
        Statement statement = conn.createStatement();
        String sql = "";
        statement.execute(sql);
    }


    /**
     * 测试 mybatis SQL Session 的第二种用法
     */
    @Test
    public void test3() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        sqlSession.insert("");
        // sqlSession.update()
        // sqlSession.delete();
        // for (Object user : users) {
        //     System.out.println("user = " + (User) user);
        // }

    }



    @Test
    public void testProxy() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDAO userDAO = (UserDAO) Proxy.newProxyInstance(
                TestMybatis.class.getClassLoader(),
                new Class[]{UserDAO.class},
                new MyMapperProxy(sqlSession, UserDAO.class));

        List<User> users = userDAO.queryAllUsers();

        for (User user : users) {
            System.out.println("user = " + user);
        }


    }

    @Test
    public void testXMLParser() throws IOException {
        // Reader reader = Resources.getResourceAsReader("users.xml");  两种相同的写法
        InputStream reader = Resources.getResourceAsStream("users.xml");

        XPathParser xPathParser = new XPathParser(reader);
        List<XNode> xNodes = xPathParser.evalNodes("/users/*");

        System.out.println("xNodes.size : " + xNodes.size());

        List<com.baizhiedu.xml.User> users = new LinkedList<>();
        for (XNode xNode : xNodes) {
            // name 、 password 封装的xNode
            List<XNode> children = xNode.getChildren();

            com.baizhiedu.xml.User user = new com.baizhiedu.xml.User();
            user.setName(children.get(0).getStringBody());
            user.setPassword(children.get(1).getStringBody());
            users.add(user);
        }
        System.out.println(users);
    }
}
