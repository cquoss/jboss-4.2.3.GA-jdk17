/*
  * JBoss, Home of Professional Open Source
  * Copyright 2007, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.test.web.test;

import java.net.HttpURLConnection;

import junit.framework.Test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.test.JBossTestCase;

//$Id$

/**
 *  JBAS-4077: Web Programmatic Login 
 *  @author Anil.Saldhana@redhat.com
 *  @since  Mar 12, 2007 
 *  @version $Revision$
 */
public class WebProgrammaticLoginTestCase extends JBossTestCase
{ 
   private String baseURLNoAuth = "http://" + getServerHost() 
              + ":" + Integer.getInteger("web.port", 8080) + "/"; 
   private HttpClient httpConn = new HttpClient();

   public WebProgrammaticLoginTestCase(String name)
   {
      super(name); 
   }
   
   public static Test suite() throws Exception
   { 
      return getDeploySetup(WebProgrammaticLoginTestCase.class, 
            "programmaticweblogin.ear"); 
   }
   
   /**
    * Test unsuccessful login
    * @throws Exception
    */
   public void testUnsuccessfulLogin() throws Exception
   {
      String path = "war1/TestServlet";
      // try to perform programmatic auth without supplying login information.
      HttpMethod indexGet = null;
      try
      {
         indexGet = new GetMethod(baseURLNoAuth + path + "?operation=login"); 
         int responseCode = httpConn.executeMethod(indexGet);
         assertTrue("Get Error("+responseCode+")", 
               responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR);
         // assert access to the restricted area of the first application is denied.
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth + 
               "war1/restricted/restricted.html");
         // assert access to the second application is not granted, as no successful login
         // was performed (and therefore no ssoid has been set).
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth + "war2/index.html");
      }
      finally
      {
         if(indexGet != null)
           indexGet.releaseConnection();
      } 
      // try to perform programmatic auth with no valid username/password.
      path = path + "?operation=login&username=dummy&pass=dummy";
      try
      {
         indexGet = new GetMethod(baseURLNoAuth + path); 
         int responseCode = httpConn.executeMethod(indexGet);
         assertTrue("Get Error("+responseCode+")", 
               responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR);
         // assert access to the restricted applications remains denied.
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth + 
               "war1/restricted/restricted.html");
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth + "war2/index.html");
      }
      finally
      {
         if(indexGet != null)
           indexGet.releaseConnection();
      } 
   }
   
   /**
    * Test Successful programmatic login in a servlet
    *
    */
   public void testSuccessfulLogin() throws Exception
   {
      String path = "war1/TestServlet?operation=login&username=jduke&pass=theduke"; 
      HttpMethod indexGet = null;
      HttpMethod indexGet2 = null;
      try
      {
         indexGet = new GetMethod(baseURLNoAuth + path); 
         int responseCode = httpConn.executeMethod(indexGet);
         assertTrue("Get OK("+responseCode+")", responseCode == HttpURLConnection.HTTP_OK);
         // assert access to the restricted are of the first application is now allowed.
         SSOBaseCase.checkAccessAllowed(this.httpConn, this.baseURLNoAuth +
               "war1/restricted/restricted.html");
         // assert the sso cookie has been created.
         SSOBaseCase.processSSOCookie(this.httpConn.getState(), this.baseURLNoAuth, this.baseURLNoAuth);
         // assert access to the second application is allowed
         SSOBaseCase.checkAccessAllowed(this.httpConn, this.baseURLNoAuth + "war2/index.html");

         // perform a programmatic logout and assert access is not allowed anymore.
         indexGet2 = new GetMethod(baseURLNoAuth + "war1/TestServlet?operation=logout");
         responseCode = httpConn.executeMethod(indexGet2);
         assertTrue("Get OK("+responseCode+")", responseCode == HttpURLConnection.HTTP_OK);
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth +
               "war1/restricted/restricted.html");
         SSOBaseCase.checkAccessDenied(this.httpConn, this.baseURLNoAuth + "war2/index.html");
      }
      finally
      {
         if(indexGet != null)
           indexGet.releaseConnection();
         if(indexGet2 != null)
           indexGet2.releaseConnection();
      } 
   } 

}
