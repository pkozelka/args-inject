package net.kozelka.args.minicalc;

import net.kozelka.args.annotation.Option;
import net.kozelka.args.annotation.Param;
import net.kozelka.args.annotation.SubCommand;
import net.kozelka.args.api.ArgsCommand;

@SubCommand(name = "log", description = "computes logarithm of given argument")
public class Logarithm implements ArgsCommand {
    private final double x;
    private double base = 0;

    public Logarithm(@Param("arg") double x) {
        this.x = x;
    }

    @Option(longName = "--base", shortName = "-b", description = "the base of the logarithm")
    public void setBase(@Param("base") double base) {
        this.base = base;
    }

    public Integer call() throws Exception {
        final double rv;
        if (base == 0) {
            // natural logarithm
            rv = Math.log(x);
        } else if (base == 10) {
            rv = Math.log10(x);
        } else {
            rv = Math.log(x) / Math.log(base);
        }
        System.out.println(rv);
        return 0;
    }
}
