package net.marcellperger.first;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class _Temp2 {
    public static void main(String[] args) {
        List<? extends Entry<Integer, ? extends Set<? extends Class<? extends MyInterface>>>> result_verboseType =
            MyRecord.RECORDS.stream().collect(
                    Collectors.groupingBy(MyRecord::key,
                        Collectors.mapping(MyRecord::cls,
                            Collectors.toUnmodifiableSet())))
                .entrySet().stream().toList();

//        List<Entry<Integer, Set<Class<? extends MyInterface>>>> result_direct =
//            MyRecord.RECORDS.stream().collect(
//                    Collectors.groupingBy(MyRecord::key,
//                        Collectors.mapping(MyRecord::cls,
//                            Collectors.toUnmodifiableSet())))
//                .entrySet().stream().toList();

        Map<Integer, Set<Class<? extends MyInterface>>> middleValue =
            MyRecord.RECORDS.stream().collect(
                Collectors.groupingBy(MyRecord::key,
                    Collectors.mapping(MyRecord::cls,
                        Collectors.toUnmodifiableSet())));
        List<Entry<Integer, Set<Class<? extends MyInterface>>>> result_indirect = middleValue
            .entrySet().stream().toList();

        System.out.println("result_verboseType = " + result_verboseType);
//        System.out.println("result_direct = " + result_direct);
        System.out.println("result_indirect = " + result_indirect);
    }
}

record MyRecord(Integer key, Class<? extends MyInterface> cls) {
    static List<MyRecord> RECORDS = List.of(new MyRecord(1, ClassA.class), new MyRecord(2, ClassB.class));
}

interface MyInterface {}
class ClassA implements MyInterface {}
class ClassB implements MyInterface {}
