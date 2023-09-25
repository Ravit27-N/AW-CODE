package com.tessi.cxm.pfl.ms3.controller.swagger;

public class FlowDocumentControllerSwaggerConstants {
  public static final String ADD_FLOW_DOCUMENT_SUMMARY = "Add a new flow document.";
  public static final String ADD_FLOW_DOCUMENT_DESCRIPTION = "Add a new flow document. Request with a flow id and other required information to create a document of particular flow. This request will return an object of created document.";
  public static final String ADD_FLOW_DOCUMENT_REQUEST_BODY_DESCRIPTION = "Create flow document request body.";
  public static final String ADD_FLOW_DOCUMENT_RESPONSE_201_DESCRIPTION = "Create flow document success response.";
  // Find all
  public static final String FIND_ALL_FLOW_DOCUMENT_SUMMARY = "Find all flow document.";
  public static final String FIND_ALL_FLOW_DOCUMENT_DESCRIPTION = "Find all flow document with pagination. Requests with provided body example. This will return back an object that consist of an array which contains multiple flow documents as well as requested page, pagesize and total number of histories. <br> <br> There are several optional filter fields that we can pass as parameter which allow us to get the documents base on specific criteria. See the parameter below.";
  public static final String FIND_ALL_FLOW_DOCUMENT_REQUEST_PARAMS_FILTER_DESCRIPTION = "Filter.";
  public static final String FIND_ALL_FLOW_DOCUMENT_RESPONSE_200_DESCRIPTION = "Find all flow documents success response.";
  // Update status of flow document
  public static final String UPDATE_STATUS_OF_FLOW_DOCUMENT =
      "<p>&nbsp; &nbsp; When the status of the document (Digital, email) is &ldquo;En cours&rdquo; then the sub-status in history is still &ldquo;En production&rdquo;, which means we will have &ldquo;En production&rdquo; twice. And please note that one status can have multiple sub-status, also one status can have the same sub-status twice.</p>\n"
          + "<p><strong>For Digital, sub-channel LRE, CSE, CSE AR:</strong></p>\n"
          + "<ul>\n"
          + "<li>&ldquo;<strong>Scheduled</strong>&rdquo; can be &nbsp;&ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>En cours</strong>&rdquo; can be &ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>Termin&eacute;</strong>&rdquo; can be &ldquo;Avis&eacute;&rdquo;, &ldquo;Accept&eacute;&rdquo;, &ldquo;R&eacute;clam&eacute;&rdquo;, and &nbsp;pickup postal&nbsp;</li>\n"
          + "</ul>\n"
          + "<p><strong>For Digital, sub-channel email or sms:</strong></p>\n"
          + "<ul>\n"
          + "<li>&ldquo;<strong>Scheduled</strong>&rdquo; can be &ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>En cours</strong>&rdquo; can be &ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>Termin&eacute;</strong>&rdquo; can be &ldquo;Sent&rdquo;, &ldquo;Received&rdquo;, &ldquo;Read&rdquo;, &ldquo;Clicked&rdquo;</li>\n"
          + "</ul>\n"
          + "<p><strong>For Postal:</strong></p>\n"
          + "<ul>\n"
          + "<li>&ldquo;<strong>Scheduled</strong>&rdquo; can be &ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>En cours</strong>&rdquo; can be &ldquo;In production&rdquo;</li>\n"
          + "<li>&ldquo;<strong>Termin&eacute;</strong>&rdquo; can be &ldquo;D&eacute;pos&eacute;&rdquo;, &ldquo;Distribu&eacute;&rdquo;</li>\n"
          + "</ul>";

  private FlowDocumentControllerSwaggerConstants() {
    }
}
