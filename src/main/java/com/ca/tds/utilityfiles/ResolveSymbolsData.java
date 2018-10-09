package com.ca.tds.utilityfiles;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Strings;

/**
 * This class resolves the given symbols and gives the output with actual
 * generated values.
 * 
 * @author pansa09
 *
 */
public class ResolveSymbolsData {
	private static Map<String, String> symbolMap = new HashMap<String,String>();
	

	/**
	 * <h1>getSymbolsToReplaceWithActualData()</h1>
	 * <p>
	 * This method reads the input map 'values' and verifies the symbols
	 * ${NEW_CARD}, ${EXISTING_CARD}, ${PARTIALLY_REG_CARD}, ${CARD_FROM_OTHER}, ${CURRENT_DATE_MONTH_YEAR}
	 * ${CURRENT_DATE} and plus, ${CURRENT_DATE} and minus, ${CURRENT_DAY}, ${TODAY_DAY_NUMBER}
	 * ${CURRENT_MONTH} and plus, ${CURRENT_YEAR} and plus, ${CURRENT_TIME}, ${CURRENT_SEC}
	 * ${CURRENT_MINUTE}, ${CURRENT_HOUR}, ${RANDOM_STRING(16)}, ${RANDOM_STRING(16,1234)}
	 * ${RANDOM_LONG}, ${RANDOM_INT(10,20)}, ${RANDOM_DOUBLE}, ${RANDOM_DOUBLE(16.2,20.3)} If values
	 * contains the symbols will invoke respective API function to get actual
	 * data and will replace the value with symbol in input {@link Map}
	 * </p>
	 * <b>Few Scenarios:</b> 1) If 'Issuer' contains one ore more symbols instead of actual
	 * issuer name, then first resolving the symbols and passing that value to
	 * respective API function to get actual card number.<br>
	 * 2) Any key could contain one or more symbols.
	 * 
	 * Example scenarios:<br>
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * | K1           			| Issuer									|BeginRange	| EndRange  | K2													|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${NEW_CARD}				| HSBC										|44xxxxxx200|44xxxxxx400| ${CURRENT_MONTH}+2 ${CURRENT_DATE}+2 ${CURRENT_YEAR}	|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${EXISTING_CARD}		| ${RANDOM_STRING(16,134567)}				|45xxxxxx200|45xxxxxx400| ${CURRENT_DATE}										|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${PARTIALLY_REG_CARD}	| ${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}	|46xxxxxx200|46xxxxxx400| ${CURRENT_DATE_MONTH_YEAR}							|
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${CARD_FROM_OTHER}		| ${RANDOM_STRING(16,134567)}				|47xxxxxx200|47xxxxxx400| ${RANDOM_LONG}										|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * Result data for above scenario:
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * | K1           			| Issuer									|BeginRange	| EndRange  | K2													|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556622		| HSBC										|44xxxxxx200|44xxxxxx400| 03 13 2016											|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556600		| 1345763165771345							|45xxxxxx200|45xxxxxx400| 11													|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556611		| 11-01-2016 MONDAY							|46xxxxxx200|46xxxxxx400| 11-01-2016											|
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556633		| 1345763165771343							|47xxxxxx200|47xxxxxx400| 321423546547657556									|
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * @param inputMap {@link Map<String, String>} contains keys and values. The value can contain symbols like explained above
	 * @return Map<String,String>  the symbols will be resolved new map will be returned.
	 * @throws Exception if symbols cannot be resolved then exception is thrown
	 * 
	 */
	public Map<String, String> getSymbolsToReplaceWithActualData(Map<String, String> inputMap) throws Exception {
		Map<String, String> outputMap = new LinkedHashMap<String, String>();
		//System.out.println("Map Contents Before resolving symbols are : "+inputMap);
		Set<String> keys = inputMap.keySet();
		String[] keyArray =  keys.toArray(new String[keys.size()]);
		for(int i=0;i<keyArray.length;i++){
			String key= keyArray[i];
			String value = inputMap.get(key);  
			String outputValue = getResolvedSymbolData(value, inputMap);
			
			if(value==null || value.equalsIgnoreCase("${ReadMAPFromProperties}")){
				
				//outputMap = inputMap;
				for (Map.Entry<String,String> e : inputMap.entrySet()){
					//inputMap.putIfAbsent(e.getKey(), e.getValue());
					if (!outputMap.containsKey(e.getKey()))
						outputMap.put(e.getKey(), e.getValue());
				}
				//System.out.println("Reached out of forloop");
				
			}
			else{
				outputMap.put(key, outputValue);
			}
		}
		return outputMap;
	}	

	private String getResolvedSymbolData(String value,
			Map<String, String> inputMap) throws Exception {
		if (value == null || (!value.contains("$") || value.length() < 1)) {
			return value;
		} else { // contains $ symbol
			String[] tokens = getMoreSymbolsFromColumn(value);
			return getSymbolData(tokens, inputMap);
		}
	}

	/**
	 * This method splits the input value when more symbols present.
	 * 
	 * @param value is like "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}"
	 * @return Array of symbols
	 */
	private String[] getMoreSymbolsFromColumn(String value) {
		String[] tokens = null;
		if (value != null && value.contains("$"))
			tokens = value.split("\\$");

		return tokens;
	}
	/**
	 * This method returns the final output value of the given KEY when value
	 * contains one or more symbols.
	 * 
	 * @param tokens :list of symbols like "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}"
	 * @param inputMap 
	 * @return String of final value of specific key.
	 * @throws Exception
	 */
	private String getSymbolData(String[] tokens, Map<String, String> inputMap) throws Exception {
		StringBuilder tempString = new StringBuilder();
		for(String s : tokens){
			if(s.startsWith("{") && s.contains("}")){
				String actual= s.substring(s.indexOf("{"),s.indexOf("}")+1);
				tempString.append(resolveSymbol(actual, inputMap));
				System.out.println("actual string :"+actual);
				String remaining = s.substring(s.indexOf("}")+1, s.length());
				tempString.append(remaining);
			}else{
				tempString.append(s);
			}
		}
		//System.out.println("Builder value : " + tempString);
		return tempString.toString();
	}
	/**
	 * This method checks the input value symbol and invokes respective API
	 * function
	 * 
	 * @param value
	 *            is any symbol like ${NEW_CARD} or ${CURRENT_DATE} or
	 *            ${RANDOM_STRING(16,1234)}
	 * @param inputMap
	 *            contains keys and values
	 * @return String Resolved symbol data
	 * @throws Exception
	 */
	private String resolveSymbol(String value, Map<String, String> inputMap) throws Exception {
		String value2 = null;
		if (value.contains("{ReadMapFromProperties}")) {
			//loadMapFromProperties(inputMap);
		}
		return value2;
	}
	
	
	public String getRandomPhoneNumber(){
		String phoneNumber = null;
		long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
		phoneNumber = Long.toString(number);
		return phoneNumber;
	}
	
	public static String getRandom_TIME_STAMP_STR(String value) throws Exception {

		String generatedValue = null;

		if (symbolMap.containsKey(value)) {
			generatedValue = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = new Date();
			String str = dateFormat.format(date); 	
//			return str;
//			String s = RandomStringUtils.random(value.length(), value);
			symbolMap.put(value, str);
			generatedValue = symbolMap.get(value);

		}

		return generatedValue;
	}
	
	public static String getRandomVisaCardRange(String value) throws Exception {
		String generatedValue = null;
		if (symbolMap.containsKey(value)) {
			generatedValue = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = new Date();
			String str = dateFormat.format(date);
			str="400"+str.substring(4);
			symbolMap.put(value, str);
			generatedValue = symbolMap.get(value);
		}
		return generatedValue;
	}


	/**
	 * Based on input details invoking respective method to get 'NEW CARD'
	 * number. Both the scenarios Issuer is mandatory.
	 * 
	 * @param issuer
	 *            is either Issuer name or one/more symbols data like 'HSBC' or
	 *            "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}" or
	 *            ${RANDOM_STRING(16,134567)}.
	 * @param bRange
	 *            is Begin range of the card for issuer.
	 * @param eRange
	 *            is End range of the card for issuer.
	 * @return Card Number
	 * @throws Exception
	 */
	
	
	
	
}
