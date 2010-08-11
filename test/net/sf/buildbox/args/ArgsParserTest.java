package net.sf.buildbox.args;

import java.text.ParseException;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import org.junit.Assert;
import org.junit.Test;

public class ArgsParserTest {
    @Test
    public void testMultiFiles() throws Exception {
        // START SNIPPET: example1
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("myapp");
        setup.setDefaultSubCommand(DemoFileLister.class);
        final ExecutableCommand cmd = BasicArgsParser.parse(setup,
                "-D", "a", "b",
                "false", "17", "/tmp", "/var", "/root",
                "-C=true",
                "--property", "xxx", "yyy");
        System.out.println("--- execute ---");
        cmd.call();
        // END SNIPPET: example1
    }

    @Test
    public void testGlobalHelp() throws Exception {
        // START SNIPPET: default-help
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testGlobalHelp");
        setup.addSubCommand(DefaultHelpCommand.class);
        // END SNIPPET: default-help
        setup.setDefaultSubCommand(DemoFileLister.class);
        System.err.println("=== using 'help' ===");
        BasicArgsParser.parse(setup, "help").call();
        System.err.println("=== using '--help' ===");
        BasicArgsParser.parse(setup, "--help").call();
    }

    @Test
    public void testCommandHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testCommandHelp");
        setup.setSubCommands(DefaultHelpCommand.class);
        final int exitCode = BasicArgsParser.process(setup, "help", "help");
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testInvalidCommandHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testCommandHelp");
        setup.setSubCommands(DefaultHelpCommand.class);
        final int exitCode = BasicArgsParser.process(setup, "help", "changeEarthRotation");
        Assert.assertEquals(1, exitCode);
    }

    @Test(expected = ParseException.class)
    public void testInvalidCommand() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testInvalidCommand");
        setup.setSubCommands(DefaultHelpCommand.class);
        BasicArgsParser.parse(setup, "invaLID").call();
    }

    // START SNIPPET: sample-main
    public static void main(String[] args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("myapp");
        setup.setDefaultSubCommand(DemoFileLister.class);
        final int exitCode = BasicArgsParser.process(setup, args);
        if (exitCode != 0) {
            System.exit(exitCode); // indicate failure to shell
        }
    }
    // END SNIPPET: sample-main
}
