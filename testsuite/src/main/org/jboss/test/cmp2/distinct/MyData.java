/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.cmp2.distinct;

import java.io.Serializable;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class MyData
   implements Serializable
{
   private String data;

   public String getData()
   {
      return data;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public String toString()
   {
      return "[MyData " + data + "]";
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof MyData))
      {
         return false;
      }

      final MyData myData = (MyData)o;

      if(data != null ? !data.equals(myData.data) : myData.data != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      return (data != null ? data.hashCode() : 0);
   }
}
