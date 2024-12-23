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

        users = userDAO.queryAllUsersByPage();

        for (User user : users) {
            System.out.println("user = " + user);
        }

        User user = new User(1, "testInterceptor");
        userDAO.update(user);

    }
}
