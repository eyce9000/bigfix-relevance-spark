package com.github.eyce9000.spark.bes.client.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.joda.time.DateTime;

import com.bigfix.schemas.relevance.ResultList;

public class WRResultParser {
	private JAXBContext jc;
	private Unmarshaller unmarshaller;
	private boolean columnsProcessed;
	public WRResultParser() throws JAXBException{

		jc = JAXBContext.newInstance(ResultList.class);
        unmarshaller = jc.createUnmarshaller();
	}
	
	public WRResult parse(InputStream in) throws XMLStreamException, JAXBException{
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
		XMLEventReader reader = factory.createXMLEventReader(new BufferedReader(new InputStreamReader(in)));

		
		boolean inResults = false;
		
		WRResult result = new WRResult();
		result.results = new ArrayList<>();
		
		while(reader.hasNext()){
			reader.nextEvent();
			XMLEvent event = reader.peek();
			if(event==null)
				break;
			if(event.isStartElement()){
				StartElement el = event.asStartElement();
				String localName = el.getName().getLocalPart();

				if(localName.equals("sessionToken")){
					JAXBElement<String> element = unmarshaller.unmarshal(reader,String.class);
					result.token = element.getValue();
				}
				if(localName.equals("faultstring")){
					JAXBElement<String> element = unmarshaller.unmarshal(reader,String.class);
					result.error = element.getValue();
					return result;
				}
				if(localName.equals("error")){
					JAXBElement<String> element = unmarshaller.unmarshal(reader,String.class);
					result.error = element.getValue();
					return result;
				}
				
				if(localName.equals("results")){
					inResults = true;
				}
				else if(inResults){
					result.results.add(processElement(el,reader));
				}
			}
			if(event.isEndElement()){
				EndElement el = event.asEndElement();
				String localName = el.getName().getLocalPart();
				if(localName.equals("results"))
					inResults = false;
				
			}
		}
		return result;
	}
	
	private List<Object> processElement(StartElement el,XMLEventReader reader) throws JAXBException{
		ResultList row = null;
		
		JAXBElement element = null;
		String localName = el.getName().getLocalPart();
		if(localName.equals("String"))
			element = unmarshaller.unmarshal(reader,String.class);
		else if(localName.equals("Boolean"))
			element = unmarshaller.unmarshal(reader,Boolean.class);
		else if(localName.equals("Integer"))
			element = unmarshaller.unmarshal(reader,Integer.class);
		else if(localName.equals("DateTime"))
			element = unmarshaller.unmarshal(reader,XMLGregorianCalendar.class);
		else if(localName.equals("FloatingPoint"))
			element = unmarshaller.unmarshal(reader,Double.class);
		else if(localName.equals("Tuple")){
			JAXBElement<ResultList> jb = unmarshaller.unmarshal(reader, ResultList.class);
			row = jb.getValue();
		}
		if(row==null){
			row = new ResultList();
			row.getBooleanOrIntegerOrString().add(element.getValue());
		}
		return processResultList(row);
	}
	
	private List<Object> processResultList(ResultList result){
		List<Object> processed = new ArrayList<Object>(result.getBooleanOrIntegerOrString().size());
		for(Object rawValue : result.getBooleanOrIntegerOrString()){
			Object value;
			if(rawValue instanceof BigDecimal)
				value = ((BigDecimal)rawValue).doubleValue();
			else if(rawValue instanceof BigInteger)
				value = ((BigInteger)rawValue).intValue();
			else if(rawValue instanceof XMLGregorianCalendar){
				value = new Timestamp(((XMLGregorianCalendar)rawValue).toGregorianCalendar().getTimeInMillis());
			}
			else
				value = rawValue;
			
			processed.add(value);
    	}
		return processed;
	}
	
	public class WRResult{
		String token;
		List<List<Object>> results;
		String error;
		public String getToken() {
			return token;
		}
		public List<List<Object>> getResults() {
			return results;
		}
		public String getError() {
			return error;
		}
	}
}
