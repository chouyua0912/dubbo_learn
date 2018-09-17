package z.learn.providerdemo;

import java.util.Date;

public class ProviderImpl implements Provider {

    public String sayHello(String name) {
        System.out.println("name is " + name);
        return "name is " + name;
    }

    @Override
    public String saySomething(String msg) {
        String p = "Formatted.. " + new Date() + " " + msg;
        System.out.println(p);
        return p;
    }
}
