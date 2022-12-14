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
package org.jboss.test.cluster.web.aop.deprec;



/**
 * Test class for TreeCacheAOP.
 * Person is a POJO that will be instrumented with CacheInterceptor
 *
 * @version $Revision: 59755 $
 * annotation marker that is needed for fine-grained replication.
 * @@org.jboss.web.tomcat.tc5.session.AopMarker
 */
public class Address
{
   String street = null;
   String city = null;
   int zip = 0;

   public String getStreet()
   {
      return street;
   }

   public void setStreet(String street)
   {
      this.street = street;
   }

   public String getCity()
   {
      return city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public int getZip()
   {
      return zip;
   }

   public void setZip(int zip)
   {
      this.zip = zip;
   }

   public String toString()
   {
      return "street=" + getStreet() + ", city=" + getCity() + ", zip=" + getZip();
   }

//    public Object writeReplace() {
//	return this;
//    }
}
