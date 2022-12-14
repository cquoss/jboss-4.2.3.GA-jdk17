/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.ejb3.test.ear.unit;

import org.jboss.test.JBossTestCase;
import org.jboss.ejb3.test.factory.Session1;
import org.jboss.ejb3.test.factory.Session2;
import org.jboss.ejb3.test.factory.MyService;
import org.jboss.ejb3.test.factory.Util;
import org.jboss.ejb3.test.factory.Stateful1;
import org.jboss.ejb3.test.factory.Entity1;
import org.jboss.ejb3.test.factory.Entity2;
import org.jboss.ejb3.test.factory.unit.FactoryUnitTestCase;
import org.jboss.ejb3.test.ear.Facade;
import junit.framework.Test;

/**
 * Sample client for the jboss container.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Id: FactoryUnitTestCase.java 58110 2006-11-04 08:34:21Z scott.stark@jboss.org $
 */

public class EarUnitTestCase
        extends JBossTestCase
{
   org.apache.log4j.Logger log = getLog();

   static boolean deployed = false;
   static int test = 0;

   public EarUnitTestCase(String name)
   {

      super(name);

   }

   public void testEarLib() throws Exception
   {
      Facade facade = (Facade)getInitialContext().lookup("ear-test/FacadeBean/remote");
      facade.testEarLib();
   }

   public void testReferencedEntity() throws Exception
   {
      Facade facade = (Facade)getInitialContext().lookup("ear-test/FacadeBean/remote");
      facade.testReferencedEntity();
   }

   public static Test suite() throws Exception
   {
      return getDeploySetup(EarUnitTestCase.class, "ear-test.ear");
   }

}
