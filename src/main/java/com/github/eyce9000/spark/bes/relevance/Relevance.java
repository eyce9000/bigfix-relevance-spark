package com.github.eyce9000.spark.bes.relevance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relevance {
	
	static public String getCleanedQuery(String query){
		char[] cleanBuffer = query.toCharArray();
		//Find locations of strings

		Pattern p;
		Matcher m;
		
		p= Pattern.compile("\".*\"");
		m= p.matcher(query);
		
		char[] mask = new char[cleanBuffer.length];
		while(m.find()){
			for(int i=m.start(); i<m.end(); i++){
				mask[i] = 's';
			}
		}
		
		p = Pattern.compile("//.*");
		m = p.matcher(query);
		while(m.find()){
			if(mask[m.start()]!='s'){
				for(int i=m.start(); i<m.end(); i++){
					mask[i] = 'c';
				}
			}
		}
		
		p = Pattern.compile("\\s+");
		m = p.matcher(query);
		while(m.find()){
			if(mask[m.start()]!='s'){
				cleanBuffer[m.start()]=' ';
				for(int i=m.start()+1; i<m.end(); i++){
					mask[i]='c';
				}
			}
		}
		for(int i=0; i<mask.length; i++){
			if(mask[i]=='c'){
				cleanBuffer[i]='\n';
			}
		}
		
		String cleaned = new String(cleanBuffer);
		cleaned = cleaned.replace("\n", "");
		return cleaned;
	}
}
