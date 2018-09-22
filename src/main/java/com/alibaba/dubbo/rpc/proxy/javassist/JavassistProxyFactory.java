/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.proxy.javassist;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * JavaassistRpcProxyFactory        Wrapper用来代理用户提供的实现类,实现方法调用Invocation的路由
 */
public class JavassistProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // TODO Wrapper cannot handle this scenario correctly: the classname contains '$'
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);  // 如果不是包装对象,则创建一个包装对象   Wrapper用来实现方法路由
        return new AbstractProxyInvoker<T>(proxy, type, url) {      // 创建Invoker对象,并实现doInvoke方法,代理到wrapper对象上进行路由到ProviderImpl实例的实际方法
            @Override
            protected Object doInvoke(T proxy, String methodName,   // proxy 是用户提供的服务实现实例   框架的作用是帮组实现可被远程调用
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);      // wrapper对象 是字节码生成的继承与Wrapper的类,会把方法代理到实现类对象的相应方法中
            }
        };
    }

}
/**
 * 被代理proxy 被 Wrapper 增强之后的方法,  实现了路由
 *
 * public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException{
 *     z.learn.providerdemo.Provider w;
 *     try{
 * 	    w = ((z.learn.providerdemo.Provider)$1);
 *     }catch(Throwable e){
 * 	    throw new IllegalArgumentException(e);
 *     }
 *
 *     try{
 *         if( "saySomething".equals( $2 )  &&  $3.length == 1 ) {
 * 	        return ($w)w.saySomething((java.lang.String)$4[0]);
 *         } if( "sayHello".equals( $2 )  &&  $3.length == 1 ) {
 *             return ($w)w.sayHello((java.lang.String)$4[0]);
 *         }
 *     } catch(Throwable e) {
 * 	        throw new java.lang.reflect.InvocationTargetException(e);
 *     }
 *     throw new com.alibaba.dubbo.common.bytecode.NoSuchMethodException("Not found method \""+$2+"\" in class z.learn.providerdemo.Provider.");
 * }
 *
 * AbstractProxyInvoker 实现了Invoker接口, 将方法定向到 doInvoke
 *     @Override
 *     public Result invoke(Invocation invocation) throws RpcException {       // Invocation 里面封装了请求的详情.  wrapper里面会根据方法名路由到bean实体,并调用相应方法
 *         try {
 *             return new RpcResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
 *         } catch (InvocationTargetException e) {
 *             return new RpcResult(e.getTargetException());
 *         } catch (Throwable e) {
 *             throw new RpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + " to " + getUrl() + ", cause: " + e.getMessage(), e);
 *         }
 *     }
 */
