package com.baizhiedu;

import com.baizhiedu.dao.Cache;
import com.baizhiedu.dao.ProductDAO;
import com.baizhiedu.dao.ProductDAOImpl;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-15 18:25
 **/
public class testMybatis2 {

    /**
     * 测试：创建DAO接口的代理
     */
    @Test
    public void test() {

        ProductDAO productDAO = new ProductDAOImpl();
        ProductDAO productDAOProxy =  (ProductDAO) Proxy.newProxyInstance(testMybatis2.class.getClassLoader(), new Class[]{ProductDAO.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 方法仅针对 query 类的方法进行缓存处理，否则，直接运行
                // if (method.getName().startsWith("query")) {
                //     System.out.println("连接redis, 查询数据是否存在，若存在则直接返回，return data ");
                //     return method.invoke(productDAO, args);
                // }
                Cache cache = method.getAnnotation(Cache.class);
                if (cache != null) {
                    String eviction = cache.eviction();
                    System.out.println("eviction = " + eviction);
                    System.out.println("连接redis, 查询数据是否存在，若存在则直接返回，return data ");
                        return method.invoke(productDAO, args);
                }

                // 非查询类的方法，直接运行，无须缓存操作
                return method.invoke(productDAO, args);
            }
        });
        productDAOProxy.save();
        System.out.println("------------------------------");
        productDAOProxy.queryProductById(10);
        System.out.println("------------------------------");
        productDAOProxy.queryAllProducts();
    }
}
