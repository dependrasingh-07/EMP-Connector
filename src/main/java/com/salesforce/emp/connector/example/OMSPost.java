package com.salesforce.emp.connector.example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.io.DataOutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

public class OMSPost {

	public static void main(String customer_id, String shared_key, String log_type, String json, String clientSessionChannel_Id, String flag) {

		String Signature = "";
		String encodedHash = "";
		String url = "";

 		ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss GMT");
        String nowStr = f.format(now);

		// String for signing the key
		String stringToSign="POST\n" + json.length() + "\napplication/json\nx-ms-date:" + nowStr + "\n/api/logs";

		System.out.println("\nDEBUG. stringToSign : " + stringToSign);
		System.out.println("\nDEBUG. message : "  + json);

		try {
			byte[] decodedBytes = Base64.decodeBase64(shared_key);

			Mac hasher = Mac.getInstance("HmacSHA256");
			hasher.init(new SecretKeySpec(decodedBytes, "HmacSHA256"));
			byte[] hash = hasher.doFinal(stringToSign.getBytes());
		    
			encodedHash = DatatypeConverter.printBase64Binary(hash);
			Signature = "SharedKey " + customer_id + ":" + encodedHash;
	    
			System.out.println("\nDEBUG. Signature : " + Signature);


			url = "https://" + customer_id + ".ods.opinsights.azure.com/api/logs?api-version=2016-04-01";	    
			URL objUrl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) objUrl.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Log-Type", log_type);
			con.setRequestProperty("x-ms-date", nowStr);
			con.setRequestProperty("Authorization", Signature);
	        
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(json);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("\nSalesforce channel ID : " + clientSessionChannel_Id);
			System.out.println("\nSalesforce message  status : " + flag);
			System.out.println("\nPost parameters : " + json);
			System.out.println("\nResponse Code : " + responseCode);
		}
		catch (Exception e) {
			System.out.println("Catch statement: " + e);
		}
	}
}