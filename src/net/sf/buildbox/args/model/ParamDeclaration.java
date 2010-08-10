package net.sf.buildbox.args.model;

import java.io.File;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import net.sf.buildbox.args.ArgsUtils;

public final class ParamDeclaration {
    private final Class<?> type;
    private String format;
    private String listSeparator = File.pathSeparator;
    private boolean varArgs;
    private String symbolicName;

    public ParamDeclaration(Class<?> type) {
        this.type = type;
    }

    private String defaultSymName() {
        if (isVarArgs()) {
            return type.getComponentType().getSimpleName() + "...";
        } else if (type.isArray()) {
            return type.getComponentType().getSimpleName() + "s"; // dirty way to make plural form
        } else {
            return type.getSimpleName().toLowerCase();
        }
    }

    public Class<?> getType() {
        return type;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getListSeparator() {
        return listSeparator;
    }

    public void setListSeparator(String listSeparator) {
        this.listSeparator = listSeparator;
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public void setVarArgs(boolean vararg) {
        this.varArgs = vararg;
    }

    private Object stringToType(String stringValue, Class<?> paramType) throws ParseException {
        // TODO: support for custom per-option or per-type unmarshalling should be implemented here
        // TODO: the value of 'format' field should be thoroughly reconsidered. Shouldn't we support more formats at once ? Should we expose SDF's parameter or wrap it with a logical nama like, DATE, TIME, DATETIME, TIMEMILLIS, DATETIMEMILLIS etc ? This needs to be clarified in light of some real app. usecases
        if (paramType.equals(Date.class)) {
            // support Date using format
            final SimpleDateFormat f = new SimpleDateFormat(format == null ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : format);
            return f.parse(stringValue);
        } else if (paramType.equals(Calendar.class)) {
            // support Calendar using format
            final Calendar cal = Calendar.getInstance();
            final SimpleDateFormat f = new SimpleDateFormat(format == null ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : format);
            final Date date = f.parse(stringValue);
            cal.setTime(date);
            return cal;
        } else {
            return ArgsUtils.stringToType(stringValue, paramType);
        }
    }

    /**
     * Parses list of values, be it for option or for command.
     *
     * @param context           information to pass to exception message, to be more user friendly
     * @param paramDeclarations the param declarations
     * @param actualValues      values to parse against declarations; this list will be shortened according to paramDeclarations
     * @return list of unmarshalled values
     * @throws java.text.ParseException -
     */
    public static List<Object> parseParamList(String context, List<ParamDeclaration> paramDeclarations, LinkedList<String> actualValues) throws ParseException {
        // parse remaining tokens as the public constructor's params
        final List<Object> unmarshalledValues = new ArrayList<Object>();
        for (ParamDeclaration paramDecl : paramDeclarations) {
            final Class paramType = paramDecl.getType();
            if (paramDecl.isVarArgs()) {
                final List<Object> list = new ArrayList<Object>();
                while (!actualValues.isEmpty()) {
                    final String value = actualValues.removeFirst();
                    if ("--".equals(value)) break; // this allows multiple varargs separated by "--" token
                    list.add(paramDecl.stringToType(value, paramType.getComponentType()));
                }
                final Object array = Array.newInstance(paramType.getComponentType(), list.size());
                int i = 0;
                for (Object item : list) {
                    Array.set(array, i, item);
                    i++;
                }
                unmarshalledValues.add(array);
//                unmarshalledValues.add(list.toArray((Object[]) array));
            } else if (actualValues.isEmpty()) {
                throw new ParseException(context + ": not enough parameters provided", 0);
            } else if (paramType.isArray()) {
                final Class componentType = paramType.getComponentType();
                final String values = actualValues.removeFirst();
                final List<Object> list = new ArrayList<Object>();
                for (StringTokenizer tok = new StringTokenizer(values, paramDecl.getListSeparator()); tok.hasMoreTokens();) {
                    final String value = tok.nextToken();
                    list.add(paramDecl.stringToType(value, componentType));
                }
                final Object array = Array.newInstance(componentType, list.size());
                unmarshalledValues.add(list.toArray((Object[]) array));
            } else {
                final String value = actualValues.removeFirst();
                unmarshalledValues.add(paramDecl.stringToType(value, paramType));
            }
        }
        return unmarshalledValues;
    }

    public String getSymbolicName() {
        return symbolicName == null ? defaultSymName() : symbolicName;
    }

    public void setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
    }

    @Override
    public String toString() {
        return symbolicName;
    }
}
