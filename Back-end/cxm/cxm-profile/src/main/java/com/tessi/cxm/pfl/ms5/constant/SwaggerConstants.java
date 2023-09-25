package com.tessi.cxm.pfl.ms5.constant;

public class SwaggerConstants {

  private SwaggerConstants() {
    // nothing to here
  }

  // Get all clients
  public static final String GET_ALL_CLIENT_DESCRIPTION =
      "Get all clients. This request will return a list of client.";
  public static final String GET_ALL_CLIENT_RESPONSE_200_DESCRIPTION =
      "Get all clients success response.";

  // Create client
  public static final String CREATE_CLIENT_DESCRIPTION =
      "To create a new client. This request will return an object of client.";
  public static final String CREATE_CLIENT_RESPONSE_201_DESCRIPTION =
      "Create a new client success response.";
  // Update client
  public static final String UPDATE_CLIENT_DESCRIPTION =
      "To update an existed client. This request will return an updated client.";
  public static final String UPDATE_CLIENT_DESCRIPTION_200_DESCRIPTION =
      "Update an existed client success response.";

  // Delete client
  public static final String DELETE_CLIENT_DESCRIPTION = "To delete an existed client.";
  public static final String DELETE_CLIENT_204_DESCRIPTION =
      "Delete an existed client success response";

  // region Profile

  // Get a profile by id
  public static final String GET_PROFILE_BY_ID_DESCRIPTION =
      "Get a profile. This request will return an object of profile.";
  public static final String GET_PROFILE_BY_ID_RESPONSE_200_DESCRIPTION =
      "Get a profile by profile's id success response.";
  public static final String ID_DESCRIPTION = "Page index, start from 1";

  // Get a list of profile
  public static final String GET_ALL_PROFILE_DESCRIPTION =
      "Get a list of profile. This request will return a list of profile.";
  public static final String GET_ALL_PROFILE_RESPONSE_200_DESCRIPTION =
      "Get a list of profile success response.";

  // Get a list of profile
  public static final String GET_PROFILE_DETAILS_BY_PROFILE_ID_DESCRIPTION =
      "Get a list of profile details. This request will return a list of profile details.";
  public static final String GET_PROFILE_DETAILS_BY_PROFILE_ID_RESPONSE_200_DESCRIPTION =
      "Get a list of profile details success response.";

  // Add a profile
  public static final String CREATE_PROFILE_DESCRIPTION =
      "Add a new profile. This request will return an object of created profile.";
  public static final String CREATE_PROFILE_REQUEST_BODY_DESCRIPTION =
      "<p>The parameters for creating a profile are divided into 2 parts:</p>"
          + "<ul>"
          + "<li><b>Profile naming</b>: "
          + "<ul>"
          + "<li><b>Name</b>: name given to the profile, it is an identifier of the profile that is unique <b>for the customer</b> (we can therefore find 2 identical names in the platform if the customer is different). "
          + "<br/>This is a rather technical data that will be used in particular to affect users."
          + "<br/><b>*Required field</b>"
          + "</li>"
          + "<li><b>Display Name</b>: Display name given to the profile, used to name the profile for display in the interface. "
          + "<br/><b>*Required field</b>"
          + "</li>"
          + "</ul>"
          + "</li>"
          + "<li><b>Management of profile permissions</b>: "
          + "<ul><li>At least one mandatory privilege.</li></ul>"
          + "</li>"
          + "</ul>"
          + "<p>A profile is unique, the name serves as a unique identifier. If a user tries to create a profile with an already existing name, the system will block the creation. "
          + "<br/>To be able to create a profile, it must necessarily be assigned at least one privilege over a feature, and given a name and display name, otherwise the system will block the creation."
          + "<br/>By default, when you arrive on the creation screen, all fields are blank.</p>";
  public static final String CREATE_PROFILE_RESPONSE_201_DESCRIPTION =
      "Create profile success response.";

  // Delete profile by id
  public static final String DELETE_PROFILE_BY_ID_DESCRIPTION = "To delete a profile by id.";
  public static final String DELETE_PROFILE_BY_ID_RESPONSE_204_DESCRIPTION =
      "Delete a profile by id success response.";

  // Is duplicated name
  public static final String IS_DUPLICATED_NAME_DESCRIPTION =
      "To check duplicated name of profile. This request will return true if name of profile is duplicated.";
  public static final String IS_DUPLICATED_NAME_RESPONSE_200_DESCRIPTION =
      "Check duplicated name of profile success response.";

  // Get pagination and filtering list of profile
  public static final String FIND_ALL_DESCRIPTION =
      "Get pagination and filtering list of profile. This request will return a pagination list of profile.";
  public static final String FIND_ALL_RESPONSE_200_DESCRIPTION =
      "Get pagination and filtering list of profile success response.";

  // Update a profile
  public static final String UPDATE_PROFILE_DESCRIPTION =
      "Update a profile. This request will return an object of updated profile.";
  public static final String UPDATE_PROFILE_REQUEST_BODY_DESCRIPTION = "Profile request body";
  public static final String UPDATE_PROFILE_RESPONSE_200_DESCRIPTION =
      "Update profile success response.";

  // Assign profile to client
  public static final String ASSIGN_PROFILE_DESCRIPTION = "To assign profile to client.";
  public static final String ASSIGN_PROFILE_RESPONSE_201_DESCRIPTION =
      "Assign profile to client success response.";

  // Assign profile to current user
  public static final String ASSIGN_PROFILE_TO_CURRENT_USER_DESCRIPTION =
      "To assign profile to current user.";
  public static final String ASSIGN_PROFILE_TO_CURRENT_USER_RESPONSE_201_DESCRIPTION =
      "Assign profile to current user success response.";

  // Assign profile to user
  public static final String ASSIGN_PROFILE_TO_USER_DESCRIPTION = "To assign profile to user.";
  public static final String ASSIGN_PROFILE_TO_USER_RESPONSE_201_DESCRIPTION =
      "Assign profile to user success response.";

  // Get key value enum functionality
  public static final String GET_KEY_VALUE_ENUM_FUNCTIONALITY_DESCRIPTION =
      "To get key value pairs from functionality";
  public static final String GET_KEY_VALUE_ENUM_FUNCTIONALITY_RESPONSE_200_DESCRIPTION =
      "Get key value pairs from functionality success response.";
  public static final String GET_KEY_VALUE_ENUM_FUNCTIONALITY_RESPONSE_EXAMPLE =
      "{\n"
          + "  \"name\": \"Concepteur\",\n"
          + "  \"displayName\": \"Concepteur MOA\",\n"
          + "  \"clientId\": 0,\n"
          + "  \"functionalities\": [\n"
          + "    {\n"
          + "      \"privileges\": [\n"
          + "        {\n"
          + "          \"key\": \"cxm_template_create\",\n"
          + "          \"visibilityLevel\": \"specific\",\n"
          + "          \"modificationLevel\": \"specific\",\n"
          + "          \"visibility\": true,\n"
          + "          \"modification\": true\n"
          + "        }\n"
          + "      ],\n"
          + "      \"visibilityLevel\": \"specific\",\n"
          + "      \"modificationLevel\": \"client\",\n"
          + "      \"functionalityKey\": \"string\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  // Load level of user
  public static final String LOAD_LEVEL_OF_USER_DESCRIPTION =
      "To load visibility-level of user base on user's functional and privilege key. This request will return visibility-level as String type";
  public static final String LOAD_LEVEL_OF_USER_RESPONSE_200_DESCRIPTION =
      "Load visibility-level of user base on user's functional and privilege key success response.";

  // Load all username of user by functional and privilege key
  public static final String LOAD_ALL_USERNAME_OF_USER_DESCRIPTION =
      "To load all username of user base on functional and privilege key.";
  public static final String LOAD_ALL_USERNAME_OF_USER_RESPONSE_200_DESCRIPTION =
      "Load all username of user base on functional and privilege key success response.";

  // Get privilege
  public static final String GET_PRIVILEGE_DESCRIPTION =
      "To get modification level of user. This request will return modification level as a string.";
  public static final String GET_PRIVILEGE_RESPONSE_200_DESCRIPTION =
      "Get modification level of user success response.";

  // Get user privilege
  public static final String GET_USER_PRIVILEGE_DESCRIPTION =
      "To get an user privilege. This request will return an object of UserProfilePrivilege";
  public static final String GET_USER_PRIVILEGE_RESPONSE_200_DESCRIPTION =
      "Get an user privilege success response.";

  // Load all users by id
  public static final String LOAD_ALL_USERNAME_BY_ID_DESCRIPTION =
      "Load all user's username that related with profile by id. This request will return a list of user's username.";
  public static final String LOAD_ALL_USERNAME_BY_ID_RESPONSE_200_DESCRIPTION =
      "Load all user's username that related with profile by id success response.";

  // List functionalities
  public static final String LIST_FUNCTIONALITIES_RESPONSE_EXAMPLE =
      "{\"functionalities\":[{\"key\":\"cxm_template\",\"value\":\"Designofanemailingtemplate\",\"subValue\":[{\"value\":\"Createatemplate\",\"subValue\":[{\"key\":\"cxm_template_create_from_scratch\",\"value\":\"Fromscratch\"},{\"key\":\"cxm_template_create_by_duplicate\",\"value\":\"Byduplicate\"}],\"key\":\"cxm_template_create\"}]}]}";

  //endregion


  // region User
  // Create new user
  public static final String CREATE_USER_DESCRIPTION = "Create a new user.";
  public static final String CREATE_USER_REQUEST_BODY_DESCRIPTION =
      "<p>The fields for creating a user:</p>"
          + "<ul><li><b>email</b>: User's username and email address.</li></ul>"
          + "<ul><li><b>fullName</b>: First and last name of the user.</li></ul>"
          + "<ul><li><b>password</b>: password for connecting to the user's platform.</li></ul>"
          + "<ul><li><b>confirmedPassword</b>: Must be the same as the password.</li></ul>"
          + "<ul><li><b>serviceId</b>: The service to which the user is attached.</li></ul>"
          + "<ul><li><b>profiles</b>: Profile's ids assigned to a user. At least one profile  must be provided.</li></ul>";
  public static final String CREATE_USER_RESPONSE_201_DESCRIPTION =
      "Created user success response.";

  public static final String VERIFICATION_OF_BLOCKED_ACCOUNTS_DESCRIPTION = "Account verification process by username if is blocked.";

  public static final String VERIFICATION_OF_BLOCKED_ACCOUNTS_RESPONSE_200_DESCRIPTION = "Account verification process by username if is blocked success response.";

  public static final String SAVING_ACCOUNT_LOGIN_ATTEMPTS_DESCRIPTION = "Saving account login attempts.";

  public static final String SAVING_ACCOUNT_LOGIN_ATTEMPTS_RESPONSE_201_DESCRIPTION = "Saving account login attempts success operation.";

  public static final String VERIFICATION_OF_BLOCKED_ACCOUNTS_REQUEST_BODY_DESCRIPTION =
          "<p>The parameters for sending verification of blocked accounts:</p>"
                  + "<ul>"
                  + "<li><b>Authentication Attempts Request</b>: "
                  + "<ul>"
                  + "<li><b>UserName</b>: username <b> of user</b>. "
                  + "<br/><b>*Required field</b>"
                  + "</li>"
                  + "</ul>";

  public static final String SAVING_ACCOUNT_LOGIN_ATTEMPTS_REQUEST_BODY_DESCRIPTION =
          "<p>The parameters for saving login attempts :</p>"
                  + "<ul>"
                  + "<li><b>User Login Attempt</b>: "
                  + "<ul>"
                  + "<li><b>UserName</b>: username <b> of user</b>. "
                  + "<br/><b>*Required field</b>"
                  + "</li>"
                  + "<li><b>LoginStatus</b>: result of current login attempt."
                  + "<br/><b>*Required field</b>"
                  + "</li>"
                  + "</ul>";
  // endregion
}
