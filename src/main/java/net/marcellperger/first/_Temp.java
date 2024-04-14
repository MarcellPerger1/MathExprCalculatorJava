package net.marcellperger.first;

public class _Temp {
    public static void main(String[] args) {
        Impl i = new Impl();
        i.methodUsingSSM();
    }
}
interface Y {

}
final class X {
    static void dummy() {
        System.out.println("Dummy called");
    }
}

interface Base<Sub extends Base<Sub>> {
    static/*<T extends X & Y>*/ void someStaticMethod() {
        //T.dummy();
        System.out.println("Base");
    }

    default void methodUsingSSM() {
        Sub.someStaticMethod();
    }
}

class Impl implements Base<Impl> {
    static void someStaticMethod() {
        System.out.println("Impl");
    }
}

//interface IBase<For extends NormalBase> {
//    default void whatever2() {
//        System.out.println("IBase.whatever2");
//        For.whatever();
//    }
//}
//class NormalBase implements IBase<NormalBase> {
//    static void whatever() {
//        System.out.println("NormalBase.whatever");
//    }
//}
//class NormalSub extends NormalBase implements IBase<NormalSub> {
//    static void whatever() {
//        System.out.println("NormalSub.whatever");
//    }
//}
