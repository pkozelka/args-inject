package net.sf.buildbox.args.annotation;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.ArgsUtils;
import net.sf.buildbox.args.ParsedOption;
import net.sf.buildbox.args.api.ArgsSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubCommandDeclaration;

public class AnnottationAwareSetup implements ArgsSetup {

    private final CommandlineDeclaration cliDeclaration = new CommandlineDeclaration();

    public AnnottationAwareSetup(String programName) {
        cliDeclaration.setProgramName(programName);
    }

    /**
     * Default command to be used if none is present explicitly.
     *
     * @param subCommand the command class to be used when the first non-option argument matches none of {@link #setSubCommands(Class[]) supportedCommands}
     * @throws ParseException -
     */
    public void setDefaultSubCommand(Class<? extends ExecutableCommand> subCommand) throws ParseException {
        final SubCommandDeclaration subCommandDeclaration = createCmdDecl(subCommand);
        cliDeclaration.setDefaultCommand(subCommandDeclaration);
        introspectOptions(subCommand, subCommandDeclaration, null);
    }

    /**
     * Specify all commands that can be dispatched by the parser using their name.
     *
     * @param subCommands all command classes
     * @throws java.text.ParseException when parsing fails
     */
    public void setSubCommands(Class<? extends ExecutableCommand>... subCommands) throws ParseException {
        for (Class<? extends ExecutableCommand> subCommand : subCommands) {
            addSubCommand(subCommand);
        }
    }

    public void addSubCommand(Class<? extends ExecutableCommand> subCommand) throws ParseException {
        introspectCommands(subCommand);
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
        final SubCommandDeclaration subCommandDeclaration = createCmdDecl(cmdClass);
        cliDeclaration.addSubCommand(subCommandDeclaration);
        introspectOptions(cmdClass, subCommandDeclaration, null);
    }

    private void introspectOptions(Class<?> subCommand, SubCommandDeclaration attachToSubCommandDeclaration, Object attachToGlobalObject) throws ParseException {
        final String cn = subCommand.getName();
        if (cn.startsWith("java.")) return;
//        System.out.println("cls " + cn);
        for (Class iface : subCommand.getInterfaces()) {
            introspectOptions(iface, attachToSubCommandDeclaration, attachToGlobalObject);
        }
        final Class<?> superclass = subCommand.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            introspectOptions(superclass, attachToSubCommandDeclaration, attachToGlobalObject);
        }
        for (Method method : subCommand.getMethods()) {
            if (method.getAnnotation(Option.class) != null) {
                final OptionDeclaration optionDeclaration = createOptionDecl(method);
                cliDeclaration.addOption(optionDeclaration);
                if (attachToSubCommandDeclaration != null) {
                    attachToSubCommandDeclaration.addOptionDeclaration(optionDeclaration);
                } else if (attachToGlobalObject != null) {
                    optionDeclaration.setGlobalObject(attachToGlobalObject);
                }
            }
        }
    }

    private SubCommandDeclaration createCmdDecl(Class<? extends ExecutableCommand> cmdClass) {
        final SubCommand annCommand = cmdClass.getAnnotation(SubCommand.class);
        // note: @SubCommand is optional
        final SubCommandDeclaration cmdDecl = new SubCommandDeclaration(cmdClass);
        if (annCommand != null) {
            cmdDecl.setName(annCommand.name().equals("") ? null : annCommand.name());
            cmdDecl.addAlternateNames(Arrays.asList(annCommand.aliases()));
            final String desc = annCommand.description();
            cmdDecl.setDescription("".equals(desc) ? null : desc);
        }
        final Constructor<? extends ExecutableCommand> con = findPublicConstructor(cmdDecl.getCommandClass());
        ParamDeclaration paramDecl = null;
        for (int i = 0; i < con.getParameterTypes().length; i++) {
            final Class<?> paramType = con.getParameterTypes()[i];
            final Annotation[] pa = con.getParameterAnnotations()[i];
            paramDecl = createParamDecl(paramType, pa);
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
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            final Class<?> paramType = method.getParameterTypes()[i];
            final Annotation[] pa = method.getParameterAnnotations()[i];
            optionDeclaration.addParamDeclaration(createParamDecl(paramType, pa));
        }
        final String desc = annOption.description();
        optionDeclaration.setDescription("".equals(desc) ? null : desc);
        return optionDeclaration;
    }

    private ParamDeclaration createParamDecl(Class<?> paramType, Annotation[] pa) {
        Param annParam = null;
        for (Annotation annotation : pa) {
            if (annotation instanceof Param) {
                annParam = (Param) annotation;
            }
        }
        // note: @Param is optional
        final ParamDeclaration paramDecl = new ParamDeclaration(paramType);
        if (annParam != null) {
            final String format = annParam.format();
            paramDecl.setFormat("".equals(format) ? null : format);
            final String name = annParam.value();
            paramDecl.setSymbolicName("".equals(name) ? null : name);
        }
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
        return paramDecl;
    }

    public CommandlineDeclaration getDeclaration() {
        return cliDeclaration;
    }

    public ExecutableCommand createSubCommand(String cmdName, SubCommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException {
        final List<Object> unmarshalledValues = ParamDeclaration.parseParamList("subcommand " + cmdName, cmdDecl.getParamDeclarations(), cmdParams);
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
                    for (Object globalOptionsObject : cliDeclaration.getGlobalOptionsObjects()) {
                        if (declaringClass.isAssignableFrom(globalOptionsObject.getClass())) {
                            method.invoke(globalOptionsObject, values);
                            continue L1;
                        }
                    }
                    throw new ParseException(String.format("subcommand %s does not accept option '%s'",
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
