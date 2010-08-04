package net.sf.buildbox.args.zer;

import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.SingleCommandBuilder;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import org.junit.Test;

public class BuildozerCliTest {

    @Test
    public void testBuildozer() throws Exception {
        final LogLevelCliOptions lo = new LogLevelCliOptions();
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("zer");
        setup.setDefaultCommand(ZerExecute.class);
        setup.setSupportedCommands(DefaultHelpCommand.class, ZerDepends.class, ZerInfo.class, ZerRegister.class, ZerUnregister.class);
        setup.setGlobalOptions(lo);
//        SingleCommandBuilder.buildCommand(setup, "-vv", "hello", "z", "dd", "--xml").call();
        SingleCommandBuilder.buildCommand(setup, "help").call();
    }
}
