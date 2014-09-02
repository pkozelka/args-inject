package net.kozelka.args.minicalc;

import net.kozelka.args.annotation.Param;
import net.kozelka.args.annotation.SubCommand;
import net.kozelka.args.api.ArgsCommand;

@SubCommand(name = "minus", description = "computes operand1 - operand2")
public class Minus implements ArgsCommand {
    private final int x;
    private final int y;

    public Minus(@Param("operand1") int x, @Param("operand2") int y) {
        this.x = x;
        this.y = y;
    }

    public Integer call() throws Exception {
        System.out.println(x - y);
        return 0;
    }
}
