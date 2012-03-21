package org.myrest.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.ScriptEvaluator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * 
 * This class holds the Rest Mapping Schema, Class, object instance and method
 * as defined in the mapping<br/>
 * [path mapping] [class].[method]<br/>
 * 
 * The controller class method should have an equal amount of String arguments
 * as variables in the path mapping.<br/>
 * Only String arguments are supported for now.<br/>
 * The method must return an instance of Writeable.<br/>
 * 
 */
public class RestPathMappingContainer {

	private static final Pattern splitPattern = Pattern.compile("[ ]+");
	private static final Pattern splitSlashPattern = Pattern.compile("/");

	final RestPathSchema schema;
	final Class<?> mappingClass;
	final Object callingInstance;
	final Method callingMethod;

	final ScriptEvaluator eval;

	final boolean hasHttpRequest;
	final int varLen;

	public RestPathMappingContainer(String mappingLine)
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			CompileException, ParseException, ScanException {
		final String[] mapping = splitPattern.split(mappingLine);

		if (mapping.length != 2)
			throw new RuntimeException(
					"Mapping line must be <path> <class>.<method>");

		final String classMethodStr = mapping[1];
		final int lastDotIndex = classMethodStr.lastIndexOf('.');

		if (lastDotIndex < 1)
			throw new RuntimeException(
					"Mapping line must be <path> <class>.<method>");

		final String className = classMethodStr.substring(0, lastDotIndex);
		final String methodName = classMethodStr.substring(lastDotIndex + 1,
				classMethodStr.length());

		schema = RestPathParser.parseSchema(mapping[0]);
		varLen = schema.getVars().length;

		Class<?>[] parameterTypes = new Class<?>[varLen];

		// find class and method
		mappingClass = Thread.currentThread().getContextClassLoader()
				.loadClass(className);
		Method callingMethod;
		boolean hasHttpRequest;
		ScriptEvaluator eval;

		try {
			StringBuilder script = new StringBuilder(100);
			script.append("try{");
			script.append("return ").append(className).append(".")
					.append(methodName).append("(");

			for (int i = 0; i < varLen; i++) {
				parameterTypes[i] = String.class;

				if (i != 0)
					script.append(",");

				script.append("vars[" + i + "].getValue(split)");
			}

			script.append(");");
			script.append("}catch(Throwable t){ RuntimeException rte = new RuntimeException(t.toString(), t); rte.setStackTrace(t.getStackTrace()); throw rte;}");
			callingMethod = mappingClass.getMethod(methodName, parameterTypes);

			hasHttpRequest = false;

			eval = new ScriptEvaluator(script.toString(), HttpResponse.class,
					new String[] { "httprequest", "split", "vars" },
					new Class<?>[] { HttpRequest.class, String[].class,
							RestPathVar[].class });
			eval.setParentClassLoader(Thread.currentThread()
					.getContextClassLoader());

		} catch (NoSuchMethodException method) {

			StringBuilder script = new StringBuilder(100);
			script.append("try{");
			script.append("return ").append(className).append(".")
					.append(methodName).append("(");
			script.append("httprequest");
			
			parameterTypes = new Class<?>[varLen + 1];

			parameterTypes[0] = HttpRequest.class;

			for (int i = 0; i < varLen; i++) {
				parameterTypes[i + 1] = String.class;
				script.append(", vars[" + i + "].getValue(split)");
			}
			script.append(");");
			script.append("}catch(Throwable t){ RuntimeException rte = new RuntimeException(t.toString(), t); rte.setStackTrace(t.getStackTrace()); throw rte;}");
			
			callingMethod = mappingClass.getMethod(methodName, parameterTypes);
			hasHttpRequest = true;

			eval = new ScriptEvaluator(script.toString(), HttpResponse.class,
					new String[] { "httprequest", "split", "vars" },
					new Class<?>[] { HttpRequest.class, String[].class,
							RestPathVar[].class });

		}

		this.hasHttpRequest = hasHttpRequest;
		this.callingMethod = callingMethod;
		this.eval = eval;

		callingInstance = mappingClass.newInstance();

	}

	public boolean matches(String path) {
		return schema.matches(path);
	}

	/**
	 * Invokes the mapped method from the given path i.e. the path is parse,
	 * then the variable values are extracted, and these values are used to
	 * invoke the controller method.
	 * 
	 * @param path
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public HttpResponse call(HttpRequest request, String path)
			throws InvocationTargetException {

		final RestPathVar[] vars = schema.getVars();

		final String[] split = (path.startsWith("/")) ? splitSlashPattern
				.split(path.subSequence(1, path.length())) : splitSlashPattern
				.split(path);

		return (HttpResponse) eval
				.evaluate(new Object[] { request, split, vars });
	}

}
