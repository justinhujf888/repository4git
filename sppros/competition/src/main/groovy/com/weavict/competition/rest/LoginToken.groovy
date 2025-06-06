package com.weavict.competition.rest

import com.weavict.competition.redis.RedisUtil
import org.aopalliance.intercept.ConstructorInterceptor
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.glassfish.hk2.api.Descriptor
import org.glassfish.hk2.api.Filter
import org.glassfish.hk2.api.InterceptionService
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import jakarta.ws.rs.core.Feature
import jakarta.ws.rs.core.FeatureContext
import java.lang.annotation.*
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Created by Justin on 2018/6/10.
 */
@Documented
@Target([ElementType.METHOD,ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface LoginToken {
}

@LoginToken
class LoginTokenMethodInterceptor implements MethodInterceptor
{
    static RedisUtil redisUtil;

    @Override
    Object invoke(MethodInvocation methodInvocation) throws Throwable
    {
        try
        {
            if (redisUtil==null)
            {
                ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext_redis.xml");
                redisUtil = ctx.getBean("redisUtil");
            }
            Object[] args = methodInvocation.getArguments();
//            println args[0];
            if(!args[0].loginState.hasLogin
                    || !args[0].loginState.loginToken.equals(redisUtil.hGet("buyer_${args[0].loginState.openid}","token")))
            {
                args[0].loginState.loginToken = "NULL";
                args[0].loginState.hasLogin = false;
                return methodInvocation.proceed();
            }
            else
            {
                args[0].loginState.loginToken = "HAS";
                args[0].loginState.hasLogin = true;
                return methodInvocation.proceed();
            }
        }
        catch (e)
        {
            e.printStackTrace();
            return null;
        }

    }
}

class JerseyInterceptor implements InterceptionService
{
    private static Map<Annotation, MethodInterceptor> map = new HashMap<>();
    static{
        Annotation[] annotations = LoginTokenMethodInterceptor.class.getAnnotations();
        for(Annotation annotation : annotations){
            map.put(annotation, new LoginTokenMethodInterceptor());
        }
    }
    @Override
    Filter getDescriptorFilter() {
        return ({
            new Filter() {
                boolean matches(Descriptor descriptor)
                {
                    return true;
                }
            }
        }).call();
    }
    @Override
    List<MethodInterceptor> getMethodInterceptors(Method method) {
        Annotation[] annotations = method.getAnnotations();
        List<MethodInterceptor> list = new ArrayList<>();
        for (Annotation annotation :annotations){
            if(map.get(annotation) != null){
                list.add(map.get(annotation));
            }
        }
        return list;
    }
    @Override
    List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}

class JerseyBinding extends AbstractBinder {
    @Override
    protected void configure() {
        this.bind(JerseyInterceptor.class).to(InterceptionService.class).in(Singleton.class);
    }
}

class JerseyFeature implements Feature
{
    @Override
    boolean configure(FeatureContext context) {
        context.register(new JerseyBinding());
        return true;
    }
}