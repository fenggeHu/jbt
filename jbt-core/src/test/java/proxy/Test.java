package proxy;

/**
 * @author jinfeng.hu  @date 2024-04-13
 **/import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;

class Calculator {
    public int calculate(int a, int b) {
        System.out.println("Original calculation: " + a + " + " + b);
        return a + b;
    }
}

class CalculatorProxyHandler implements MethodHandler {
    private Calculator originalCalculator;

    public CalculatorProxyHandler(Calculator originalCalculator) {
        this.originalCalculator = originalCalculator;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        // 在调用原始方法之前记录日志
        System.out.println("Before method execution...");

        // 调用原始方法，并获取结果
        Object result = proceed.invoke(originalCalculator, args);

        // 在调用原始方法之后处理其他逻辑
        System.out.println("After method execution...");

        // 返回原始方法的结果
        return result;
    }
}

public class Test {
    public static void main(String[] args) {
        // 创建原始对象
        Calculator originalCalculator = new Calculator();

        // 创建代理工厂
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(Calculator.class);

        try {
            // 实例化代理对象
            Calculator proxyCalculator = (Calculator) factory.create(new Class<?>[0], new Object[0], new CalculatorProxyHandler(originalCalculator));

            // 调用代理对象的方法
            int result = proxyCalculator.calculate(10, 5); // 输出结果：Before method execution...Original calculation: 10 + 5 After method execution...
            System.out.println("Result: " + result); // 输出结果：Result: 15
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
