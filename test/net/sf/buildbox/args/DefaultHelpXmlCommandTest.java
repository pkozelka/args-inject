package net.sf.buildbox.args;

import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import org.junit.Test;

public class DefaultHelpXmlCommandTest {
    @Test
    public void testXmlHelp() throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("testXmlHelp");
        setup.setDefaultCommand(DemoFileLister.class);
        setup.setSupportedCommands(DefaultHelpCommand.class, DefaultHelpXmlCommand.class);
        if (!SingleCommandBuilder.main(setup, "help-xml")) {
            System.exit(1);
        }
    }
}
