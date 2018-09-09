package z.learn.providerdemo;

public class ProviderImpl implements Provider {

    public String sayHello(String name) {
        System.out.println("name is " + name);
        return "name is " + name;
    }
}
