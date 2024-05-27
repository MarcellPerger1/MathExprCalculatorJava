package net.marcellperger.mathexpr.interactive;

import java.util.Arrays;
import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("args = " + Arrays.toString(args));
        System.out.print("Enter something: ");
        String s = sc.nextLine();
        System.out.println("You entered " + s);
    }
}
