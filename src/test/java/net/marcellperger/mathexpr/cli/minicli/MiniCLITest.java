package net.marcellperger.mathexpr.cli.minicli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MiniCLITest {
    @Nested
    class ParseArgsE2E_MathexprCLI {
        MiniCLI cli;
        CLIOption<String> rounding;
        CLIOption<Boolean> interactive;

        @BeforeEach
        void setUp() {
            cli = new MiniCLI();
            rounding = cli.addStringOption("-R", "--round-sf").setDefault("12");
            interactive = cli.addBooleanOption("-i", "--interactive");
            cli.setPositionalArgCount(0, 1);
        }

        @Test
        void testMathexprCLI() {
            cli.parseArgs(new String[]{});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of(), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-i", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(true, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--interactive", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(true, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--interactive=false", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--interactive=true", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(true, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-i=yes", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(true, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-i=0", "1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-i", "0"});
            assertEquals("12", rounding.getValue());
            assertEquals(true, interactive.getValue());
            assertEquals(List.of("0"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-R", "6", "1+1 * 2"});
            assertEquals("6", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{ "1+1 * 2", "-R", "6"});
            assertEquals("6", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"-R", "-9", "1+1 * 2"});
            assertEquals("-9", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--round-sf=99", "1+1 * 2"});
            assertEquals("99", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("1+1 * 2"), cli.getPositionalArgs());

            assertThrowsAndMsgContains(CLIParseException.class, "-R/--round-sf option requires a value",
                () -> cli.parseArgs(new String[]{"1+1", "-R"}));
            assertThrowsAndMsgContains(CLIParseException.class, "positional args",
                () -> cli.parseArgs(new String[]{"1+2", "*8"}));
            assertThrowsAndMsgContains(CLIParseException.class, "Bad boolean value 'abc' for -i/--interactive",
                () -> cli.parseArgs(new String[]{"--interactive=abc"}));

        }

        @Test
        void doubleDashHandling() {
            cli.parseArgs(new String[]{"--", "-1+1 * 2"});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("-1+1 * 2"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--", "-i"});
            assertEquals("12", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("-i"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--round-sf=3", "--", "--"});
            assertEquals("3", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("--"), cli.getPositionalArgs());
            cli.parseArgs(new String[]{"--round-sf=3", "--", "--"});
            assertEquals("3", rounding.getValue());
            assertEquals(false, interactive.getValue());
            assertEquals(List.of("--"), cli.getPositionalArgs());
            assertThrowsAndMsgContains(CLIParseException.class, "positional args",
                () -> cli.parseArgs(new String[]{"--", "--interactive", "6+2"}));
        }
    }

    @Test
    void test_miniCli_options_notParsing() {
        {
            MiniCLI cli = new MiniCLI();
            cli.addStringOption("--abc", "-a", "-b");
            assertThrowsAndMsgContains(IllegalStateException.class, "already been registered",
                () -> cli.addBooleanOption("--qwerty", "-a"));
        }
        {
            MiniCLI cli = new MiniCLI();
            assertThrowsAndMsgContains(IllegalStateException.class,
                "defaultIfNoValue should not be specified with a REQUIRED valueMode",
                () -> {
                    cli.addStringOption("-a").setValueMode(ValueMode.REQUIRED).setDefaultIfNoValue("v");
                    cli.validate();
                });
        }
    }

    <T extends Throwable> void assertThrowsAndMsgContains(@SuppressWarnings("SameParameterValue") Class<T> cls,
                                                          String containsMsg, Executable fn) {
        T exc = assertThrows(cls, fn);
        assertTrue(exc.getMessage().contains(containsMsg), () ->
            "Expected \"%s\" to contain \"%s\"".formatted(exc.getMessage(), containsMsg));
    }
}