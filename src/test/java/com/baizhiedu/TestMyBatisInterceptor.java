package com.baizhiedu;

import com.baizhiedu.dao.UserDAO;
import com.baizhiedu.entity.User;
import com.baizhiedu.util.Page;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-20 19:28
 **/
public class TestMyBatisInterceptor {

    @Test
    public void testInterceptor() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        List<User> users = userDAO.queryAllUsers();


        for (User user : users) {
            System.out.println("user = " + user);
        }

        // users = userDAO.queryAllUsersByPage();

        for (User user : users) {
            System.out.println("user = " + user);
        }

        User user = new User(1, "testInterceptor");
        userDAO.update(user);

    }


    @Test
    public void testInterceptor2() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        Page page = new Page(2);
        List<User> users = userDAO.queryAllUsersByPage(page);


        for (User user : users) {
            System.out.println("user = " + user);
        }
    }


    @Test
    public void testThreadLocal() {
        ThreadLocal<String> stringThreadLocal = new ThreadLocal<>();
        stringThreadLocal.set("gao");


        Thread thread = Thread.currentThread();

        System.out.println("=-=-=");
    }


    @Test
    public void testLock() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        User user = new User(1, "update", 1);

        userDAO.update(user);

        System.out.println("=============================");

        List<User> users = userDAO.queryAllUsers();
        for (User user1 : users) {
            System.out.println("user1 = " + user1);
        }

        sqlSession.commit();
    }


    @Test
    public void testLockSave() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        User user = new User();
        user.setName("xiaowangba");
        userDAO.save(user);

        System.out.println("=============================");

        List<User> users = userDAO.queryAllUsers();
        for (User user1 : users) {
            System.out.println("user1 = " + user1);
        }

        sqlSession.commit();
    }
}
