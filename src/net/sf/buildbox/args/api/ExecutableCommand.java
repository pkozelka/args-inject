package net.sf.buildbox.args.api;

import java.util.concurrent.Callable;

/**
 * Must be implemented by classes representing (sub)command of the program.
 * Each implementing class is subject to following rules:
 * <ul>
 * <li>there is at most one public constructor</li>
 * <li>the public constructor's parameters define mandatory command arguments and their order</li>
 * <li>to have variable number of arguments, make the last param of array type</li>
 * <li>methods starting with "set", "add", "put" and equipped with {@link net.sf.buildbox.args.annotation.Option} annottations serve for feeding the instance with commandline options, possibly with values</li>
 * <li>method {@link #call} implements the command logic</li>
 * </ul>
 *
 * @todo replace with Callable<Integer>, where returning non-zero exitcode will be for silent failure
 */
public abstract interface ExecutableCommand extends Callable<Integer> {
    /**
     * Executes the business logic of the command. All specified options have already been populated via their setters.
     *
     * @return exitCode, typically 0
     * @throws Exception when anything goes wrong
     */
    Integer call() throws Exception;
}
