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

package org.jboss.ejb3.test.clusteredsession.unit;

import java.rmi.dgc.VMID;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Test;

import org.jboss.ejb3.test.clusteredsession.VMTester;
import org.jboss.test.JBossClusteredTestCase;

/**
 * @author Brian Stansberry *
 */
public class SimpleIsLocalInterceptorUnitTestCase 
extends InvokeLocalTestBase
{

   public SimpleIsLocalInterceptorUnitTestCase(String name)
   {
      super(name);
   }

   public static Test suite() throws Exception
   {
      return getDeploySetup(SimpleIsLocalInterceptorUnitTestCase.class,
                           "clusteredsession-local.jar");
   }
   
   @Override
   protected String getPartitionName()
   {
      return "DefaultPartition";
   }
   
   public void testClusteredStatefulStaysLocal() throws Exception
   {
      stayLocalTest("ClusteredStatefulRemote" , true);
   }
   
   // should round robin
   /*public void testClusteredStatelessStaysLocal() throws Exception
   {
      stayLocalTest("ClusteredStatelessRemote" , false);      
   }*/
   
   public void testNonClusteredStatefulGoesRemote() throws Exception
   {
      stayLocalTest("NonClusteredStatefulRemote", false);      
   }
   
   public void testNonClusteredStatelessGoesRemote() throws Exception
   {
      stayLocalTest("NonClusteredStatelessRemote", false);    
   }
   
   protected String[] getIdenticalJndiUrls() throws Exception
   {
      String[] jndiURLs = getNamingURLs();
      String[] identicalJndiUrls = new String[2];
      
      identicalJndiUrls[0] = jndiURLs[0];
      identicalJndiUrls[1] = jndiURLs[0];
      
      return identicalJndiUrls;
   }
}
