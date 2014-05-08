package groovyx.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import groovy.lang.Script;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.join;

/**
 *
 * @author <mailto:james.paul.white@gmail.com
 */
abstract public class JavaCommandScript extends Script {
    protected abstract Object runScriptBody();

    @Override
    public Object run() {
        String[] args = getScriptArguments();
        JCommander jc = createJCommander();
        try {
            parseScriptArguments(jc, args);
            return runScriptBody();
        } catch (ParameterException pe) {
            return handleParameterException(jc, args, pe);
        }
    }

    public String[] getScriptArguments() {
        return (String[]) getProperty("args");
    }

    public JCommander createJCommander() {
        JCommander jc = new JCommander(this);
        jc.setProgramName(this.getClass().getSimpleName());
        return jc;
    }

    public void parseScriptArguments(JCommander jc, String[] args) {
        jc.parse(args);
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
