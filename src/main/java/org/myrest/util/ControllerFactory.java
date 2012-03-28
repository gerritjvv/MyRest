package org.myrest.util;

/**
 * 
 * Abstracts away how the controllers are instantiated, if at all.
 *
 */
public interface ControllerFactory {

	/**
	 * 
	 * @param controllerName String this is the name specified in the RestPathMapping
	 * @return Object
	 */
	Object newInstance(String controllerName);
	
}
