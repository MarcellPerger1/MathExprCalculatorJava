package net.marcellperger.mathexpr.interactive;

import java.util.Arrays;

import static net.marcellperger.mathexpr.util.Input.input;

public class Shell {
    public static void main(String[] args) {
        System.out.println("args = " + Arrays.toString(args));
        System.out.print("Enter something: ");
        System.out.println("You entered " + input());
    }
}
