package com.gmpsop.standardoperatingprocedures.Helper;

/**
 * Created by BD1 on 05-May-17.
 */

public class Constants {

    public static final String BASE_URL = "http://volxom.com/projects/gmpsop/admin/";
    public static final String REGISTER_URL = BASE_URL + "register_data";
    public static final String LOGIN_URL = BASE_URL + "login_data";
    public static final String FORGOT_PASSWORD_URL = BASE_URL + "forgot_pwd";
    public static final String LOGOUT_URL = BASE_URL + "logout_data";
    public static final String GET_USER_DETAIL_URL = BASE_URL + "get_userdata";
    public static final String SAMPLE_DOC_URL = BASE_URL + "sample_doc";
    public static final String SAMPLE_DATA_URL = BASE_URL + "get_sample_Data";
    public static final String GMP_TERM_SEARCH_URL = BASE_URL + "gmp_search";
    public static final String GMP_TERM_SEARCH_SUGGESTION_URL = BASE_URL + "get_suggestions";
    public static final String GMP_TERM_SEARCH_SUGGESTED_TAGS_URL = BASE_URL + "get_suggested_tags";
    public static final String PARENT_DETAIL_URL = BASE_URL + "get_parentDetail";
    public static final String SUBSCRIBE_USER = BASE_URL + "subscribe_user";
    public static final String POST_QUESTION = BASE_URL + "insert_question";
    public static final String REGULATORY_LIST = BASE_URL + "regulatory_list";
    public static final String FORUM_QUESTIONS = BASE_URL + "all_questions";
    public static final String FORUM_COMMENTS = BASE_URL + "get_comments";
    public static final String INSERT_COMMENT = BASE_URL + "insert_comments";
    public static final String QUESTION_VIEWS = BASE_URL + "question_views";
    public static final String NOTIFICATIONS = BASE_URL + "notifications";

    public static final String IAB="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgsDrUNZcTIsnOcB+hvG99iUfK8se5lOrBW7PqaMKs2WCJrcY7jtWa7vFz6QrnOdQUXq6qC/oxB9QgG3QsXIgeTHncRHumduMugK/VOr3zfQicDF4z4LbwchLW+Zh4VWcPB4aeoqBMXrLsHZKoHVMchxA/LciaTLQfTW7LwMTdT8zAsB1JhGJQb0tSO27I0qyybENEDYGbjiKr9EyzGXVCUcu//1MP0PeLEWCxWDn/gKSKicU54KYvKnwS6VSmQDcxXqUJQOD7nvyExHAULYgwjonwK7ZPhgjTg/0BKdWcks3j7HVO4gapB/G7wwWanT441sI+C2X5pLM9zj2oZu2qwIDAQAB";
    public static final int LEVEL_SUBSCRIPTION_REQUEST_CODE = 10001;

    public static final String DOCUMENT_RESPONSE_MSG = "response_msg";
    public static final String DOCUMENT_RESPONSE_ROOT_DETAIL = "root_details";
    public static final String DOCUMENT_RESPONSE_DATA = "data";
    public static final String DOCUMENT_RESPONSE_REGULATORY = "regulatory";
    public static final String DOCUMENT_RESPONSE_SUGGESTION = "suggestions";
    public static final String DOCUMENT_RESPONSE_GMP_SEARCH = "gmpsearch";
    public static final String DOCUMENT_RESPONSE_DEFINITION = "definition";

    public static final String FULL_NAME = "full_name";
    public static final String USER_NAME = "user_name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String SUBSCRIPTION = "subscription";

    public static final String REG_MSG_INSERT_SUCCESS = "Record Inserted successfully";
    public static final String REG_MSG_INSERT_UNSUCCESSFUL = "Error while Inserting";
    public static final String REG_MSG_INSERT_UNSUCCESSFUL_EMAIL_EXIST = "Email address already exist";
    public static final String LOGIN_MSG_SUCCESS = "Login Successfully";
    public static final String LOGIN_MSG_FAILED = "Invalid Information";
    public static final String LOGOUT_MSG_SUCCESS = "Logout Successfully";
    public static final String LOGOUT_MSG_ERROR = "Error while Logout";
    public static final String USER_DETAIL_MSG_SUCCESS = "Getting Detail successfully";
    public static final String USER_DETAIL_MSG_UNSUCCESSFUL = "Record Not Found";
    public static final String SUBSCRIPTION_MSG_SUCCESS = "Successfully Subscribed";
    public static final String SUBSCRIPTION_MSG_UNSUCCESSFUL = "Error while Subscription";

    public static final String LOGIN_EMAIL = "login_email";
    public static final String LOGOUT_EMAIL = "user_email";
    public static final String LOGIN_PASSWORD = "login_password";
    public static final String TYPE_FOLDER = "folder";
    public static final String TYPE_FILE = "file";

    public static final String NO_RECORD_FOUND = "No File or Folder Found";
    public static final String NO_TERM_FOUND = "No Definition Found";

    public static final String PARAMETER_FILE_NAME = "file_name";
    public static final String PARAMETER_ID = "id";
    public static final String PARAMETER_GMP_TERM = "gmp_term";
    public static final String PARAMETER_SUGGESTION_TERM = "suggestion_term";
    public static final String PARAMETER_SUGGESTION_TAGS = "tag_term";
    public static final String PARAMETER_TITLE = "title";
    public static final String PARAMETER_DETAIL = "question";
    public static final String PARAMETER_SENDER_ID = "question_by";
    public static final String PARAMETER_TAGS = "question_tags";
    public static final String PARAMETER_VIEW_COUNT = "view_counts";
    public static final String PARAMETER_VIEWER_EMAIL = "viewer_email";
    public static final String PARAMETER_COMMENT_COUNT = "comment_counts";
    public static final String PARAMETER_COMMENT = "comment";
    public static final String PARAMETER_COMMENTED_BY = "commented_by";
    public static final String PARAMETER_QUESTION_ID = "question_id";
    public static final String PARAMETER_QUESTION_TAG = "question_tags";
    public static final String PARAMETER_QUESTION_VIEWS = "question_views";

    public static final String FAQ = "dashboard_faq - Contact Us";
    public static final String GMP_DOC = "GMP-SOP Library";
    public static final String GMP_OVER_VIEW = "Overview";
    public static final String SAMPLE_DOC = "Sample Documents";
    public static final String REGULATORY_REFERANCE = "Regulatory Reference";
    public static final String GMP_DEFINITION = "Definitions";

    public static final String INTENT_ROOT_TYPE = "intentRootType";
    public static final String INTENT_SEARCH_TERM = "SearchTerm";
    public static final String INTENT_LOGIN_FROM = "loginFrom";
    public static final String ROOT_TYPE = "rootType";

    public static final String LOGIN_FROM_GMP = "gmp";
    public static final String LOGIN_FROM_POST_QUESTION = "postQuestion";

    public static final String GMP_FILE_ID = "id";
    public static final String GMP_FILE_NAME = "file_name";
    public static final String GMP_FILE_TYPE = "file_type";
    public static final String GMP_FILE_PATH = "file_path";
    public static final String GMP_FILE_PARENT_ID = "parent_id";

}