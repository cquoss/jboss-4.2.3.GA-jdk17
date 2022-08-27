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
package org.jboss.mq.pm.none;

import javax.jms.JMSException;

import org.jboss.mq.SpyMessage;

/**
 * A persistence manager and cache store that does not persistence.
 * This implements the optimized Topic persistence manager
 *
 * @author Adrian Brock (adrian@jboss.org)
 *
 *  @version $Revision: 57198 $
 */
public class NewPersistenceManager
   extends PersistenceManager
   implements org.jboss.mq.pm.NewPersistenceManager
{
   // Constants --------------------------------------------------------------------

   // Attributes -------------------------------------------------------------------
   
   // Constructors -----------------------------------------------------------------
   
   // Public -----------------------------------------------------------------------
   
   // NewPersistenceManager implementation -----------------------------------------
   
   public void addMessage(SpyMessage message) throws JMSException
   {
      if (delegate != null)
         ((org.jboss.mq.pm.NewPersistenceManager) delegate).addMessage(message);
   }
   
   // ServerMBeanSupport overrides -------------------------------------------------

   protected void startService() throws Exception
   {
      super.startService();
      if (delegate != null && (delegate instanceof org.jboss.mq.pm.NewPersistenceManager) == false)
            throw new UnsupportedOperationException("The delegate persistence manager must be a NewPersistenceManager");
   }
   
   // Protected --------------------------------------------------------------------

   // Inner Classes ----------------------------------------------------------------
}
