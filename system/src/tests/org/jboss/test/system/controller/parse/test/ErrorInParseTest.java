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
package org.jboss.test.system.controller.parse.test;

import javax.management.MalformedObjectNameException;

import org.jboss.deployment.DeploymentException;
import org.jboss.test.system.controller.AbstractControllerTest;
import org.jboss.test.system.controller.support.SimpleMBean;

/**
 * ErrorInParseTest.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public abstract class ErrorInParseTest extends AbstractControllerTest
{
   public ErrorInParseTest(String name)
   {
      super(name);
   }

   protected void errorInParse(Class expected) throws Exception
   {
      String resource = getName();
      resource = resource.substring(4) + ".xml";
      assertInitialDeployFailure(resource, SimpleMBean.OBJECT_NAME, expected);
   }
   
   public void testNameMissing() throws Exception
   {
      errorInParse(DeploymentException.class);
   }
   
   public void testNameEmpty() throws Exception
   {
      errorInParse(DeploymentException.class);
   }
   
   public void testNameInvalid() throws Exception
   {
      errorInParse(MalformedObjectNameException.class);
   }
}
