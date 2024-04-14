package net.marcellperger.first;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

// TODO parser

/**
 * TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
 * click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
 */
public class Main {
    public static void main(final String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
//        List<String> lines = new ArrayList<>();
//        Scanner sc = new Scanner(System.in);
//        while (true) {
//            String line = sc.nextLine().strip();
//            if(line.equalsIgnoreCase("exit")) break;
//            if(line.isEmpty()) continue;
//            lines.add(line);
//        }
//        var xy = String.class.getMethod("equals", Object.class);
//        var z = xy.invoke(2, "we");
//        System.out.println(z);
        // (0.2 + 8.1) * (2.7 * -2.1 + 0.1) + (7.9 + -2.3) + ((9.9 + 2.7 * -2.1) + 2.1)
        MathSymbol sym = new AddOperation(
            new MulOperation(
                new AddOperation(
                    new BasicDoubleSymbol(0.2),
                    new BasicDoubleSymbol(8.1)),
                new AddOperation(
                    new MulOperation(
                        new BasicDoubleSymbol(2.7),
                        new BasicDoubleSymbol(-2.1)
                    ),
                    new BasicDoubleSymbol(0.1)
                )
            ),
            new AddOperation(
                new AddOperation(
                    new BasicDoubleSymbol(7.9),
                    new BasicDoubleSymbol(-2.3)),
                new AddOperation(
                    new AddOperation(
                        new BasicDoubleSymbol(9.9),
                        new MulOperation(
                            new BasicDoubleSymbol(2.3),
                            new BasicDoubleSymbol(-1.1))),
                    new BasicDoubleSymbol(2.1)
                )
            )
        );
        System.out.println(sym.fmt());
        List<String> lines = Arrays.asList("4", "6", "7");
        Integer ls = lines.stream().map(Integer::valueOf).reduce(Integer::sum).orElseThrow();
//        System.out.println(ls);
//        for (String x: args) {
//            System.out.println(x);
//        }
    }
}

//class X {
//    X(int a) {}  // ...
//}
//
//class Y extends X {
//    Y() {
//        super(9);
//    }
//}

//class Foo {
//    public static void m(Integer a) {
//        System.out.println("Integer");
//    }
//    public static void m(Boolean a) {
//        System.out.println("Bool");
//    }
//    public static void m(Object a) {
//        System.out.println("Oh No");
//        assert false;
//    }
//
//    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Object o = 11;
//        Foo.class.getMethod("m", o.getClass()).invoke(null, o);
//    }
//}

//interface BaseOperation {
//
//}
//
//class Add implements BaseOperation {
//
//}
//
//enum OperationPrecedence {
//    ADD(1, Add.class),
//    MUL(2, null);
//
//    static final private HashMap<Class<? extends BaseOperation>, OperationPrecedence> CLS_TO_OBJ_MAP = new HashMap<>();
//
//    final int precedence;
//    final Class<? extends BaseOperation> cls;
//
//    OperationPrecedence(int precedence, Class<? extends BaseOperation> cls) {
//        precedence = precedence;
//        cls = cls;
//    }
//
//    public boolean equals(OperationPrecedence other) {
//        return precedence == other.precedence;
//    }
//
//    public static OperationPrecedence fromClass(Class<? extends BaseOperation> cls) {
//        return CLS_TO_OBJ_MAP.get(cls);
//    }
//
//    static {
//        for(OperationPrecedence p : values()) {
//            CLS_TO_OBJ_MAP.put(p.cls, p);
//        }
//    }
//}
