package org.myrest.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.ScriptEvaluator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

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

	final RestPathSchema schema;
	final Class<?> mappingClass;
	final Method callingMethod;

	final ScriptEvaluator eval;

	final boolean hasHttpRequest;
	final int varLen;

	final String className;

	final ControllerFactory controllerFactory;

	public RestPathMappingContainer(String mappingLine) throws Exception {
		this(mappingLine, null);
	}

	public RestPathMappingContainer(String mappingLine,
			ControllerFactory controllerFactory) throws Exception {
		final String[] mapping = StringUtils.split(mappingLine);

		if (mapping.length != 2)
			throw new RuntimeException(
					"Mapping line must be <path> <class>.<method>");

		final String classMethodStr = mapping[1];
		final int lastDotIndex = classMethodStr.lastIndexOf('.');

		if (lastDotIndex < 1)
			throw new RuntimeException(
					"Mapping line must be <path> <class>.<method>");

		className = classMethodStr.substring(0, lastDotIndex);

		// if the controllerFactory is null we instantiate a
		// SingletonControllerFactory
		controllerFactory = (controllerFactory == null) ? new SingletonControllerFactory(
				Thread.currentThread().getContextClassLoader()
						.loadClass(className).newInstance())
				: controllerFactory;

		this.controllerFactory = controllerFactory;

		final String methodName = classMethodStr.substring(lastDotIndex + 1,
				classMethodStr.length());

		schema = RestPathParser.parseSchema(mapping[0]);
		varLen = schema.getVars().length;

		final String callingStr;

		mappingClass = controllerFactory.newInstance(className).getClass();
		callingStr = "((" + className
				+ ")controllerFactory.newInstance(className))";

		Method callingMethod;
		boolean hasHttpRequest;

		try {

			Class<?>[] parameterTypes = new Class<?>[varLen];
			Arrays.fill(parameterTypes, String.class);

			hasHttpRequest = false;
			callingMethod = mappingClass.getMethod(methodName, parameterTypes);

		} catch (NoSuchMethodException method) {

			Class<?>[] parameterTypes = new Class<?>[varLen + 1];
			Arrays.fill(parameterTypes, String.class);
			parameterTypes[0] = HttpRequest.class;

			callingMethod = mappingClass.getMethod(methodName, parameterTypes);
			hasHttpRequest = true;
		}

		this.eval = createScriptEval(createScript(callingStr, methodName,
				varLen, hasHttpRequest));

		this.hasHttpRequest = hasHttpRequest;
		this.callingMethod = callingMethod;

	}

	private static final String createScript(String callingStr,
			String methodName, int varLen, boolean addRequest) {
		StringBuilder script = new StringBuilder(100);
		script.append("try{");
		script.append("return ").append(callingStr).append(".")
				.append(methodName).append("(");

		if (addRequest) {
			script.append("httprequest");
			if (varLen > 0)
				script.append(",");
		}

		for (int i = 0; i < varLen; i++) {
			if (i != 0)
				script.append(",");

			script.append("vars[" + i + "].getValue(split)");
		}

		script.append(");");
		script.append("}catch(Throwable t){ RuntimeException rte = new RuntimeException(t.toString(), t); rte.setStackTrace(t.getStackTrace()); throw rte;}");

		return script.toString();
	}

	private static final ScriptEvaluator createScriptEval(String script)
			throws CompileException, ParseException, ScanException {
		ScriptEvaluator eval = new ScriptEvaluator(script, HttpResponse.class,
				new String[] { "httprequest", "split", "vars", "className",
						"controllerFactory" }, new Class<?>[] {
						HttpRequest.class, String[].class, RestPathVar[].class,
						String.class, ControllerFactory.class });
		eval.setParentClassLoader(Thread.currentThread()
				.getContextClassLoader());
		return eval;
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
	public final HttpResponse call(HttpRequest request, String path)
			throws InvocationTargetException {

		final RestPathVar[] vars = schema.getVars();

		
		final int queryIndex = path.indexOf('?');
		final String parsePath = (queryIndex > -1) ? path.substring(0, queryIndex) : path;
		
		
		final String[] split = (parsePath.startsWith("/")) ? StringUtils
				.split(parsePath.substring(1, parsePath.length()), '/') : StringUtils
				.split(parsePath, '/');

		return (HttpResponse) eval.evaluate(new Object[] { request, split,
				vars, className, controllerFactory });
	}

	/**
	 * 
	 * Returns the same instance for the controller
	 * 
	 */
	static final class SingletonControllerFactory implements ControllerFactory {

		final Object instance;

		public SingletonControllerFactory(Object instance) {
			super();
			this.instance = instance;
		}

		public Object newInstance(String controllerName) {
			return instance;
		}

	}

}
