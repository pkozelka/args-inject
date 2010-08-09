package net.sf.buildbox.args.zer;

import java.text.ParseException;
import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.api.ArgsSetup;
import org.junit.Before;
import org.junit.Test;

public class BuildozerCliTest {
    private ArgsSetup setup;

    @Before
    public void setUp() throws ParseException {
        final LogLevelCliOptions lo = new LogLevelCliOptions();
        final AnnottationAwareSetup s = new AnnottationAwareSetup("zer");
        s.addSubCommand(ZerDepends.class);
        s.setDefaultSubCommand(ZerExecute.class);
        s.addSubCommand(DefaultHelpCommand.class);
        s.addSubCommand(ZerInfo.class);
        s.addSubCommand(ZerRegister.class);
        s.addSubCommand(ZerUnregister.class);
        s.setGlobalOptions(lo);
        setup = s;
    }

    @Test
    public void testBuildozer() throws Exception {
        BasicArgsParser.process(setup, "-vv", "hello", "z", "dd", "--xml");
    }

    @Test
    public void testBuildozerHelp() throws Exception {
        BasicArgsParser.process(setup, "help");
    }

    @Test
    public void testBuildozerHelpDepends() throws Exception {
        BasicArgsParser.process(setup, "help", "depends");
    }
}
