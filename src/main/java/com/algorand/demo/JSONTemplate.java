package com.algorand.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import org.isda.cdm.*;
import com.rosetta.model.lib.RosettaModelObject;
import com.rosetta.model.lib.meta.RosettaMetaData;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.lang.reflect.Modifier;
import org.reflections.Reflections;


/**
Given a class path in the ISDA CDM, we will output a pretty nested JSON string of that model. 
*/
public class JSONTemplate {

    // Instantiate the pre-configured Rosetta ObjectMapper
	private static ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getDefaultRosettaObjectMapper();

    //build with:
    //mvn -s settings.xml clean install
    //Run this using:
    //mvn -s settings.xml  exec:java -Dexec.mainClass="com.regnosys.cdm.example.JSONTemplate" -Dexec.cleanupDaemonThreads=false
    public static void main(String[] args) {

    	//run an example
    	String classPath = "org.isda.cdm.PackageInformation";
    	try {
	    	String json = toJSONTemplate(classPath);

	        Class<?> classObject = null;

	        classObject = Class.forName(classPath);

	    	Object deserializedObject = rosettaObjectMapper.readValue(json, classObject);
			System.out.println("Deserialize to Java");
			System.out.println(deserializedObject.toString());
		} catch (ClassNotFoundException e) {
			System.err.println(classPath + " is not in the ISDA CDM java package");
			e.printStackTrace();
		} catch (ClassCastException e) {
			System.err.println("A field in the java object has a type that can't be cast as a Class");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("read/write exception with rosettaObjectMapper");
			e.printStackTrace();			
		} 
    }

    public static ArrayList getCDMClassPaths() {
    	ArrayList<String> classpaths = new ArrayList<String>();
        Reflections reflections = new Reflections("org.isda.cdm");
        Set<Class<? extends RosettaModelObject>> subTypes = reflections.getSubTypesOf(RosettaModelObject.class);
        for (Class<? extends RosettaModelObject> rosettaModelObjectClass : subTypes) {
        	String classPath = rosettaModelObjectClass.getName();
        	Class<?> classObject = null;
	        try {
	        	classObject = Class.forName(classPath);
	        }
			catch (ClassNotFoundException e) {
				System.err.println(classPath + " not found");
				e.printStackTrace();
				continue;
			}
			if(Modifier.isAbstract( classObject.getModifiers() ) || classObject.isInterface()) {
				continue;
			}
			classpaths.add(classPath);
		}
        return classpaths;
    }

    /**
     * Takes an ISDA CDM java class path as input. Fills in the fields of the java object with default values. A corner case is 
     * when the object is infinitely recursvie, we set the field to null when the same class is repeated in the path to that field. 
     * The filled-in java object is then serialized to JSON and returned. 
     *
     * @param classPath the ISDA CDM Java class path for the RosettaModelObject
     * @return a pretty nested JSON string of a mostly-filled in RosettaModelObject
	 * @throws ClassNotFoundException when the classPath is not in the ISDA CDM java package
	 * @throws ClassCastException when a field in the java object has a type that can't be cast as a Class
	 * @throws IOException read/write exception with rosettaObjectMapper
	 * @throws JsonProcessingException if it is unable to parse a JSON from the given value
	 * 
     */
	public static String toJSONTemplate(String classPath) throws ClassNotFoundException, ClassCastException, IOException, JsonProcessingException {

        Class<?> classObject = null;

        // get the class object from the path string
        classObject = Class.forName(classPath);

        // load Rosetta Object from class
        Object model = rosettaObjectMapper.readValue("{}", classObject);

        // initialize a set to check if we ever come to the same class in a nested path to a field
		Set<Class<?>> parentClasses = new HashSet<>();

		// fill in the object
        instantiateFields(model, parentClasses);

        // serialise JSON
        String json = rosettaObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);

        return json;
	}    


    private static Object getDefaultValue(Class<?> clazz) {
    	Object instance = null;

    	// if the clazz is any of the following data types, return a default value accordingly. Otherwise, return
    	// null

	    if (RosettaModelObject.class.isAssignableFrom(clazz) || RosettaMetaData.class.isAssignableFrom(clazz)) {
	    	try {
	    		instance = rosettaObjectMapper.readValue("{}", clazz);
	    	}
	    	catch (IOException e) {
	    		System.err.println("Could not serialise class " + clazz.getName() + " to JSON");
	    		e.printStackTrace();
	    	}
	    }
	    else if (List.class.isAssignableFrom(clazz)) {
	    	clazz = ArrayList.class;
	    	try {
	    		instance = clazz.newInstance();
	    	}
	    	catch (InstantiationException e) {
	    		System.err.println("Could not instantiate ArrayList");
	    		e.printStackTrace();
	    	}
	    	catch (IllegalAccessException e) {
	    		System.err.println("Could not instantiate ArrayList");
	    		e.printStackTrace();	    		
	    	}
	    }
	    else if (Boolean.class.isAssignableFrom(clazz)) {
    		instance = false;
    	}
    	else if (BigDecimal.class.isAssignableFrom(clazz)) {
    		instance = BigDecimal.ZERO;
    	}
    	else if (BigInteger.class.isAssignableFrom(clazz)) {
    		instance = BigInteger.ZERO;
    	}
    	else if (Number.class.isAssignableFrom(clazz)) {
    		instance = 0;
    	}
    	else if (String.class.isAssignableFrom(clazz)) {
    		instance = "";
    	}
    	else if (Enum.class.isAssignableFrom(clazz)) {
    		instance = clazz.getEnumConstants()[0];
    	}
    	else if (LocalTime.class.isAssignableFrom(clazz)) {
    		instance = LocalTime.MIDNIGHT;
    	}
    	else if (LocalDate.class.isAssignableFrom(clazz)) {
    		instance = LocalDate.now();
    	}
    	else if (ZonedDateTime.class.isAssignableFrom(clazz)) {
    		instance = ZonedDateTime.now();
    	}
    	else {
    		System.out.println("SETTING TO NULL: " + clazz.getName());
    	}

	    return instance;
    }

    private static void instantiateFields(Object o, Set<Class<?>> parentClasses) throws ClassCastException {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
        	try {
        		// Attempt to modify the field if it is set to null
	        	field.setAccessible(true);
	        	if (field.get(o) == null) {

	        		// Get a default value to set the field to based on the field type
		            Type type = field.getType();
	                Class<?> clazz = (Class<?>) type;
	                Object instance = getDefaultValue(clazz);

	                // If the field is a list, we have to fill in the fields of its element type
	                if (List.class.isAssignableFrom(clazz)) {
	                    instantiateList(clazz, field, instance, parentClasses);
	                }

	                // set the field to your default value
	                field.set(o, instance); 

	                // recur on the field if its a Rosetta Object and if you have not seen this class before on the path
	                // to this field. This prevent infinite recursion.
	            	if (!parentClasses.contains(clazz) && 
	            		(RosettaModelObject.class.isAssignableFrom(clazz) || RosettaMetaData.class.isAssignableFrom(clazz))) {
	                	Set<Class<?>> newParentClasses = new HashSet<>();
	                	newParentClasses.addAll(parentClasses);
	                	newParentClasses.add(clazz);
	                	instantiateFields(instance, newParentClasses);
	                }
		        }
		    }
		    catch (IllegalAccessException e) {
		    	System.err.println("Cannot set field.");
		    	e.printStackTrace();
		    }
        }
    }

    private static void instantiateList(Class<?> clazz, Field field, Object instance, Set<Class<?>> parentClasses) {
    	// Get the list's element type
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
        Object listTypeInstance = getDefaultValue(listClass);

        // Fill in an element
    	if (RosettaModelObject.class.isAssignableFrom(listClass) || RosettaMetaData.class.isAssignableFrom(listClass)) {
			Set<Class<?>> newParentClasses = new HashSet<>();
			newParentClasses.addAll(parentClasses);
			newParentClasses.add(listClass);
	        instantiateFields(listTypeInstance, newParentClasses);
        }

        // Add the element to the list
        List<Object> list = (List<Object>) instance;
        list.add(listTypeInstance);
    }
}