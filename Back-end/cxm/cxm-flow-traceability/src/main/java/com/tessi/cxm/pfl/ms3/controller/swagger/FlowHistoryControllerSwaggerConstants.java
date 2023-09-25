package com.tessi.cxm.pfl.ms3.controller.swagger;

public class FlowHistoryControllerSwaggerConstants {
    public static final String ADD_FLOW_HISTORY_SUMMARY = "Add a new flow history.";
    public static final String ADD_FLOW_HISTORY_DESCRIPTION = "Add a new flow history. Request with a flow id to create the history of particular flow. This request will automatically map the status of history corresponds to the current flow status. <br><br> **If the flow status is:** <br><br>\"In Creation\" : date/time of deposit <br>=> Corresponds to the \"Submitted\" status in the event history <br><br>\"Validated\" : date/time of finalization <br>=> Corresponds to the \"Finalized\" status in the event history <br><br>\"Scheduled\" : date/time when the flow is scheduled <br>=> Corresponds to the status \"Waiting tobe processed\" in the event history <br><br>\"Progress\" : date/time when the first document in the flow was processed <br>=> Corresponds to the status \"In process\" in the event history<br><br>\"Completed\" : date/time when the last document was sent<br>=> Corresponds to the \"Processed\" status in the event history<br><br>\"Cancelled\" : date/time of the cancellation <br>=>Corresponds to the status\" Cancelled \" of the history of the events<br><br>\"In error\" : date/time of the error <br>=> corresponds to the status \"In error\" in the event history <br><br>";
    public static final String ADD_FLOW_HISTORY_REQUEST_BODY_DESCRIPTION = "Flow history request body.";
    public static final String ADD_FLOW_HISTORY_RESPONSE_201_DESCRIPTION = "Flow history success created response.";
    // Find all
    public static final String FIND_ALL_FLOW_HISTORY_SUMMARY = "Find all flow histories.";
    public static final String FIND_ALL_FLOW_HISTORY_DESCRIPTION = "Find all flow histories with pagination. Requests with provided body example. This will return back an object that consist of an array which contains multiple flow histories as well as requested page, pagesize and total number of histories. <br> <br> There are several optional filter fields that we can pass as parameter which allow us to get the histories base on specific criteria. See the parameter below.";
    public static final String FIND_ALL_FLOW_HISTORY_REQUEST_PARAMS_FILTER_DESCRIPTION = "Filter.";
    public static final String FIND_ALL_FLOW_HISTORY_RESPONSE_200_DESCRIPTION = "Find all flow histories success response.";
    // Get Flow history by id
    public static final String GET_FLOW_HISTORY_BY_ID_DESCRIPTION = "Get flow history by id. Request with a flow history id as the parameter. This will return an object of a flow history.";
    public static final String GET_FLOW_HISTORY_BY_ID_RESPONSE_200_DESCRIPTION = "Get flow history success response.";
    // Get Flow's histories by Flow's id
    public static final String GET_FLOW_HISTORIES_BY_FLOW_ID_DESCRIPTION = "Get all flow histories by flow traceability's id.";
    public static final String GET_FLOW_HISTORIES_BY_FLOW_ID_RESPONSE_200_DESCRIPTION = "Get all flow histories by flow's id success response.";
    // Get all Flow history status event
    public static final String GET_FLOW_HISTORY_STATUS_EVENT_DESCRIPTION = "Get all status of flow history. This will return an object contains a list of flow history status";
    public static final String GET_FLOW_HISTORY_STATUS_EVENT_RESPONSE_200_DESCRIPTION = "Get all status of flow history success response.";
    public static final String GET_FLOW_HISTORY_STATUS_EVENT_RESPONSE_200_EXAMPLE = "{\"flowHistoryEvent\":[{\"key\":\"flow.history.status.canceled\",\"value\":\"Canceled\"}]}";
    private FlowHistoryControllerSwaggerConstants() {
    }
}
