package com.salesforce.emp.connector.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jetty.util.ajax.JSON;

public class OMSLoggingListener implements ClientSessionChannel.MessageListener {

    private boolean logSuccess;
    private boolean logFailure;
    private String  customer_id;
    private String  shared_key;
    private String  log_type;
    private String  json_log;
    

    public OMSLoggingListener() {
        this.logSuccess  = true;
        this.logFailure  = true;
        this.customer_id = "";
        this.shared_key  = "";
        this.log_type    = "";        
    }

    public OMSLoggingListener(boolean logSuccess, boolean logFailure, String customer_id, String shared_key, String log_type) {
        this.logSuccess  = logSuccess;
        this.logFailure  = logFailure;
        this.customer_id = customer_id;
        this.shared_key  = shared_key;
        this.log_type    = log_type;
    }

    @Override
    public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
        
        Consumer<Map<String, Object>> consumer = event -> { 
            String eventAsJson = JSON.toString(event);

            String clientSessionChannel_Id = "\"" + clientSessionChannel.getId() + "\"";
            String log = "\"[" + eventAsJson + "]\"";

            System.out.println("\nDEBUG. log : " + log);

            String[] inputArguments_s = {customer_id, shared_key, log_type, log, clientSessionChannel_Id, "Success"};
            String[] inputArguments_f = {customer_id, shared_key, log_type, log, clientSessionChannel_Id, "Failure"};   

            if (logSuccess && message.isSuccessful()) {
                OMSPost.main(inputArguments_s);
            }

            if (logFailure && !message.isSuccessful()) {
                OMSPost.main(inputArguments_f);
            }
        };
    }
}
