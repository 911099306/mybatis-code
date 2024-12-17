package com.baizhiedu;

import com.baizhiedu.dao.Cache;
import com.baizhiedu.dao.ProductDAO;
import com.baizhiedu.dao.ProductDAOImpl;
import com.baizhiedu.dao.UserDAO;
import com.baizhiedu.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-15 18:25
 **/
public class testMybatis3 {

    /**
     * 测试：一级缓存
     */
    @Test
    public void test() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        List<User> users = userDAO.queryAllUsers();

        for (User user : users) {
            System.out.println("user = " + user);
        }
        System.out.println("=================================================");
        System.out.println("second query...");

        users = userDAO.queryAllUsers();

        for (User user : users) {
            System.out.println("user = " + user);
        }

    }

    @Test
    public void test2() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();

        UserDAO userDAO1 = sqlSession1.getMapper(UserDAO.class);


        List<User> users = userDAO1.queryAllUsers();

        for (User user : users) {
            System.out.println("user = " + user);
        }

        System.out.println("=================================================");
        System.out.println("second query...");

        UserDAO userDAO2 = sqlSession2.getMapper(UserDAO.class);
        List<User> users1 = userDAO2.queryAllUsers();

        for (User user : users1) {
            System.out.println("user = " + user);
        }

    }
}
