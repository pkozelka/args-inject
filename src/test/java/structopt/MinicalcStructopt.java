package structopt;

import java.util.Arrays;

public class MinicalcStructopt {

    interface MinicalcCli {

        @Structopt(defaultValue = "%d", shortOpt = 'f')
        String format();

        CalcOperation subcommand();
    }

    enum CalcOperation {
        @StructoptCommand(commandParams = PlusParams.class)
        PLUS,
        @StructoptCommand(commandParams = MinusParams.class)
        MINUS,
        @StructoptCommand(name = "log", commandParams = LogParams.class)
        LOGARITHM
    }

    interface PlusParams {
        int[] operands();
    }

    interface MinusParams {
        int op1();

        int op2();
    }

    interface LogParams {
        @Structopt(defaultValue = "10", shortOpt = 'b', longOpt = "base")
        int base();

        int number();
    }

    public static void main(String[] args) throws Exception {
        final StructoptParser<MinicalcCli> cliParser = new StructoptParser<>(MinicalcCli.class);
        final MinicalcCli cli = cliParser.parse(args);
        final Object params = cliParser.subcommandParams(cli.subcommand());
        switch (cli.subcommand()) {
            case PLUS:
                {
                    final PlusParams pp = (PlusParams) params;
                    final int result = Arrays.stream(pp.operands()).sum();
                    System.out.printf(cli.format(), result);
                }
                break;
            case MINUS:
                {
                    final MinusParams mp = (MinusParams) params;
                    final int result = mp.op1() - mp.op2();
                    System.out.printf(cli.format(), result);
                }
                break;
            case LOGARITHM:
                {
                    final LogParams p = (LogParams) params;
                    final double result = Math.log(p.number() / Math.log(p.base()));
                    System.out.printf(cli.format(), result);
                }
                break;
        }
    }
}
