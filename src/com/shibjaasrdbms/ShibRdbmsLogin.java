package com.shibjaasrdbms;
                                                                                                      
import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import com.shibjaasrdbms.PyHashVerifier;

public class ShibRdbmsLogin implements LoginModule {

	protected Subject subject;
        protected CallbackHandler callbackHandler;
        protected Map sharedState;
        protected Map options;

        protected String dbDriver = "";
        protected String dbUrl = "";
        protected String dbUser = "";
        protected String dbPassword = "";
        protected String dbTable = "";
        protected String dbColumnPw = "";
        protected String dbColumnLogin = "";

	protected String validatedUser = "";
	protected boolean commitSucceeded = false;

	protected PyHashVerifier hashVerifier = null;

	public ShibRdbmsLogin() {
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {

		this.subject = subject;
                this.callbackHandler = callbackHandler;
                this.sharedState = sharedState;
                this.options = options;

		dbDriver = getOption("dbDriver", null);
                dbUrl = getOption("dbUrl", null);
                dbUser = getOption("dbUser", null);
                dbPassword = getOption("dbPassword", null);
                dbTable = getOption("dbTable", "");
                dbColumnLogin = getOption("dbColumnLogin", "");
                dbColumnPw = getOption("dbColumnPw", "");

		hashVerifier = new PyHashVerifier(getOption("pyExePath", ""), getOption("pyModulePath", ""));
	}

	public boolean login() throws LoginException {
		
		try {
			String username;
			char password[] = null;
			Callback[] callbacks = new Callback[2];

			callbacks[0] = new NameCallback("Username:");
			callbacks[1] = new PasswordCallback("Password:", false);

			callbackHandler.handle(callbacks);

			username = ((NameCallback)callbacks[0]).getName();
			password = ((PasswordCallback)callbacks[1]).getPassword();

			((PasswordCallback)callbacks[1]).clearPassword();

			validatedUser = validateUser(username, password);

			return true;

		} catch (UnsupportedOperationException ex) {
			throw new LoginException(ex.getMessage());
		} catch (Exception ex) {
			throw new LoginException(ex.getMessage());
		}
	}

	public boolean commit() throws LoginException {

		if (validatedUser.isEmpty()) {
                        return false;
                }

                commitSucceeded = true;

		return true;
	}

	public boolean abort() throws LoginException {

                if (validatedUser.isEmpty()) {
                        return false;
                } 

		logout();

		return true;
	}
	public boolean logout() throws LoginException {

                validatedUser = "";
                commitSucceeded = false;

		return true;
	}
	
	//
	//get option from initilized options
	//
	protected String getOption(String name, String defaultValue) {
                String optionValue = (String)options.get(name);
                return (optionValue != null)?optionValue:defaultValue;
        }

	protected String validateUser(String username, char password[]) throws LoginException {

		Connection con = null;
                ResultSet res = null;
                PreparedStatement stmt = null;

                try {
                        Class.forName(dbDriver);

                        if (!dbUser.isEmpty())
                           con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                        else
                           con = DriverManager.getConnection(dbUrl);

                        stmt = con.prepareStatement("SELECT " + dbColumnPw + " FROM " + dbTable + " WHERE " + dbColumnLogin + "=?");
                        stmt.setString(1, username);

                        res = stmt.executeQuery();
                        if (!res.next()) 
				throw new FailedLoginException("Credentials not recognized");

                        String password_hash = res.getString(1);

                        if (!hashVerifier.verify(new String(password), password_hash)) {
                        	throw new LoginException("Credentials not recognized");
                        }

                        return username;
                }
		catch (IOException ex) {
			throw new LoginException("IOException");
		}
		catch (ClassNotFoundException ex) {
			throw new LoginException(ex.getMessage());
		}
                catch (SQLException ex) {
                        throw new LoginException(ex.getMessage());
                }
                finally {
                        try {
                                if (res != null) res.close();
                                if (stmt != null) stmt.close();
                                if (con != null) con.close();
                        } catch (Exception e) { }
                }
	}
}
