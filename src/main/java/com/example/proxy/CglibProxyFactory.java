package com.example.proxy;

import net.sf.cglib.proxy.Enhancer;

public class CglibProxyFactory {
    public static Object getProxy(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader());
        enhancer.setSuperclass(clazz);

        return enhancer.create();
    }

    public static void main(String[] args) {
        AliSendMessageService sendMessageService= (AliSendMessageService)CglibProxyFactory.getProxy(AliSendMessageService.class);
        sendMessageService.send();
    }
}
