package world;

import java.util.HashMap;
import java.util.Map;

import util.Log;

public class VariableHolder {
	/**
	 * @uml.property  name="vals"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Object" qualifier="name:java.lang.String java.lang.Object"
	 */
	public Map<String, Object> vals = new HashMap<String, Object>();

	public void set(String name, Object val) {
		vals.put(name, val);
	}
	
	public Map<String, Object> getVars(){
		return vals;
	}
	
	public Object get(String name){
		return vals.get(name);
	}

}
