/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.amazon.advertising.api.sample;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "YOUR_ACCESS_KEY_ID_HERE";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "YOUR_SECRET_KEY_HERE";

    /*
     * Your Affiliate ID.
     */
    private static final String AWS_AFFILIATE_ID = "YOUR_AFFILIATE_ID";
    
    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private static final String ENDPOINT = "ecs.amazonaws.jp";

    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    private static final String ITEM_ID = "4048691899";

    /*
     * DocumentBuilder object for parse DOM of amazon API's return.
     */
    private static final DocumentBuilder db = getDocumentBuilder();
    
    public static void main(String[] args) {
    	/* get and print item's information. case of form method is POST. */
    	Map<String, String> info = getItemBasicInformation(ITEM_ID, "POST");
        System.out.println("Title is \"" + info.get("Title") + "\"");
        System.out.println("Author is \"" + info.get("Author") + "\"");
        System.out.println();
        
        /* get and print item's information. case of form method is GET. */
    	info = getItemBasicInformation(ITEM_ID, "GET");
        System.out.println("Title is \"" + info.get("Title") + "\"");
        System.out.println("Author is \"" + info.get("Author") + "\"");
        System.out.println();
    }

    /*
     * get item information which specified by asin(ITEM_ID).
     * @param asin ITEM_ID which you want to get information
     * @param method form's request method. You can select "POST" or "GET". If you select "", this method select "POST". 
     * @return Map("AttributeName", "AttributeValue")　You can use values "Title" and "Author" as AtrributeName.
     */
    private static Map<String, String> getItemBasicInformation(String asin, String method) {
        /*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }

        if (method.equals("")) {
        	method = "POST";
        }
   
    	System.out.println("Method: " + method);
        String requestUrl = null;
        
        /* The helper can sign requests in two forms - map form and string form */
        
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        if (method == "POST") {
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("Service", "AWSECommerceService");
        	params.put("Version", "2009-03-31");
        	params.put("Operation", "ItemLookup");
        	params.put("ItemId", asin);
        	params.put("ResponseGroup", "Small");
        	params.put("AssociateTag", AWS_AFFILIATE_ID);

        	requestUrl = helper.sign(params);
        	System.out.println("Request is \"" + requestUrl + "\"");

        	Map<String, String> results = new HashMap<String, String>();
        	results.put("Title", fetchTitle(db, requestUrl));
        	results.put("Author", fetchAuthor(db, requestUrl));
        	return results;
        } else {
        /* Here is an example with string form, where the requests parameters have already been concatenated
         * into a query string.
         */
        	String queryString = "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Small&ItemId="
                + asin;
        	queryString += "&AssociateTag=" + AWS_AFFILIATE_ID;
        	requestUrl = helper.sign(queryString);
        	System.out.println("Request is \"" + requestUrl + "\"");

        	Map<String, String> results = new HashMap<String, String>();
        	results.put("Title", fetchTitle(db, requestUrl));
        	results.put("Author", fetchAuthor(db, requestUrl));
        	return results;
        }
    }
    
    /*
     * Utility function to generate DocumentBuiler object for parser Amazon API's response.
     */
    private static DocumentBuilder getDocumentBuilder() {
    	if (db != null) {
    		return db;
    	} else {
    		try {
    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    			DocumentBuilder db = dbf.newDocumentBuilder();
    			return db;
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
    }
    
    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(DocumentBuilder db, String requestUrl) {
        String title = null;
        try {
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            title = titleNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }

    /*
     * Utility function to fetch the response from the service and extract the
     * author from the XML.
     */
    private static String fetchAuthor(DocumentBuilder db, String requestUrl) {
        String author = null;
        try {
            Document doc = db.parse(requestUrl);
            Node authorNode = doc.getElementsByTagName("Author").item(0);
            author = authorNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return author;
    }
    
}
