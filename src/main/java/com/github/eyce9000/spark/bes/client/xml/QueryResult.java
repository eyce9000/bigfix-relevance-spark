package com.github.eyce9000.spark.bes.client.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;


@XmlType(name = "Query")
@XmlRootElement(name="BESAPI")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryResult {
	@XmlPath("Query/@Resource")
	private String query;

	@XmlPath("Query/Result/Tuple")
    protected List<ResultTuple> results = new ArrayList<ResultTuple>();
	
	@XmlPath("Query/Result/Answer")
	@XmlJavaTypeAdapter(value=ResultAnswerAdapter.class)
	protected List<Object> singleResultAnswer = new ArrayList<Object>();
	
	@XmlPath("Query/Evaluation")
	private QueryEvaluation evaluation;

	@XmlPath("Query/Error/text()")
	private String error;
	
	public String getQuery() {
		return query;
	}

	public List<ResultTuple> getPluralResults() {
		return results;
	}
	public List<Object> getSingleResults(){
		return singleResultAnswer;
	}
	public QueryEvaluation getEvaluation() {
		return evaluation;
	}

	public String getError() {
		return error;
	}
}
