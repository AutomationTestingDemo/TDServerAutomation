package com.ca.tds.utilityfiles;

import java.io.FileReader;
import java.io.IOException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtility {

	
	public static void validate(String jsonStr, String jsonSchemaPath) throws IOException, ParseException {

		FileReader reader = new FileReader(jsonSchemaPath); //resource/schema/areq/areq_schema.json
			JSONParser jsonParser = new JSONParser();
			
			org.json.JSONObject jsonData = new org.json.JSONObject(jsonStr);
			//System.out.println("REQUEST : "+jsonData);
			// Read JSON file
			Object schemaObj = jsonParser.parse(reader);
			org.json.JSONObject jsonSchema = new org.json.JSONObject(schemaObj.toString());
			//System.out.println("SCHEMA : "+jsonSchema);
			Schema schema = SchemaLoader.load(jsonSchema);
			schema.validate(jsonData);
			System.out.println("\n +++++++Json Schema Validated ++++++");
			
		
		

	}
}
