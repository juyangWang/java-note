package com.fuyao.example.handle;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.fuyao.example.annotation.DataSource;
import com.fuyao.example.datasource.DynamicDataSourceHolder;

public class DataSourceAspect {
	
	 public void before(JoinPoint point) throws InstantiationException, IllegalAccessException
	    {
	        Object target = point.getTarget();
	        String method = point.getSignature().getName();

	        //Class<?>[] classz = target.getClass().getInterfaces();
	        Class classz = target.getClass();
	        
	        //Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
	        try {
	            Method m = classz.getMethod(method);
	        	//Method m = ((MethodSignature) point.getSignature()).getMethod();
	            if (m != null && m.isAnnotationPresent(DataSource.class)) {
	                DataSource data = m
	                        .getAnnotation(DataSource.class);
	                DynamicDataSourceHolder.putDataSource(data.value());
	                System.out.println(data.value());
	            }
	            
	        } catch (Exception e) {
	            // TODO: handle exception
	        }
	    }

}
