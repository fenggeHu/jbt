package proxy;

import jbt.notify.Notify;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/
public class MyService extends Service {

    @Notify
    public String say(String msg) {
        return "Say: " + msg;
    }
    @Notify(enabled = false)
    public String hello(String guest) {
        return "Hello, " + guest;
    }

    public void name(String n) {
        this.name = n;
    }
}
