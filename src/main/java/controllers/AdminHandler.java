package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import filters.SecureFilter;

import models.User;


/**
 * Handles all Actions for the Admin Section
 * @author Patrick Thum 2012
 * released under Apache 2.0 License
 */
@FilterWith(SecureFilter.class)
public class AdminHandler{
	
	
	  // ---------------------Functions for the Admin-Section ---------------------
	  //TODO: implement some useful adminfunctions :)
	  
	  // Shows all Users - Admin-Section
	  public Result showUsers(Context context){ //TODO Handle Account-Generation
		  String cok = context.getSessionCookie().get("adm");
		  if(!(cok==null)){
			  Map<String, List<User>> map = new HashMap<String, List<User>>();
			  map.put("users", User.all()); 
			  return Results.html().render(map);
		  }else{
			  return Results.forbidden();
		  }
			  
//			  if( ( session().containsKey("adm") ) ){
//			  List<String> lst = Arrays.asList(jmc.getDomainList());
//			  return ok( usrAdminF.render("", User.all(), lst, userAdm, domainFrm) );
//			  	}else return badRequest( index.render("not authorized!") ); 	  	
	  }
	  
	  // promotes the User - Admin-Section 
	  public Result promoteUser(@PathParam("uid") Long id){
		  //TODO check whether the user is authorized to do this!
		  User.promote(id);
		  return Results.redirect("/admin");
		  
	  }
	  
	  /**
	   * Handles the user delete function
	   * @param id
	   * @return
	   */
	  public Result deleteUser(Long id){
		  //TODO check whether the user is authorized to do this!
		  User.delete(id);
		  return Results.redirect("/admin");
	  }
	
	  /**
	   * adds the given domain to the james-server
	   * @return 
	   */
	  public Result addDomain(){
		  return Results.html(Result.SC_501_NOT_IMPLEMENTED);
	  }
	  
	  /**
	   * deletes the selected domain
	   * @return
	   */
	  public Result deleteDomain(){
		  return Results.html(Result.SC_501_NOT_IMPLEMENTED);
	  }
}


