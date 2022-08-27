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
package org.jboss.test.iiopperf.interfaces;

/** 
 * A serializable data object for testing data passed to an EJB.
 * This data object defines a writeObject method (which means that 
 * the corresponding IDL valuetype is custom-marshaled).
 */
public class CMFoo implements java.io.Serializable
{
   public int i;
   public String s;
   
   public CMFoo(int i, String s)
   {
      this.i = i;
      this.s = s;
   }
   
   public String toString()
   {
      return "CMFoo(" + i + ", \"" + s + "\")";
   }
   
   public boolean equals(Object o)
   {
      return (o instanceof CMFoo) && (((CMFoo)o).i == i)
                                && (((CMFoo)o).s.equals(s));
   }
   
   private synchronized void writeObject(java.io.ObjectOutputStream s)
      throws java.io.IOException {
      s.defaultWriteObject();
   }
}
