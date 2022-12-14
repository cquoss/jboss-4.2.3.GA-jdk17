/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.ejb3.test.clusteredentity.unit;

import java.util.Properties;
import javax.naming.InitialContext;

import org.hibernate.cache.StandardQueryCache;
import org.jboss.cache.Fqn;
import org.jboss.ejb3.entity.SecondLevelCacheUtil;
import org.jboss.ejb3.test.clusteredentity.classloader.Account;
import org.jboss.ejb3.test.clusteredentity.classloader.AccountHolderPK;
import org.jboss.ejb3.test.clusteredentity.classloader.EntityQueryTest;
import org.jboss.test.JBossClusteredTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Base class for tests involving clustered entities with a scoped classloader.
 *
 * @author Brian Stansberry
 * @version $Id: EntityUnitTestCase.java 57207 2006-09-26 12:06:13Z dimitris@jboss.org $
 */
public class EntityClassloaderTestBase
extends JBossClusteredTestCase
{
   public static final String EAR_NAME = "clusteredentity-classloader-test";
   public static final String JAR_NAME = "clusteredentity-classloader-test";
   public static final String PERSISTENCE_UNIT_NAME = "tempdb";
   
   protected org.apache.log4j.Logger log = getLog();

   protected static final long SLEEP_TIME = 300L;
   
   
   static boolean deployed0 = true;
   static boolean deployed1 = true;

   protected static final AccountHolderPK SMITH = new AccountHolderPK("Smith", "1000");
   protected static final AccountHolderPK JONES = new AccountHolderPK("Jones", "2000");
   protected static final AccountHolderPK BARNEY = new AccountHolderPK("Barney", "3000");
   
   protected EntityQueryTest sfsb0;

   protected EntityQueryTest sfsb1;
   
   public EntityClassloaderTestBase(String name)
   {
      super(name);
   }
   
   protected void setUp() throws Exception
   {
      super.setUp();
      
      sfsb0 = getEntityQueryTest(System.getProperty("jbosstest.cluster.node0"));
      sfsb1 = getEntityQueryTest(System.getProperty("jbosstest.cluster.node1"));  
      sfsb0.cleanup();
      sfsb1.cleanup();
   }
   
   protected EntityQueryTest getEntityQueryTest(String nodeJNDIAddress) throws Exception
   {
      Properties prop1 = new Properties();
      prop1.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
      prop1.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
      prop1.put("java.naming.provider.url", "jnp://" + nodeJNDIAddress + ":1099");
   
      log.info("===== Naming properties for " + nodeJNDIAddress + ": ");
      log.info(prop1);
      log.info("Create InitialContext for " + nodeJNDIAddress);
      InitialContext ctx1 = new InitialContext(prop1);
   
      log.info("Lookup sfsb from " + nodeJNDIAddress);
      EntityQueryTest eqt = (EntityQueryTest)ctx1.lookup(getEarName() + "/EntityQueryTestBean/remote");
      eqt.getCache(isOptimistic());
      
      return eqt;
   }
    
   protected void tearDown() throws Exception
   {
      if (sfsb0 != null)
      {
         try
         {
            sfsb0.remove();
         }
         catch (Exception e) {}
      }
      if (sfsb1 != null)
      {
         try
         {
            sfsb1.remove();
         }
         catch (Exception e) {}
      }
      
      sfsb0 = sfsb1 = null;
   }
   
   protected void standardEntitySetup()
   {
//      sfsb0.createAccountHolder(SMITH, "94536");
      sfsb0.createAccount(SMITH, new Integer(1001), new Integer(5), "94536");
      sfsb0.createAccount(SMITH, new Integer(1002), new Integer(15), "94536");
      sfsb0.createAccount(SMITH, new Integer(1003), new Integer(20), "94536");
      
//      sfsb0.createAccountHolder(JONES, "63088");
      sfsb0.createAccount(JONES, new Integer(2001), new Integer(5), "63088");
      sfsb0.createAccount(JONES, new Integer(2002), new Integer(15), "63088");
      sfsb0.createAccount(JONES, new Integer(2003), new Integer(20), "63088");
      
//      sfsb0.createAccountHolder(BARNEY, "63088");
      sfsb0.createAccount(BARNEY, new Integer(3001), new Integer(5), "63088");
      sfsb0.createAccount(BARNEY, new Integer(3002), new Integer(15), "63088");
      sfsb0.createAccount(BARNEY, new Integer(3003), new Integer(20), "63088");
      
      log.info("Standard entities created");
   }
   
   protected void resetRegionUsageState()
   {  
      String stdName = createRegionName(StandardQueryCache.class.getName());
      String acctName = createRegionName("AccountRegion");
      
      sfsb0.getSawRegionModification(stdName);
      sfsb0.getSawRegionModification(acctName);
      
      sfsb0.getSawRegionAccess(stdName);
      sfsb0.getSawRegionAccess(acctName);
      
      sfsb1.getSawRegionModification(stdName);
      sfsb1.getSawRegionModification(acctName);
      
      sfsb1.getSawRegionAccess(stdName);
      sfsb1.getSawRegionAccess(acctName);
      
      log.info("Region usage state cleared");      
   }
   
   protected void modifyEntities(EntityQueryTest bean)
   {
      bean.updateAccountBranch(1001, "63088");
      bean.updateAccountBalance(2001, 15);
      
      log.info("Entities modified");
   }
   
   protected void restoreEntities(EntityQueryTest bean)
   {
      // Undo the mods done in queryTest
      bean.updateAccountBranch(1001, "94536");
      bean.updateAccountBalance(2001, 5);
      
      log.info("Standard entities restored to initial state");
   }

   /**
    * Executes a series of entity operations and queries, checking that
    * expected modifications and reads of the query cache occur.
    * 
    * @param setupEntities  <code>true</code> if entities don't exist and need
    *                       to be created; <code>false</code> if they should
    *                       exist and need to be returned to their initial state
    * @param useNamedQuery  <code>true</code> if named queries are to be used;
    *                       <code>false</code> if the EJBQL should be passed
    *                       to the Query
    * @param useNamedRegion <code>true</code> if the query should be cached in
    *                       "AccountRegion"; <code>false</code> if it should be
    *                       cached in the default region
    * @param expectInactivatedRegion <code>true</code> if the test should assume
    *                                the query cache region is inactive on each
    *                                server until accessed on that server;
    *                                <code>false</code> if it should be assumed
    *                                the region is activated and able to 
    *                                receive replication events.
    */
   protected void queryTest(boolean setupEntities, boolean useNamedQuery, boolean useNamedRegion, boolean expectInactivatedRegion)
   {
      if (setupEntities)
         standardEntitySetup();
      else
         restoreEntities(sfsb0);
      
      resetRegionUsageState();
      
      // Initial ops on node 0
      
      String regionName = createRegionName(useNamedRegion ? "AccountRegion" : StandardQueryCache.class.getName());
      
      // Query on post code count
      assertEquals("63088 has correct # of accounts", 6, sfsb0.getCountForBranch("63088", useNamedQuery, useNamedRegion));
      
      assertTrue("Query cache used " + regionName, 
                 sfsb0.getSawRegionModification(regionName));
      // Clear the access state
      sfsb0.getSawRegionAccess(regionName);
      
      log.info("First query on node0 done");
      
      // Do it again from node 1
      // Sleep a bit to allow async repl to happen
      sleep(SLEEP_TIME);
      
      // If region isn't activated yet, should not have been modified      
      if (expectInactivatedRegion)
      {
         assertFalse("Query cache remotely modified " + regionName, 
                      sfsb1.getSawRegionModification(regionName));
         // Clear the access state
         sfsb1.getSawRegionAccess(regionName);
      }
      else //if (useNamedRegion)
      {
         assertTrue("Query cache remotely modified " + regionName, 
                    sfsb1.getSawRegionModification(regionName));
         // Clear the access state
         sfsb1.getSawRegionAccess(regionName);         
      }
      
      assertEquals("63088 has correct # of accounts", 6, sfsb1.getCountForBranch("63088", useNamedQuery, useNamedRegion));
      
      if (expectInactivatedRegion)
      {
         // Query should have activated the region and then been inserted
         assertTrue("Query cache modified " + regionName, 
                    sfsb1.getSawRegionModification(regionName));
         // Clear the access state
         sfsb1.getSawRegionAccess(regionName);
      }
      
      log.info("First query on node 1 done");
      
      // We now have the query cache region activated on both nodes.
      
      // Sleep a bit to allow async repl to happen
      sleep(SLEEP_TIME);
      
      // Do some more queries on node 0
      
      assertEquals("Correct branch for Smith", "94536", sfsb0.getBranch(SMITH, useNamedQuery, useNamedRegion));
      
      assertEquals("Correct high balances for Jones", 40, sfsb0.getTotalBalance(JONES, useNamedQuery, useNamedRegion));
      
      assertTrue("Query cache used " + regionName, 
                 sfsb0.getSawRegionModification(regionName));
      // Clear the access state
      sfsb0.getSawRegionAccess(regionName);
      
      log.info("Second set of queries on node0 done");
      
      // Sleep a bit to allow async repl to happen
      sleep(SLEEP_TIME);
      
      // Do it again from node 1
      
      // First check if the previous queries replicated (if the region is replicable)
      
      assertTrue("Query cache remotely modified " + regionName, 
                 sfsb1.getSawRegionModification(regionName));
      // Clear the access state
      sfsb1.getSawRegionAccess(regionName);
      
      assertEquals("Correct branch for Smith", "94536", sfsb1.getBranch(SMITH, useNamedQuery, useNamedRegion));
      
      assertEquals("Correct high balances for Jones", 40, sfsb1.getTotalBalance(JONES, useNamedQuery, useNamedRegion));
      
      // Should be no change; query was already there
      assertFalse("Query cache modified " + regionName, 
                  sfsb1.getSawRegionModification(regionName));
      assertTrue("Query cache accessed " + regionName, 
                 sfsb1.getSawRegionAccess(regionName));
      
      log.info("Second set of queries on node1 done");
      
      // Modify underlying data on node 1
      modifyEntities(sfsb1);
      
      // Confirm query results are correct on node 0
      
      assertEquals("63088 has correct # of accounts", 7, sfsb0.getCountForBranch("63088", useNamedQuery, useNamedRegion));
      
      assertEquals("Correct branch for Smith", "63088", sfsb0.getBranch(SMITH, useNamedQuery, useNamedRegion));
      
      assertEquals("Correct high balances for Jones", 50, sfsb0.getTotalBalance(JONES, useNamedQuery, useNamedRegion));
      
      log.info("Third set of queries on node0 done");
   }
   
   protected void sleep(long millis)
   {
      try
      {
         Thread.sleep(millis);
      }
      catch (InterruptedException e)
      {
         log.warn("Interrupted while sleeping", e);
      }
   }
   
   protected String createRegionName(String noPrefix)
   {
      String prefix = createCacheRegionPrefix(getEarName(), getJarName(), getPersistenceUnitName());
      return "/" + prefix + "/" + noPrefix.replace('.', '/');
   }
            
   protected String getEarName()
   {
      return EAR_NAME;
   }
            
   protected String getJarName()
   {
      return JAR_NAME;
   }
            
   protected String getPersistenceUnitName()
   {
      return PERSISTENCE_UNIT_NAME;
   }
   
   public static String createCacheRegionPrefix(String earName, String jarName, String unitName)
   {
      StringBuilder sb = new StringBuilder();
      if (earName != null)
      {
         sb.append(earName);
         if (!earName.endsWith(".ear")) 
            sb.append("_ear");
         sb.append(",");
      }
      if (jarName != null)
      {
         sb.append(jarName);
         if (!jarName.endsWith(".jar"))
            sb.append("_jar");
         sb.append(",");
      }
      sb.append(unitName);
      String raw = sb.toString();
      // Replace any '.' otherwise the JBoss Cache integration may replace
      // it with a '/' and it will become a level in the FQN
      String escaped = raw.replace('.', '_');
      return escaped;
   }

   protected boolean isOptimistic()
   {
      return false;
   }
}
