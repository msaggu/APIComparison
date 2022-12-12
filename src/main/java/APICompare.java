import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;

public class APICompare {
	
	File file1;
	File file2;
	
	public APICompare(String filepath1, String filepath2) {
		// Constructor 
		this.file1 = new File(filepath1);
		this.file2 = new File(filepath2);
	}
	
	/**
	 * This method will get the LineIterators for the given 2 files
	 * and returns the same in a list 
	 */
	public List<LineIterator> getIterator() throws IOException {
		
		// check to see if the files exist
		if(!(file1.exists()) || !(file2.exists())) {
			System.out.println("Either or both file1 or file2 doesn't exist");
			return null;
		}
		
		// Iterator for File1
		LineIterator firstFileIterator = FileUtils.lineIterator(file1, "UTF-8");
		
		// Iterator for File2
		LineIterator secondFileIterator = FileUtils.lineIterator(file2, "UTF-8");
		
		// List to store the Iterators for the files in a list
		List<LineIterator> iteratorList = new ArrayList<LineIterator>();
		iteratorList.add(firstFileIterator);
		iteratorList.add(secondFileIterator);
		
		// Returning the list of Iterators
		return iteratorList;		
		
	}
	
	/**
	 * This method will perform GET requests on the url's, 
	 * compares the response from both the url requests,
	 * returns true if the responses match,
	 * otherwise returns false. 
	 */
	public Boolean compareURLs(String url1, String url2) {
		
		// check to see if the url line is blank 
		if(url1.isBlank() || url2.isBlank()) {
			System.out.println("Either url1 or url2 is empty and comparison cannot be done");
			return false;
		}
		
		Response response1 = RestAssured.get(url1);
		Response response2 = RestAssured.get(url2);
		
		Headers headerList1 = response1.getHeaders();
		Headers headerList2 = response2.getHeaders();
		
		// status code comparison
		if(response1.getStatusCode() != response2.getStatusCode()) {
			return false;
		}
		
		// response body comparison
		if(!response1.getBody().asString().equals(response2.getBody().asString())) {
			return false;
		}
		
		// content type comparison 
		if(!response1.getContentType().equals(response2.getContentType())) {
			return false;
		}
		
		// header comparison 
		if(headerList1.size() != headerList2.size()) {
			return false;
		}
		
		return true;
	}

	public static void main(String[] args) throws IOException {
				
		APICompare compare = new APICompare("File1.txt", "File2.txt");
		List<LineIterator> list = compare.getIterator();
		
		LineIterator firstFile = list.get(0);
		LineIterator secondFile = list.get(1);
		
		try {		
			while(firstFile.hasNext() || secondFile.hasNext()) {
				String url1 = firstFile.next();
				String url2 = secondFile.next();
				
				Boolean compareResult = compare.compareURLs(url1, url2);
				
				if (compareResult) {
					System.out.println(url1 + " equals " + url2);
				}
				else{
					System.out.println(url1 + " not equals " + url2);
				}				
			}				
		} 
		
		catch(NoSuchElementException e) {
			System.out.println("Number of lines in File1 is not equal to File2");
		}
		
		finally {
			firstFile.close();
			secondFile.close();
		}
		
	} 
}
		

