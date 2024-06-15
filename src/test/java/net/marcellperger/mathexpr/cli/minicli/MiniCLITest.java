package net.marcellperger.mathexpr.cli.minicli;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            cli.parseArgs(new String[]{"-R", "6", "1+1 * 2"});
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
            //  need to support `-` or need to see if this doesn't match any option (nut then adding an option would be breaking)
            assertThrows(CLIParseException.class, () -> cli.parseArgs(new String[]{"1+2", "*8"}));
        }
    }

}