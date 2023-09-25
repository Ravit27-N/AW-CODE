package com.tessi.cxm.pfl.ms3.controller.swagger;

public class FlowDocumentHistoryControllerSwaggerConstants {
  public static final String ADD_FLOW_DOCUMENT_HISTORY_DESCRIPTION =
      "Add a new flow document history. Request with a flow id to create the history of particular flow. This request will automatically map the status of history corresponds to the current flow status. <br><br> **If the flow status is:** <br><br>\"In Creation\" : date/time of deposit <br>=> Corresponds to the \"Submitted\" status in the event history <br><br>\"Validated\" : date/time of finalization <br>=> Corresponds to the \"Finalized\" status in the event history <br><br>\"Scheduled\" : date/time when the flow is scheduled <br>=> Corresponds to the status \"Waiting tobe processed\" in the event history <br><br>\"Progress\" : date/time when the first document in the flow was processed <br>=> Corresponds to the status \"In process\" in the event history<br><br>\"Completed\" : date/time when the last document was sent<br>=> Corresponds to the \"Processed\" status in the event history<br><br>\"Cancelled\" : date/time of the cancellation <br>=>Corresponds to the status\" Cancelled \" of the history of the events<br><br>\"In error\" : date/time of the error <br>=> corresponds to the status \"In error\" in the event history <br><br>";
  public static final String ADD_FLOW_DOCUMENT_HISTORY_REQUEST_BODY_DESCRIPTION =
      "Flow document history request body.";
  public static final String ADD_FLOW_DOCUMENT_HISTORY_RESPONSE_201_DESCRIPTION =
      "Flow document history success created response.";
  // Find all
  public static final String FIND_ALL_FLOW_DOCUMENT_HISTORY_DESCRIPTION =
      "Find all flow document histories with pagination. Requests with provided body example. This will return back an object that consist of an array which contains multiple flow document histories as well as requested page, pageSize and total number of histories. <br> <br> There are several optional filter fields that we can pass as parameter which allow us to get the histories base on specific criteria. See the parameter below.";
  public static final String FIND_ALL_FLOW_DOCUMENT_HISTORY_REQUEST_PARAMS_FILTER_DESCRIPTION =
      "Filter.";
  public static final String FIND_ALL_FLOW_DOCUMENT_HISTORY_RESPONSE_200_DESCRIPTION =
      "Find all flow document histories success response.";
  // Get Flow document history by id
  public static final String GET_FLOW_DOCUMENT_HISTORY_BY_ID_DESCRIPTION =
      "Get flow document history by id. Request with a flow history id as the parameter. This will return an object of a flow document history.";
  public static final String GET_FLOW_DOCUMENT_HISTORY_BY_ID_RESPONSE_200_DESCRIPTION =
      "Get flow document history success response.";
  // Get Flow document history by flow document id.
  public static final String GET_FLOW_DOCUMENT_HISTORIES_BY_FLOW_DOCUMENT_ID_DESCRIPTION =
      "Get all flow histories by flow traceability's id.";
  public static final String
      GET_FLOW_DOCUMENT_HISTORIES_BY_FLOW_DOCUMENT_ID_RESPONSE_200_DESCRIPTION =
          "Get all flow histories by flow document's id success response.";
  private FlowDocumentHistoryControllerSwaggerConstants() {}
}
