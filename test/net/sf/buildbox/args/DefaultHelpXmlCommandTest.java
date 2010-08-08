package net.sf.buildbox.args;

import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import org.junit.Test;

public class DefaultHelpXmlCommandTest {
    @Test
    public void testXmlHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testXmlHelp");
        setup.setDefaultSubCommand(DemoFileLister.class);
        setup.setSubCommands(DefaultHelpCommand.class, DefaultHelpXmlCommand.class);
        final int exitCode = BasicArgsParser.process(setup, "help-xml");
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
