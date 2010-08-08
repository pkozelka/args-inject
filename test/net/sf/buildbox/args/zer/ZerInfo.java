package net.sf.buildbox.args.zer;

import java.io.File;
import net.sf.buildbox.args.annotation.SubCommand;

@SubCommand(name = "info", description = "shows basic module information")
public class ZerInfo extends ZerXmlfulCommand {
    private final File[] modules;

    public ZerInfo(File... modules) {
        this.modules = modules;
    }

    public Integer call() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        return 0;
    }
}