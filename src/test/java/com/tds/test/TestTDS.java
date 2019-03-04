package com.tds.test;

public class TestTDS {
	
	public static void main(String[] args){
		String bin = "4";
		int length = 16;
		int numberOfCardRanges = 1000;
		int differenceInCardRanges = 100;
		int randomNumberLength = length - (bin.length() + 1);

        int digit = 0;
        while(digit < numberOfCardRanges*differenceInCardRanges){
        	
        	StringBuilder builder = new StringBuilder(bin);
        	String defaultVal = "0";
        	String digitLength = digit+"";
	        for (int i = 0; i < (randomNumberLength - digitLength.length()+1); i++) {
	            builder.append(defaultVal);		           
	        }
	        if(digit % differenceInCardRanges == 0){
		        builder.append(digit);
		        digit = digit+1;
		        System.out.println("start card range : "+builder);
	        }else if(digit % differenceInCardRanges == (differenceInCardRanges-1)){
	        	builder.append(digit);
				digit = digit+1;
				 System.out.println("end card range : "+builder);
	        }else{
	        	digit = digit+1;
	        }
        }
	}

}
