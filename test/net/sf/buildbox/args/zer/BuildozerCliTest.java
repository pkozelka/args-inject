package net.sf.buildbox.args.zer;

import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import org.junit.Test;

public class BuildozerCliTest {

    @Test
    public void testBuildozer() throws Exception {
        final LogLevelCliOptions lo = new LogLevelCliOptions();
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("zer");
        setup.setDefaultSubCommand(ZerExecute.class);
        setup.setSubCommands(DefaultHelpCommand.class, ZerDepends.class, ZerInfo.class, ZerRegister.class, ZerUnregister.class);
        setup.setGlobalOptions(lo);
//        BasicArgsParser.parse(setup, "-vv", "hello", "z", "dd", "--xml").call();
        BasicArgsParser.parse(setup, "help").call();
    }
}
