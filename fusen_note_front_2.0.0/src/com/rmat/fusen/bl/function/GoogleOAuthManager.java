package com.rmat.fusen.bl.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.rmat.fusen.db.GoogleCredentialDAO;
import com.rmat.fusen.db.GoogleCredentialEntity;
import com.rmat.fusen.util.AppProperty;
import com.rmat.fusen.util.LoggingUtil;

/**
 * Credential manager to get, save, delete user credentials.
 *
 * @author jbd@google.com (Burcu Dogan)
 */
public class GoogleOAuthManager {

  public static String USER_ID_KEY = "google_oauth_userid";
  public static String USER_EMAIL_KEY = "google_oauth_useremail";
  public static String REDIRECT_URL_KEY = "google_oauth_redirect_url";
  
  private GoogleClientSecrets clientSecrets;
  /**
   * Transport layer for OAuth2 client.
   */
  private HttpTransport transport;

  /**
   * JSON factory for OAuth2 client.
   */
  private JsonFactory jsonFactory;
  
  /**
   * request
   */
  private HttpServletRequest request;
  
  /**
   * Scopes for which to request access from the user.
   */
  public static final List<String> SCOPES = Arrays.asList(
      // Required to access and manipulate files.
      "https://www.googleapis.com/auth/drive.file",
      "https://www.googleapis.com/auth/drive.appdata",
      // Required to identify the user in our data store.
      "https://www.googleapis.com/auth/userinfo.email",
      "https://www.googleapis.com/auth/userinfo.profile");


  /**
   * Credential Manager constructor.
   * @param clientSecrets App client secrets to be used during OAuth2 exchanges.
   * @param transport Transportation layer for OAuth2 client.
   * @param factory JSON factory for OAuth2 client.
   */
  public GoogleOAuthManager(HttpServletRequest req) {
	  String methodname = "GoogleOAuthManager()";
	  
	this.request = req;
	
    this.transport =  new NetHttpTransport();
    this.jsonFactory = new JacksonFactory();
    
	List<String> redirecturls = new ArrayList<String>();
	redirecturls.add(AppProperty.getProperty("GOOGLEDRIVE_CALLBACKURL"));
	Details web = new Details();
	web.setClientId(AppProperty.getProperty("GOOGLEDRIVE_CLIENT_ID"));
	web.setClientSecret(AppProperty.getProperty("GOOGLEDRIVE_CLIENT_SECRET"));
	web.setRedirectUris(redirecturls);
	this.clientSecrets = new GoogleClientSecrets();
	this.clientSecrets.setFactory(this.jsonFactory);
	this.clientSecrets.setWeb(web);
  }

  /**
   * Builds an empty credential object.
   * @return An empty credential object.
   */
  private Credential buildEmpty() {
	  
	  String methodname = "GoogleOAuthManager.buildEmpty()";
	  
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  return new GoogleCredential.Builder()
        .setClientSecrets(this.clientSecrets)
        .setTransport(transport)
        .setJsonFactory(jsonFactory)
        .build();
  }
/*

  public Credential get(String userId) {
    Credential credential = buildEmpty();
    if (credentialStore.load(userId, credential)) {
      return credential;
    }
    return null;
  }

  public void save(String userId, Credential credential) {
    credentialStore.store(userId, credential);
  }

  public void delete(String userId) {
    credentialStore.delete(userId, get(userId));
  }
*/
  /**
   * Generates a consent page url.
   * @return A consent page url string for user redirection.
   */
  private String getAuthorizationUrl() {
	  
	  String methodname = "GoogleOAuthManager.getAuthorizationUrl()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  GoogleAuthorizationCodeRequestUrl urlBuilder =
        new GoogleAuthorizationCodeRequestUrl(
        clientSecrets.getWeb().getClientId(),
        clientSecrets.getWeb().getRedirectUris().get(0),
        SCOPES).setAccessType("offline").setApprovalPrompt("force");
	  return urlBuilder.build();
  }

  /**
   * Send a request to the UserInfo API to retrieve the user's information.
   *
   * @param credentials OAuth 2.0 credentials to authorize the request.
   * @return User's information.
   * @throws NoUserIdException An error occurred.
   */
  private Userinfo getUserInfo(Credential credentials)
      throws NoUserIdException {
	  
	  String methodname = "GoogleOAuthManager.getUserInfo()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  Oauth2.Builder builder = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(),credentials);
	  Oauth2 userInfoService = builder.build();
	  //Oauth2 userInfoService = Oauth2.builder(new NetHttpTransport(), new JacksonFactory(),credentials)
      //      .setHttpRequestInitializer(credentials).build();
	   
	  Userinfo userInfo = null;
    try {
    	userInfo = userInfoService.userinfo().get().execute();
    } catch (IOException e) {
    	System.err.println("An error occurred: " + e);
    }
    if (userInfo != null && userInfo.getId() != null) {
    	LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "userID=" + userInfo.getId() + ";userEmail=" + userInfo.getEmail());
    	return userInfo;
    } else {
    	throw new NoUserIdException();
    }
  }
  
  /**
   * Retrieves a new access token by exchanging the given code with OAuth2
   * end-points.
   * @param code Exchange code.
   * @return A credential object.
   */
  private Credential retrieve(String code) {
	  
	  String methodname = "GoogleOAuthManager.retrieve()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  try {
		  GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
				  transport,
				  jsonFactory,
				  clientSecrets.getWeb().getClientId(),
				  clientSecrets.getWeb().getClientSecret(),
				  code,
				  clientSecrets.getWeb().getRedirectUris().get(0)).execute();
		  		return buildEmpty().setAccessToken(response.getAccessToken()).setRefreshToken(response.getRefreshToken());
		  } catch (IOException e) {
			  new RuntimeException("An unknown problem occured while retrieving token");
		  }

	  return null;
  }
  
  /**
   * Retrieved stored credentials for the provided user ID.
   *
   * @param userId User's ID.
   * @return Stored Credential if found, {@code null} otherwise.
   * @throws Exception 
   */
  private Credential getStoredCredentials(String userId) {
    // TODO: Implement this method to work with your database. Instantiate a new
    // Credential instance with stored accessToken and refreshToken.
    //throw new UnsupportedOperationException();
	  
	  String methodname ="GoogleOAuthClient.getStoredCredentials()";
	  
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  try{
		  GoogleCredentialDAO dao = new GoogleCredentialDAO();
		  GoogleCredentialEntity entity = dao.select(userId);
		  if(entity == null){
			  return null;
		  }
		  else{
			  Credential credential = buildEmpty();
			  credential.setAccessToken(entity.getAccessToken());
			  credential.setRefreshToken(entity.getRefreshToken());
			  
			  return credential;
		  }
	  }
	  catch(Exception e){
		  LoggingUtil.out(LoggingUtil.ERROR, methodname, "");
		  LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
		  return null;
	  }
	  
  }
  
  /**
   * Store OAuth 2.0 credentials in the application's database.
   *
   * @param userId User's ID.
   * @param credentials The OAuth 2.0 credentials to store.
   * @throws Exception 
   */
  private void storeCredentials(String userId, Credential credentials) {
    // TODO: Implement this method to work with your database.
    // Store the credentials.getAccessToken() and credentials.getRefreshToken()
    // string values in your database.
	
	  String methodname ="GoogleOAuthClient.storeCredentials()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  GoogleCredentialEntity entity = new GoogleCredentialEntity(userId, credentials.getAccessToken(), credentials.getRefreshToken(), new Date());
	  
	  try{
		  GoogleCredentialDAO dao = new GoogleCredentialDAO();
		  dao.insertOrUpdate(entity);
	  }
	  catch(Exception e){
		  LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
		  
	  }

  }
  
  /**
   * Delete OAuth 2.0 credentials in the application's database.
   *
   * @param userId User's ID.
   * @throws Exception 
   */
  private void deleteStoredCredentials(String userId){
	  String methodname ="GoogleOAuthClient.deleteStoredCredentials()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  try{
		  GoogleCredentialDAO dao = new GoogleCredentialDAO();
		  dao.delete(userId);
	  }
	  catch(Exception e){
		  LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
		  
	  }
	  
  }
  
  /**
   * Retrieve credentials using the provided authorization code.
   *
   * This function exchanges the authorization code for an access token and
   * queries the UserInfo API to retrieve the user's e-mail address. If a
   * refresh token has been retrieved along with an access token, it is stored
   * in the application database using the user's e-mail address as key. If no
   * refresh token has been retrieved, the function checks in the application
   * database for one and returns it if found or throws a
   * NoRefreshTokenException with the authorization URL to redirect the user
   * to.
   *
   * @return Credential containing an access and refresh token.
   * @throws NoRefreshTokenException No refresh token could be retrieved from
   *         the available sources.
   * @throws IOException 
   */
  public Credential getActiveCredential() throws GoogleOAuthManager.NoRefreshTokenException, IOException {
	  
	  String methodname ="GoogleOAuthClient.getActiveCredential()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  String userId = (String) request.getSession().getAttribute(USER_ID_KEY);
	  
	  Credential credential = null;
	  
	  try {
		  // Only bother looking for a Credential if the user has an existing
		  // session with their email address stored.
		  if (userId != null) {
			  LoggingUtil.out(LoggingUtil.DEBUG,methodname,"userId in HttpSession =" + userId);
			  
			  credential = getStoredCredentials(userId);
		  }
		  else{
			  LoggingUtil.out(LoggingUtil.DEBUG,methodname,"userId in HttpSession is Null.");
		  }
		  // No Credential was stored for the current user or no refresh token is
		  // available.
		  // If an authorizationCode is present, upgrade it into an
		  // access token and hopefully a refresh token.
		  if ((credential == null || credential.getRefreshToken() == null)
				  && request.getParameter("code") != null) {
			  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "No Credential was stored for the current user or no refresh token is available.");
			  
			  credential = retrieve(request.getParameter("code"));
			  if (credential != null) {
				  Userinfo userInfo = getUserInfo(credential);
				  userId = userInfo.getId();
				  request.getSession().setAttribute(USER_ID_KEY, userId);
				  request.getSession().setAttribute(USER_EMAIL_KEY, userInfo.getEmail());
          // Sometimes we won't get a refresh token after upgrading a code.
          // This won't work for our app, because the user can land directly
          // at our app without first visiting Google Drive. Therefore,
          // only bother to store the Credential if it has a refresh token.
          // If it doesn't, we'll get one below.
				  if (credential.getRefreshToken() != null) {
					  storeCredentials(userId, credential);
				  }
			  }
		  }

		  if (credential == null || credential.getRefreshToken() == null) {
			  // No refresh token has been retrieved.
			  // Start a "fresh" OAuth 2.0 flow so that we can get a refresh token.
			  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "No refresh token has been retrieved. Start a \"fresh\" OAuth 2.0 flow..");
			  
			  String authorizationUrl = getAuthorizationUrl();
			  throw new GoogleOAuthManager.NoRefreshTokenException(authorizationUrl);
		  }
	  } catch (NoUserIdException e) {
      // This is bad because it means the user either denied us access
      // to their email address, or we couldn't fetch it for some reason.
      // This is unrecoverable. In a production application, we'd show the
      // user a message saying that we need access to their email address
      // to work.
		  e.printStackTrace();
	  }
	  return credential;
  }
  
  /**
   * delete stored credential for log out process
   */
  public void deleteActiveCredentials() throws Exception {
	  
	  String methodname ="GoogleOAuthClient.deleteActiveCredentials()";
	  LoggingUtil.out(LoggingUtil.DEBUG	, methodname, "start");
	  
	  try{
		  String userId = (String) request.getSession().getAttribute(USER_ID_KEY);
		  if(userId != null){
			  request.getSession().removeAttribute(USER_ID_KEY);
			  deleteStoredCredentials(userId);
		  }
	  }
	  catch(Exception e){
		  LoggingUtil.out(LoggingUtil.ERROR,methodname,e);
		  throw e;
	  }
  }
  
  /**
   * Exception thrown when an error occurred while retrieving credentials.
   */
  public static class GetCredentialsException extends Exception {

    protected String authorizationUrl;

    /**
     * Construct a GetCredentialsException.
     *
     * @param authorizationUrl The authorization URL to redirect the user to.
     */
    public GetCredentialsException(String authorizationUrl) {
      this.authorizationUrl = authorizationUrl;
    }

    /**
     * Set the authorization URL.
     */
    public void setAuthorizationUrl(String authorizationUrl) {
      this.authorizationUrl = authorizationUrl;
    }

    /**
     * @return the authorizationUrl
     */
    public String getAuthorizationUrl() {
      return authorizationUrl;
    }
  }

  /**
   * Exception thrown when a code exchange has failed.
   */
  public static class CodeExchangeException extends GetCredentialsException {

    /**
     * Construct a CodeExchangeException.
     *
     * @param authorizationUrl The authorization URL to redirect the user to.
     */
    public CodeExchangeException(String authorizationUrl) {
      super(authorizationUrl);
    }

  }

  /**
   * Exception thrown when no refresh token has been found.
   */
  public static class NoRefreshTokenException extends GetCredentialsException {

    /**
     * Construct a NoRefreshTokenException.
     *
     * @param authorizationUrl The authorization URL to redirect the user to.
     */
    public NoRefreshTokenException(String authorizationUrl) {
      super(authorizationUrl);
    }

  }

  /**
   * Exception thrown when no user ID could be retrieved.
   */
  private static class NoUserIdException extends Exception {
  }

  
  
}