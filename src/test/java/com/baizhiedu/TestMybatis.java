package com.baizhiedu;

import com.baizhiedu.dao.UserDAO;
import com.baizhiedu.entity.User;
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


}
