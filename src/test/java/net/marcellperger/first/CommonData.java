package net.marcellperger.first;

public class CommonData {
    private static MathSymbol getBigData1_obj() {
        return new AddOperation(
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
    }

    /**
     * Tests involving this are more like snapshot tests - this just returns this randomly generated MathSymbol
     * (small python script). You could call this a form of fuzzing.
     * <pre>{@code
     * import random
     *
     * OPTIONS = [s + 'Operation' for s in ('Add', 'Sub', 'Mul', 'Div')]
     *
     *
     * def happens(prob: float):
     *     return random.random() < prob
     *
     *
     * def gen_random_double_sym(curr_depth: int, is_arg1: bool):
     *     indent = ' ' * curr_depth * 4
     *     return (f'{indent}new BasicDoubleSymbol('
     *             f'{random.randint(-999, 999) / 10:.1f})'
     *             + (',' if is_arg1 else ''))
     *
     *
     * def get_leaf_prob(min_depth: int, max_depth: int, depth: int):
     *     if depth >= max_depth:
     *         return 1
     *     if depth < min_depth:
     *         return 0
     *     w = max_depth - min_depth
     *     o = depth - min_depth
     *     return o / w
     *
     *
     * def gen_random(min_depth: int, max_depth: int,
     *                curr_depth: int = 0, is_arg1=False) -> str:
     *     indent = ' ' * curr_depth * 4
     *     if happens(get_leaf_prob(min_depth, max_depth, curr_depth)):
     *         return gen_random_double_sym(curr_depth, is_arg1)
     *     name = random.choice(OPTIONS)
     *     arg1 = gen_random(min_depth, max_depth, curr_depth + 1, True)
     *     arg2 = gen_random(min_depth, max_depth, curr_depth + 1, False)
     *     result = (f'{indent}new {name}(\n'
     *               f'{arg1}\n'
     *               f'{arg2}\n'
     *               f'{indent})' + (',' if is_arg1 else ''))
     *     return result
     *
     *
     * if __name__ == '__main__':
     *     print(gen_random(2, 7))
     * }</pre>
     */
    private static MathSymbol getBigData2_obj() {
        return new DivOperation(
            new SubOperation(
                new AddOperation(
                    new BasicDoubleSymbol(68.0),
                    new DivOperation(
                        new MulOperation(
                            new BasicDoubleSymbol(93.5),
                            new BasicDoubleSymbol(-85.5)
                        ),
                        new BasicDoubleSymbol(41.1)
                    )
                ),
                new MulOperation(
                    new AddOperation(
                        new BasicDoubleSymbol(42.3),
                        new BasicDoubleSymbol(66.8)
                    ),
                    new DivOperation(
                        new AddOperation(
                            new DivOperation(
                                new BasicDoubleSymbol(77.8),
                                new BasicDoubleSymbol(45.3)
                            ),
                            new MulOperation(
                                new BasicDoubleSymbol(-10.7),
                                new BasicDoubleSymbol(65.6)
                            )
                        ),
                        new AddOperation(
                            new BasicDoubleSymbol(0.4),
                            new AddOperation(
                                new BasicDoubleSymbol(84.5),
                                new DivOperation(
                                    new BasicDoubleSymbol(-31.1),
                                    new BasicDoubleSymbol(90.6)
                                )
                            )
                        )
                    )
                )
            ),
            new MulOperation(
                new AddOperation(
                    new BasicDoubleSymbol(-37.6),
                    new MulOperation(
                        new BasicDoubleSymbol(59.5),
                        new MulOperation(
                            new SubOperation(
                                new BasicDoubleSymbol(-80.9),
                                new BasicDoubleSymbol(-72.2)
                            ),
                            new SubOperation(
                                new BasicDoubleSymbol(84.1),
                                new DivOperation(
                                    new BasicDoubleSymbol(-68.0),
                                    new BasicDoubleSymbol(-67.8)
                                )
                            )
                        )
                    )
                ),
                new MulOperation(
                    new DivOperation(
                        new DivOperation(
                            new AddOperation(
                                new BasicDoubleSymbol(-96.2),
                                new BasicDoubleSymbol(-1.2)
                            ),
                            new AddOperation(
                                new BasicDoubleSymbol(2.6),
                                new BasicDoubleSymbol(36.7)
                            )
                        ),
                        new BasicDoubleSymbol(40.6)
                    ),
                    new BasicDoubleSymbol(-57.4)
                )
            )
        );
    }

    public static Pair<MathSymbol, String> getBigData1_minimumParens() {
        return new Pair<>(getBigData1_obj(), "(0.2 + 8.1) * (2.7 * -2.1 + 0.1) + (7.9 + -2.3 + (9.9 + 2.3 * -1.1 + 2.1))");
    }
    public static Pair<MathSymbol, String> getBigData1_groupingParens() {
        return new Pair<>(getBigData1_obj(), "(0.2 + 8.1) * (2.7 * -2.1 + 0.1) + ((7.9 + -2.3) + ((9.9 + 2.3 * -1.1) + 2.1))");
    }

    public static Pair<MathSymbol, String> getBigData2_minimumParens() {
        return new Pair<>(getBigData2_obj(), "(68.0 + 93.5 * -85.5 / 41.1 - (42.3 + 66.8) * ((77.8 / 45.3 + -10.7 * 65.6) / (0.4 + (84.5 + -31.1 / 90.6)))) / ((-37.6 + 59.5 * ((-80.9 - -72.2) * (84.1 - -68.0 / -67.8))) * ((-96.2 + -1.2) / (2.6 + 36.7) / 40.6 * -57.4))");
    }
     public static Pair<MathSymbol, String> getBigData2_groupingParens() {
        return new Pair<>(getBigData2_obj(), "((68.0 + (93.5 * -85.5) / 41.1) - (42.3 + 66.8) * ((77.8 / 45.3 + -10.7 * 65.6) / (0.4 + (84.5 + -31.1 / 90.6)))) / ((-37.6 + 59.5 * ((-80.9 - -72.2) * (84.1 - -68.0 / -67.8))) * ((((-96.2 + -1.2) / (2.6 + 36.7)) / 40.6) * -57.4))");
    }
}
