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
package org.jboss.naming.interceptors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.logging.Logger;
import org.jboss.mx.interceptor.AbstractInterceptor;
import org.jboss.mx.server.Invocation;
import org.jboss.mx.util.MBeanServerLocator;
import org.jnp.interfaces.Naming;
import org.jnp.interfaces.NamingContext;

/** An interceptor that replaces any NamingContext values returned with the
 * proxy found as the Proxy attribute of the mbean given by proxyName.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 57209 $
 */
public class ProxyFactoryInterceptor
   extends AbstractInterceptor
{
   private static Logger log = Logger.getLogger(ProxyFactoryInterceptor.class);
   private String proxyName;
   private Naming proxy;

   public void setProxyName(String proxyName)
   {
      this.proxyName = proxyName;
   }

   // Interceptor overrides -----------------------------------------
   public Object invoke(Invocation invocation) throws Throwable
   {
      Object value = invocation.nextInterceptor().invoke(invocation);
      if( value instanceof NamingContext )
      {
         initNamingProxy();
         NamingContext ctx = (NamingContext) value;
         ctx.setNaming(proxy);
      }
      return value;
   }

   /** This is not synchronized as we don't care if the proxy object might
    * get lookedup more than once on initialization
    * @throws Exception
    */ 
   private void initNamingProxy()
      throws Exception
   {
      if( proxy != null )
         return;

      ObjectName proxyFactory = new ObjectName(proxyName);
      MBeanServer server = MBeanServerLocator.locateJBoss();
      proxy = (Naming) server.getAttribute(proxyFactory, "Proxy");
   }
}
