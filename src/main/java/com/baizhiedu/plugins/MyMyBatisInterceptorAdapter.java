package com.baizhiedu.plugins;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-21 10:46
 **/
public abstract class MyMyBatisInterceptorAdapter implements Interceptor {
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
