package groovyx.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.join;

/**
 * Base script that provides JCommander declarative (annotation-based) argument processing for scripts.
 *
 * @author Jim White
 */

abstract public class JCommanderScript extends Script {
    /**
     * Name of the property that holds the JCommander for this script (i.e. 'scriptJCommander').
     */
    public final static String SCRIPT_JCOMMANDER = "scriptJCommander";

    /**
     * The script body
     * @return The result of the script evaluation.
     */
    protected abstract Object runScriptBody();

    @Override
    public Object run() {
        String[] args = getScriptArguments();
        JCommander jc = getScriptJCommanderWithInit();
        try {
            parseScriptArguments(jc, args);
            runScriptCommand(jc);
            return runScriptBody();
        } catch (ParameterException pe) {
            return handleParameterException(jc, args, pe);
        }
    }

    public String[] getScriptArguments() {
        return (String[]) getProperty("args");
    }

    /**
     * Return the JCommander for this script.
     * If there isn't one already, then create it and set the default command name from the script's class name.
     *
     * @return the JCommander for this script.
     */
    public JCommander getScriptJCommanderWithInit() {
        try {
            JCommander jc = (JCommander) getProperty(SCRIPT_JCOMMANDER);
            if (jc == null) {
                jc = createScriptJCommander();
                // The script has a real property (a field or getter) but
                // if we let Script.setProperty handle this then it just gets stuffed into the bindings.
                this.getMetaClass().setProperty(this, SCRIPT_JCOMMANDER, jc);
            }
            return jc;
        } catch (MissingPropertyException mpe) {
            setProperty(SCRIPT_JCOMMANDER, createScriptJCommander());
            return (JCommander) getBinding().getProperty(SCRIPT_JCOMMANDER);
        }
    }

    public JCommander createScriptJCommander() {
        JCommander jc = new JCommander(this);
        jc.setProgramName(this.getClass().getSimpleName());

        initializeJCommanderCommands(jc);

        return jc;
    }

    public void initializeJCommanderCommands(JCommander jc) {
        Class cls = this.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(Subcommand.class);
                if (annotation != null) {
                    try {
                        field.setAccessible(true);
                        jc.addCommand(field.get(this));
                    } catch (IllegalAccessException e) {
                        printErrorMessage("Trying to add JCommanderCommand but got error '" + e.getMessage() + "' when getting value of field " + field.getName());
                    }
                }
            }

            cls = cls.getSuperclass();
        }
    }

    public void parseScriptArguments(JCommander jc, String[] args) {
        jc.parse(args);
    }

    /**
     * If there are any objects implementing Runnable that are part of this command script,
     * then run them.  If there is a parsed command, then run those objects after the main command objects.
     * Note that this will not run the main script though, we leave that for run to do (which will happen
     * normally since groovy.lang.Script doesn't implement java.lang.Runnable).
     *
     * @param jc
     */
    public void runScriptCommand(JCommander jc) {
        List<Object> objects = jc.getObjects();

        String parsedCommand = jc.getParsedCommand();
        if (parsedCommand != null) {
            JCommander commandCommander = jc.getCommands().get(parsedCommand);
            objects.addAll(commandCommander.getObjects());
        }

        for (Object commandObject : objects) {
            if (commandObject instanceof Runnable) {
                Runnable runnableCommand = (Runnable) commandObject;
                if ((Object) runnableCommand != (Object) this) {
                    runnableCommand.run();
                }
            }
        }
    }

    public void printErrorMessage(String message) {
        println(message);
    }

    public Object handleParameterException(JCommander jc, String[] args, ParameterException pe) {
        StringBuilder sb = new StringBuilder();

        sb.append(pe.getMessage());
        sb.append("\n");

        sb.append("args: [");
        sb.append(join(args, ", "));
        sb.append("]");
        sb.append("\n");

        jc.usage(sb);

        printErrorMessage(sb.toString());

        return -1;
    }

}
