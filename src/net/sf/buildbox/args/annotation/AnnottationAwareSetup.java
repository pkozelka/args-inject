package net.sf.buildbox.args.annotation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import net.sf.buildbox.args.ArgsUtils;
import net.sf.buildbox.args.ParsedOption;
import net.sf.buildbox.args.api.ArgsSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CliDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubcommandDeclaration;

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
    public void setDefaultSubcommand(Class<? extends ExecutableCommand> cmdClass) throws ParseException {
        if (cmdClass != null) {
            final SubcommandDeclaration subcommandDeclaration = createCmdDecl(cmdClass);
            cliDeclaration.setDefaultCommand(subcommandDeclaration);
            introspectOptions(cmdClass, subcommandDeclaration, null);
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
        final SubcommandDeclaration subcommandDeclaration = createCmdDecl(cmdClass);
        cliDeclaration.addCommand(subcommandDeclaration);
        introspectOptions(cmdClass, subcommandDeclaration, null);
    }

    private void introspectOptions(Class<?> cmdClass, SubcommandDeclaration attachToSubcommandDeclaration, Object attachToGlobalObject) throws ParseException {
        for (Class iface : cmdClass.getInterfaces()) {
            introspectOptions(iface, attachToSubcommandDeclaration, attachToGlobalObject);
        }
        final Class<?> superclass = cmdClass.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            introspectOptions(superclass, attachToSubcommandDeclaration, attachToGlobalObject);
        }
        for (Method method : cmdClass.getMethods()) {
            if (method.getAnnotation(Option.class) != null) {
                final OptionDeclaration optionDeclaration = createOptionDecl(method);
                cliDeclaration.addOption(optionDeclaration);
                if (attachToSubcommandDeclaration != null) {
                    attachToSubcommandDeclaration.addOptionDeclaration(optionDeclaration);
                } else if (attachToGlobalObject != null) {
                    optionDeclaration.setGlobalObject(attachToGlobalObject);
                }
            }
        }
    }

    private SubcommandDeclaration createCmdDecl(Class<? extends ExecutableCommand> cmdClass) {
        final SubCommand annCommand = cmdClass.getAnnotation(SubCommand.class);
        // note: @SubCommand is optional
        final SubcommandDeclaration cmdDecl = new SubcommandDeclaration(cmdClass);
        if (annCommand != null) {
            cmdDecl.setName(annCommand.name());
            cmdDecl.addAlternateNames(Arrays.asList(annCommand.aliases()));
        }
        final Constructor<? extends ExecutableCommand> con = findPublicConstructor(cmdDecl.getCommandClass());
        ParamDeclaration paramDecl = null;
        for (Class paramType : con.getParameterTypes()) {
            paramDecl = createParamDecl(paramType);
            cmdDecl.addParamDeclaration(paramDecl);
        }
        // if the constructor is varargs, note it in the last param declaration - which is expected to be array
        if (paramDecl != null && con.isVarArgs()) {
            paramDecl.setVarArgs(true);
        }
        return cmdDecl;
    }

    private OptionDeclaration createOptionDecl(Method method) throws ParseException {
        final Option annOption = method.getAnnotation(Option.class);
        // note: @Option is mandatory
        final OptionDeclaration optionDeclaration = new OptionDeclaration(annOption.shortName(), annOption.longName(), method);
        for (Class<?> paramType : method.getParameterTypes()) {
            optionDeclaration.addParamDeclaration(createParamDecl(paramType));
        }
        return optionDeclaration;
    }

    private ParamDeclaration createParamDecl(Class<?> paramType) {
        final Param annParam = paramType.getAnnotation(Param.class);
        // note: @Param is optional
        final ParamDeclaration paramDecl = new ParamDeclaration(paramType);
        final String format = annParam == null ? null : annParam.format();
        paramDecl.setFormat("".equals(format) ? null : format);
        if (paramType.isArray()) {
            final String listSeparator = annParam == null ? "" : annParam.listSeparator();
            if (!"".equals(listSeparator)) {
                paramDecl.setListSeparator(listSeparator);
            } else if (paramType.getComponentType().equals(File.class)) {
                paramDecl.setListSeparator(File.pathSeparator);
            } else {
                paramDecl.setListSeparator(",");
            }
        }
        final String timeZone = annParam == null ? "" : annParam.timezone();
        paramDecl.setTimeZone("".equals(timeZone) ? TimeZone.getDefault().getID() : timeZone);
        return paramDecl;
    }

    public CliDeclaration getDeclaration() {
        return cliDeclaration;
    }

    public ExecutableCommand createSubcommand(SubcommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException {
        final String cmdName = cmdParams.removeFirst();
        final List<Object> unmarshalledValues = ParamDeclaration.parseParamList("command " + cmdName, cmdDecl.getParamDeclarations(), cmdParams);
        // find public constructor
        final Class<? extends ExecutableCommand> cmdClass = cmdDecl.getCommandClass();
        final Constructor<? extends ExecutableCommand> con = findPublicConstructor(cmdClass);
        ArgsUtils.debug("cmd constructor: %s%s", con, unmarshalledValues);
        try {
            return con.newInstance(unmarshalledValues.toArray(new Object[unmarshalledValues.size()]));
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
            final List<Object> unmarshalledValues = option.getValues();
            final Method method = option.getOptionDecl().getOptionMethod();
            ArgsUtils.debug("  option method: %s%s", method, unmarshalledValues.isEmpty() ? "" : unmarshalledValues);
            try {
                final Object[] values = unmarshalledValues.toArray(new Object[unmarshalledValues.size()]);
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

    @SuppressWarnings("unchecked")
    public static Constructor<ExecutableCommand> findPublicConstructor(Class<? extends ExecutableCommand> cmdClass) {
        //TODO: fail if more public constructors exist
        for (Constructor<ExecutableCommand> constructor : cmdClass.getConstructors()) {
            if (Modifier.isPublic(constructor.getModifiers())) return constructor;
        }
        throw new IllegalStateException("There is no public constructor on " + cmdClass);
    }
}
