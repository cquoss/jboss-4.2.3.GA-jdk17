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

package org.jboss.ejb3.test.clusteredsession.stress;

import java.util.Random;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.annotation.ejb.Clustered;
import org.jboss.annotation.ejb.cache.tree.CacheConfig;

/**
 * @author Brian Stansberry
 *
 */
@Stateful
@Clustered
@CacheConfig(idleTimeoutSeconds=60,removalTimeoutSeconds=120)
public class ReplicationStressCounterBean implements ReplicationStressCounter
{
   private byte[] payload;
   private int counter = 0;
   
   public int getPayloadSize()
   {
      return payload == null ? 0 : payload.length;
   }

   public int incrementCounter()
   {
      return ++counter;
   }

   public void setPayloadSize(int bytes)
   {  
      if (bytes > 0)
      {
         payload = new byte[bytes];
         Random random = new Random(System.currentTimeMillis());
         random.nextBytes(payload);
      }
      else
      {
         payload = null;
      }

   }
   
   @Remove
   public void remove()
   {
      payload = null;
      counter = 0;
   }

}
