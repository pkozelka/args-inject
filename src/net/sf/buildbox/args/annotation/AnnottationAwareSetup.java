package net.sf.buildbox.args.annotation;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;
import net.sf.buildbox.args.ArgsUtils;
import net.sf.buildbox.args.ParsedOption;
import net.sf.buildbox.args.api.ArgsSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CliDeclaration;
import net.sf.buildbox.args.model.CommandDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;

public class AnnottationAwareSetup implements ArgsSetup {

    private final CliDeclaration cliDeclaration = new CliDeclaration();

    public AnnottationAwareSetup(String programName) {
        cliDeclaration.setProgramName(programName);
    }

    /**
     * Default command to be used if none is present explicitly.
     *
     * @param cmdClass the command class to be used when the first non-option argument matches none of {@link #setSupportedCommands(Class[]) supportedCommands}
     * @throws ParseException -
     */
    public void setDefaultCommand(Class<? extends ExecutableCommand> cmdClass) throws ParseException {
        if (cmdClass != null) {
            final CommandDeclaration commandDeclaration = createCmdDecl(cmdClass);
            cliDeclaration.setDefaultCommand(commandDeclaration);
            introspectOptions(cmdClass, commandDeclaration, null);
        }
    }

    /**
     * Specify all commands that can be dispatched by the parser using their name.
     *
     * @param supportedCommands all command classes
     * @throws java.text.ParseException when parsing fails
     */
    public void setSupportedCommands(Class<? extends ExecutableCommand>... supportedCommands) throws ParseException {
        for (Class<? extends ExecutableCommand> cmdClass : supportedCommands) {
            introspectCommands(cmdClass);
        }
    }

    /**
     * Attaches objects that will receive values of global options.
     *
     * @param globalOptionObjects -
     * @throws ParseException -
     */
    public void setGlobalOptions(Object... globalOptionObjects) throws ParseException {
        cliDeclaration.setGlobalOptions(globalOptionObjects);
        for (Object globalOptionsObject : globalOptionObjects) {
            introspectOptions(globalOptionsObject.getClass(), null, globalOptionsObject);
        }
    }

    private void introspectCommands(Class<? extends ExecutableCommand> cmdClass) throws ParseException {
        final CommandDeclaration commandDeclaration = createCmdDecl(cmdClass);
        cliDeclaration.addCommand(commandDeclaration);
        introspectOptions(cmdClass, commandDeclaration, null);
    }

    private void introspectOptions(Class<?> cmdClass, CommandDeclaration attachToCommandDeclaration, Object attachToGlobalObject) throws ParseException {
        for (Class iface : cmdClass.getInterfaces()) {
            introspectOptions(iface, attachToCommandDeclaration, attachToGlobalObject);
        }
        final Class<?> superclass = cmdClass.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            introspectOptions(superclass, attachToCommandDeclaration, attachToGlobalObject);
        }
        for (Method method : cmdClass.getMethods()) {
            if (method.getAnnotation(CliOption.class) != null) {
                final OptionDeclaration optionDeclaration = createOptionDecl(method);
                cliDeclaration.addOption(optionDeclaration);
                if (attachToCommandDeclaration != null) {
                    attachToCommandDeclaration.addOption(optionDeclaration);
                } else if (attachToGlobalObject != null) {
                    optionDeclaration.setGlobalObject(attachToGlobalObject);
                }
            }
        }
    }

    private CommandDeclaration createCmdDecl(Class<? extends ExecutableCommand> cmdClass) {
        final CliCommand annCommand = cmdClass.getAnnotation(CliCommand.class);
        // note: @CliCommand is optional
        final CommandDeclaration cmdDecl = new CommandDeclaration(cmdClass);
        if (annCommand != null) {
            cmdDecl.setName(annCommand.name());
            cmdDecl.addAlternateNames(Arrays.asList(annCommand.aliases()));
        }
        final Constructor<? extends ExecutableCommand> con = ArgsUtils.findPublicConstructor(cmdDecl.getCommandClass());
        for (Class paramType : con.getParameterTypes()) {
            cmdDecl.addParam(createParamDecl(paramType));
        }
        return cmdDecl;
    }

    private OptionDeclaration createOptionDecl(Method method) throws ParseException {
        final CliOption annOption = method.getAnnotation(CliOption.class);
        // note: @CliOption is mandatory
        final OptionDeclaration optionDeclaration = new OptionDeclaration(annOption.shortName(), annOption.longName(), method);
        for (Class<?> paramType : method.getParameterTypes()) {
            optionDeclaration.addParam(createParamDecl(paramType));
        }
        return optionDeclaration;
    }

    private ParamDeclaration createParamDecl(Class<?> paramType) {
        final CliParam annParam = paramType.getAnnotation(CliParam.class);
        // note: @CliParam is optional
        final ParamDeclaration paramDecl = new ParamDeclaration(paramType);
        final String format = annParam == null ? null : annParam.format();
        paramDecl.setFormat("".equals(format) ? null : format);
        final String listSeparator = annParam == null ? "" : annParam.listSeparator();
        paramDecl.setListSeparator("".equals(listSeparator) ? File.pathSeparator : listSeparator);
        final String timeZone = annParam == null ? "" : annParam.timezone();
        paramDecl.setTimeZone("".equals(timeZone) ? TimeZone.getDefault().getID() : timeZone);
        return paramDecl;
    }

    public CliDeclaration getDeclaration() {
        return cliDeclaration;
    }

    public ExecutableCommand createCommandInstance(CommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException {
        //TODO: use ParamDeclarations
        final String cmdName = cmdParams.removeFirst();
        final Class<? extends ExecutableCommand> cmdClass = cmdDecl.getCommandClass();
        // parse remaining tokens as the public constructor's params
        final List<Object> cmdParamsUnmarshalled = new ArrayList<Object>();
        for (ParamDeclaration paramDecl : cmdDecl.getParams()) {
            final Class paramType = paramDecl.getType();
            if (paramType.isArray()) {
                final List<Object> lastArrayParam = new ArrayList<Object>();
                while (!cmdParams.isEmpty()) {
                    lastArrayParam.add(ArgsUtils.stringToType(cmdParams.removeFirst(), paramType.getComponentType()));
                }
                final Object array = Array.newInstance(paramType.getComponentType(), lastArrayParam.size());
                cmdParamsUnmarshalled.add(lastArrayParam.toArray((Object[]) array));
            } else {
                if (cmdParams.isEmpty()) {
                    throw new ParseException("command " + cmdName + ": not enough parameters provided", 0);
                }
                cmdParamsUnmarshalled.add(ArgsUtils.stringToType(cmdParams.removeFirst(), paramType));
            }
        }
        // instantiate cmdClass
        // find public constructor
        final Constructor<? extends ExecutableCommand> con = ArgsUtils.findPublicConstructor(cmdClass);
        ArgsUtils.debug("cmd constructor: %s%s", con, cmdParamsUnmarshalled);
        try {
            return con.newInstance(cmdParamsUnmarshalled.toArray(new Object[cmdParamsUnmarshalled.size()]));
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void injectOptions(ExecutableCommand commandInstance, List<ParsedOption> parsedOptions, String cmdName) throws ParseException {
        final Class<? extends ExecutableCommand> cmdClass = commandInstance.getClass();
        // set options on command instance
        L1:
        for (ParsedOption option : parsedOptions) {
            final Object[] values = option.getValues();
            final Method method = option.getOptionDecl().getOptionMethod();
            ArgsUtils.debug("  option method: %s%s", method, values.length == 0 ? "" : Arrays.asList(values));
            try {
                final Class<?> declaringClass = method.getDeclaringClass();
                if (declaringClass.isAssignableFrom(cmdClass)) {
                    method.invoke(commandInstance, values);
                } else {
                    final Object[] globalOptionsObjects = cliDeclaration.getGlobalOptionsObjects();
                    for (Object globalOptionsObject : globalOptionsObjects) {
                        if (declaringClass.isAssignableFrom(globalOptionsObject.getClass())) {
                            method.invoke(globalOptionsObject, values);
                            continue L1;
                        }
                    }
                    throw new ParseException(String.format("command %s does not accept option '%s'",
                            cmdName, option.getUsedName()), 0);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        // special cases - todo: generalize ?
        if (commandInstance instanceof MetaCommand) {
            final MetaCommand h = (MetaCommand) commandInstance;
            h.setDeclaration(cliDeclaration);
        }
    }
}
