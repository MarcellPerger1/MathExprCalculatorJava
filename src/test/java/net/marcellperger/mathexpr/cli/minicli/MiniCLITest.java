package net.marcellperger.mathexpr.cli.minicli;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MiniCLITest {
    @Nested
    class ParseArgsE2E_MathexprCLI {
        @Test
        void testMathexprCLI() {
            MiniCLI cli = new MiniCLI();
            CLIOption<String> rounding = cli.addStringOption("-R", "--round-sf").setDefault("12");
            CLIOption<Boolean> interactive = cli.addBooleanOption("-i", "--interactive");
            cli.setPositionalArgCount(0, 1);
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
            // TODO 2nd arg cannot be -8 here as it is interpreted as a flag so
            //  need to support `--` or need to see if this doesn't match any option
            //  (but then adding an option would be a breaking change)
            assertThrowsAndMsgContains(CLIParseException.class, "positional args",
                () -> cli.parseArgs(new String[]{"1+2", "*8"}));
            assertThrowsAndMsgContains(CLIParseException.class, "Bad boolean value",
                () -> cli.parseArgs(new String[]{"--interactive=abc"}));
        }
    }

    <T extends Throwable> void assertThrowsAndMsgContains(@SuppressWarnings("SameParameterValue") Class<T> cls,
                                                          String containsMsg, Executable fn) {
        T exc = assertThrows(cls, fn);
        assertTrue(exc.getMessage().contains(containsMsg), () ->
            "Expected error message ('%s') to contain %s".formatted(exc.getMessage(), containsMsg));
    }
}