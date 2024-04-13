package proxy;

import jbt.notify.Notify;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
public class Service {
    protected String name;

    public String say(String msg) {
        return "Say: " + msg;
    }

    @Notify
    public String hello(String guest) {
        return null;
    }

    @Notify
    public void name(String n) {
        this.name = n;
    }

    public Object age(String name) {
        return name.length();
    }
}
