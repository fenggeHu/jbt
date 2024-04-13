package proxy;

import jbt.notify.NotifyProxy;
import lombok.SneakyThrows;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        // 用子类
        System.out.println("============代理子类：");
        MyService myService = NotifyProxy.get(MyService.class);
        // 调用代理对象的方法
        myService.say("hello world");
        myService.hello("Jack");
        myService.name("Max");
        myService.age("hjkadf");

        // 用基类
        System.out.println("============代理父类：");
        MyService ms2 = new MyService();
        Service myService2 = NotifyProxy.get(Service.class, ms2);
        // 调用代理对象的方法
        myService2.say("hello world");
        myService2.hello("Jack");
        myService2.name("Max");
        myService2.age("hjkadf");

    }
}
