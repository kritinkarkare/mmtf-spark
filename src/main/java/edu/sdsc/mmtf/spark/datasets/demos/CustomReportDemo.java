/**
 * 
 */
package edu.sdsc.mmtf.spark.datasets.demos;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import edu.sdsc.mmtf.spark.datasets.CustomReportService;

/**
 * This demo shows how to create and query a dataset.
 * The dataset in this case is generated by running an
 * RCSB PDB web service to create a custom report of
 * PDB annotations.
 * 
 * @author Peter Rose
 *
 */
public class CustomReportDemo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {    
	    long start = System.nanoTime();
	    
	    SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(CustomReportDemo.class.getSimpleName());
	    JavaSparkContext sc = new JavaSparkContext(conf);
	   
	    // retrieve PDB annotation: Binding affinities (Ki, Kd), 
	    // group name of the ligand (hetId), and the 
	    // Enzyme Classification number (ecNo)
	    Dataset<Row> ds = CustomReportService.getDataset("Ki","Kd","hetId","ecNo");
	    
	    // show the schema of this dataset
	    ds.printSchema();
	        
	    // select structures that either have a Ki or Kd value(s) and
	    // are protein-serine/threonine kinases (EC 2.7.1.*):
	    
	    // A. by using dataset operations
	    ds = ds.filter("(Ki IS NOT NULL OR Kd IS NOT NULL) AND ecNo LIKE '2.7.11.%'");
	    ds.show(10);
	     
	    // B. by creating a temporary query and running SQL
	    ds.createOrReplaceTempView("table");
	    ds.sparkSession().sql("SELECT * from table WHERE (Ki IS NOT NULL OR Kd IS NOT NULL) AND ecNo LIKE '2.7.11.%'");
	    ds.show(10);
	    
	    long end = System.nanoTime();
	    
	    System.out.println("Time:     " + (end-start)/1E9 + "sec.");
	    
	    sc.close();
	}
}