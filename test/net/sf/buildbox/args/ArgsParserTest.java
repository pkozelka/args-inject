package net.sf.buildbox.args;

import java.text.ParseException;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import org.junit.Test;

public class ArgsParserTest {
    @Test
    public void testMultiFiles() throws Exception {
        // START SNIPPET: example1
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("myapp");
        setup.setDefaultCommand(DemoFileLister.class);
        final ExecutableCommand cmd = SingleCommandBuilder.buildCommand(setup, "-D", "a", "b", "false", "17", "/tmp", "/var", "/root", "-C=true", "--property", "xxx", "yyy");
        System.out.println("--- execute ---");
        cmd.call();
        // END SNIPPET: example1
    }

    @Test
    public void testGlobalHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testGlobalHelp");
        setup.setDefaultCommand(DemoFileLister.class);
        setup.setSupportedCommands(DefaultHelpCommand.class);
        System.err.println("=== using 'help' ===");
        SingleCommandBuilder.buildCommand(setup, "help").call();
        System.err.println("=== using '--help' ===");
        SingleCommandBuilder.buildCommand(setup, "--help").call();
    }

    @Test
    public void testCommandHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testCommandHelp");
        setup.setSupportedCommands(DefaultHelpCommand.class);
        SingleCommandBuilder.main(setup, "help", "help");
    }

    @Test(expected = ParseException.class)
    public void testInvalidCommand() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testInvalidCommand");
        setup.setSupportedCommands(DefaultHelpCommand.class);
        SingleCommandBuilder.buildCommand(setup, "invaLID").call();
    }
}
