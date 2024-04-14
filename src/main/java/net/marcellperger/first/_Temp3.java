import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WrongPackageStatement")
public class _Temp3 {
    interface MyInterface {};
    static class ClassA implements MyInterface {};
    static class ClassB implements MyInterface {};

    enum MyEnum {
        OPT_A(1, ClassA.class),
        OPT_B(2, ClassB.class),
        ;

        final Integer key;
        final Class<? extends MyInterface> cls;

        MyEnum(Integer key_, Class<? extends MyInterface> cls_) {
            cls = cls_;
            key = key_;
        }
    }


    public static void main(String[] args) {
        // This gives an error:
        // java: incompatible types: java.util.List<java.util.Map.Entry<java.lang.Integer,java.util.Set<java.lang.Class<capture#1 of ? extends _Temp3.MyInterface>>>> cannot be converted to java.util.List<java.util.Map.Entry<java.lang.Integer,java.util.Set<java.lang.Class<? extends _Temp3.MyInterface>>>>
        List<Entry<Integer, Set<Class<? extends MyInterface>>>> direct =
            Arrays.stream(MyEnum.values()).collect(
                    Collectors.groupingBy(s -> s.key,
                        Collectors.mapping(s -> s.cls,
                            Collectors.<Class<? extends MyInterface>>toUnmodifiableSet())))
                .entrySet().stream().toList();
        System.out.println("direct = " + direct);

        // This works (see https://stackoverflow.com/q/6293871)
        List<? extends Entry<Integer, ? extends Set<? extends Class<? extends MyInterface>>>> verboseType =
            Arrays.stream(MyEnum.values()).collect(
                    Collectors.groupingBy(s -> s.key,
                        Collectors.mapping(s -> s.cls,
                            Collectors.toUnmodifiableSet())))
                .entrySet().stream().toList();
        System.out.println("verboseType = " + verboseType);

        // But why does this work? Surely adding an extra variable shouldn't change anything compared to the top one.
        Map<Integer, Set<Class<? extends MyInterface>>> temp =
            Arrays.stream(MyEnum.values()).collect(
                Collectors.groupingBy(s -> s.key,
                    Collectors.mapping(s -> s.cls,
                        Collectors.toUnmodifiableSet())));
        List<Entry<Integer, Set<Class<? extends MyInterface>>>> indirect = temp
            .entrySet().stream().toList();
        System.out.println("indirect = " + indirect);
    }
}
