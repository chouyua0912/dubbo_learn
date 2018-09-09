package z.learn.providerdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 多个网卡的时候，dubbo无法正确调用。。
 * ifconfig virbr0 down
 * brctl delbr virbr0
 */
public class ProviderMain {

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:provider.xml"});
        context.start();
        System.out.println("ProviderMain started.");
        System.in.read(); // press any key to exit
    }
}
