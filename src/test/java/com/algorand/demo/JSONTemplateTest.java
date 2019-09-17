package com.algorand.demo;

import static org.junit.Assert.assertTrue;
import org.reflections.Reflections;
import com.rosetta.model.lib.RosettaModelObject;
import java.util.Set;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class JSONTemplateTest 
{
    private static ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getDefaultRosettaObjectMapper();

    @Test
    public void EveryRosettaModelObjectSerializeDeserializeTest()
    {
        Reflections reflections = new Reflections("org.isda.cdm");
        Set<Class<? extends RosettaModelObject>> subTypes = reflections.getSubTypesOf(RosettaModelObject.class);
        for (Class<? extends RosettaModelObject> rosettaModelObjectClass : subTypes) {
        	String classPath = rosettaModelObjectClass.getName();
        	System.out.println("Class: " + classPath);
        	Class<?> classObject = null;
	        try {
	        	classObject = Class.forName(classPath);
	        }
			catch (ClassNotFoundException e) {
				System.err.println(classPath + " not found");
				e.printStackTrace();
				assertTrue(false);
			}
			if(Modifier.isAbstract( classObject.getModifiers() ) || classObject.isInterface()) {
				continue;
			}
			try {
				String json = JSONTemplate.toJSONTemplate(classPath);
	        	Object deserializedObject = rosettaObjectMapper.readValue(json, classObject);
			}
			catch (Exception e) {
				System.err.println("Exception occured trying to serialise/deserialise model: " + classPath);
				e.printStackTrace();
				assertTrue(false);
			}
        }
        assertTrue(true);
    }

}
