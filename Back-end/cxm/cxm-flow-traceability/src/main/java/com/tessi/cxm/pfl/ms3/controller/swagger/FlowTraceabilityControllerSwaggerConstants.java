package com.tessi.cxm.pfl.ms3.controller.swagger;

public class FlowTraceabilityControllerSwaggerConstants {
  public static final String ADD_FLOW_TRACEABILITY_SUMMARY = "Add a new Flow traceability";
  public static final String ADD_FLOW_TRACEABILITY_DESCRIPTION =
      "Add a flow traceability. Try it now or see an example. \n Requests with provided body example. This will return back the object of the flow as long as the request success. <br> <br> **Note:** The deposit mode, channel and subChannel fields are the enumerations which required to pass the corresponding value. <br> <br> <table border=\"2\">  <tr>  <th>Deposit Mode</th> </tr>  <tr>  <td>Virtual Printer</td> </tr> <tr>  <td>Portal</td> </tr> <tr> <td>API</td>  </tr> <tr> <td>Batch</td> </tr> </table> <br> <br> <table border=\"2\">  <tr>  <th>Channel</th>  <th>Sub-Channel</th>  </tr>  <tr>  <td>Postal</td>  <td>Reco, Reco AR</td>  </tr>  <tr>  <td>Digital</td>  <td>CSE, CSE AR, LRE, email, sms</td>  </tr>  <tr>  <td>Multiple</td>  <td>All sub-channel</td>  </tr> </table> <br> <br> The status field is optional, by default the new flow will be created with the status \"In Creation\".";
  public static final String ADD_FLOW_TRACEABILITY_REQUEST_BODY_DESCRIPTION =
      "Flow traceability request body";
  // 201
  public static final String ADD_FLOW_TRACEABILITY_RESPONSE_201_DESCRIPTION =
      "Add flow traceability success response";
  // Find all
  public static final String FIND_ALL_FLOW_TRACEABILITY_SUMMARY =
      "Find all Flow Traceability with pagination.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_DESCRIPTION =
      "Find Flow Traceability with pagination. Requests with provided body example. This will return back an object that consist of an array which contains multiple flows as well as requested page, pagesize and total number of flows. <br> <br> There are several optional filter fields that we can pass as parameter which allow us to get the flow by specific criteria. See the parameter below.";
  // Find all > request parameters
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_FILTER_DESCRIPTION =
      "Filter.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CHANNEL_DESCRIPTION =
      "Channel.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SUB_CHANNEL_DESCRIPTION =
      "Sub Channel.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_MODE_DESCRIPTION =
      "Deposit mode.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION =
      "Status.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SUB_STATUS_DESCRIPTION =
      "sub-Status.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SERVICE_DESCRIPTION =
      "Service.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_BY_DESCRIPTION =
      "User.";
  public static final String
      FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_DATE_START_DESCRIPTION =
          "From deposit date. Format: \"yyyy-MM-dd\".";
  public static final String
      FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_DATE_END_DESCRIPTION =
          "To deposit date. Format: \"yyyy-MM-dd\".";
  public static final String
      FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DATE_START_DESCRIPTION =
          "From status date. Format: \"yyyy-MM-dd\".";
  public static final String FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DATE_END_DESCRIPTION =
      "To status date. Format: \"yyyy-MM-dd\".";
  public static final String
      FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_START_DESCRIPTION =
          "From createdAt date. Format: \"yyyy-MM-dd\".";
  public static final String
      FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_END_DESCRIPTION =
          "To createdAt date. Format: \"yyyy-MM-dd\".";
  // Update status of FLow traceability
  public static final String UPDATE_STATUS_OF_FLOW_TRACEABILITY_SUMMARY =
      "Update status of Flow Traceability.";
  public static final String UPDATE_STATUS_OF_FLOW_TRACEABILITY_DESCRIPTION =
      "Update status of Flow Traceability. Requests with provided body example. This will response back an object of the flow that has been updated with the new status.";
  public static final String UPDATE_STATUS_OF_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION =
      "Update flow traceability success response";
  public static final String UPDATE_STATUS_OF_FLOW_TRACEABILITY_REQUEST_PARAMS_ID_DESCRIPTION =
      "Flow id.";
  public static final String UPDATE_STATUS_OF_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION =
      "New flow status to change to.";
  public static final String FIND_ALL_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION = "list flow traceability success response";
  // Load all Flow Traceability filter criteria
  public static final String LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_SUMMARY =
      "Load all filter criteria of flow traceability.";
  public static final String LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_DESCRIPTION =
      "Load all filter criteria of flow traceability. This will return all the possible filter criteria";
  public static final String LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_RESPONSE_200_DESCRIPTION =
      "Load all filter criteria of flow traceability success response";
  // Get all users
  public static final String GET_ALL_USERS_SUMMARY = "To retrieve the users into service that depend on the user logged in and the functionality key and privilege key.";
  public static final String GET_ALL_USERS_DESCRIPTION = "The functionality key can have (cxm_flow_traceability, cxm_template, and etc) and privilege key also can have (Create, Edit, Delete, and etc).";
  public static final String GET_ALL_USERS_RESPONSE_200_DESCRIPTION = "List of users.";
  // Get all services
  public static final String GET_ALL_SERVICES_SUMMARY = "Get all services.";
  public static final String GET_ALL_SERVICES_DESCRIPTION = "Get all services.";
  public static final String GET_ALL_SERVICES_RESPONSE_200_DESCRIPTION = "List of services.";
  // Get all Sub-Channels by Channel
  public static final String GET_ALL_SUB_CHANNEL_BY_CHANNEL_SUMMARY =
      "Get all sub-channel of a channel.";
  public static final String GET_ALL_SUB_CHANNEL_BY_CHANNEL_DESCRIPTION =
      "Get all sub-channel of a channel. This will return an object contains a list of all available sub-channel of the specific channel";
  public static final String GET_ALL_SUB_CHANNEL_BY_CHANNEL_RESPONSE_200_DESCRIPTION =
      "List of sub-channel success response.";
  public static final String GET_ALL_SUB_CHANNEL_BY_CHANNEL_PARAMS_CHANNEL_DESCRIPTION =
      "Channel's name.";
  public static final String GET_ALL_SUB_CHANNEL_BY_CHANNEL_RESPONSE_200_EXAMPLE =
      "{\"subChannel\":[\"Reco\",\"Reco AR\"]}";
  // Get all status
  public static final String GET_ALL_STATUS_SUMMARY = "Get all status.";
  public static final String GET_ALL_STATUS_DESCRIPTION = "Get all status.";
  public static final String GET_ALL_STATUS_RESPONSE_200_DESCRIPTION = "List of status.";
  public static final String GET_ALL_STATUS_RESPONSE_200_EXAMPLE =
      "{\"flowStatus\":[{\"key\":\"flow.traceability.status.in_creation\",\"value\":\"In Creation\"}]}";
  // Get all
  public static final String GET_ALL_FLOW_TRACEABILITY_DESCRIPTION = "Get all Flow Traceability.";
  public static final String GET_ALL_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION =
      "List of all Flow Traceability.";
  public static final String GET_ALL_FLOW_TRACEABILITY_SUMMARY = "Get all Flow Traceability.";
  private FlowTraceabilityControllerSwaggerConstants() {}


}
