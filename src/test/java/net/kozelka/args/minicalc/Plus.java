package net.kozelka.args.minicalc;

import net.kozelka.args.annotation.SubCommand;
import net.kozelka.args.api.ArgsCommand;

@SubCommand(name = "plus", description = "computes sum of all given numbers")
public class Plus implements ArgsCommand {
    private final int[] numbers;

    public Plus(int... numbers) {
        this.numbers = numbers;
    }

    public Integer call() throws Exception {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        System.out.println(sum);
        return 0;
    }
}
