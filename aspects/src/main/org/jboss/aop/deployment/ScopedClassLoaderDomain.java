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
package org.jboss.aop.deployment;

import java.lang.ref.WeakReference;

import org.jboss.aop.AspectManager;
import org.jboss.aop.Domain;
import org.jboss.aop.advice.AspectDefinition;
import org.jboss.mx.loading.HeirarchicalLoaderRepository3;
import org.jboss.mx.loading.LoaderRepository;
import org.jboss.mx.loading.RepositoryClassLoader;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * A domain that is used for scoped classloaders
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ScopedClassLoaderDomain extends Domain
{
   
   WeakReference loader;
   boolean parentDelegation;
   ConcurrentReaderHashMap myPerVMAspects = new ConcurrentReaderHashMap();
   ConcurrentReaderHashMap notMyPerVMAspects = new ConcurrentReaderHashMap();
   
   public ScopedClassLoaderDomain(ClassLoader loader, String name, boolean parentDelegation, AspectManager manager, boolean parentFirst)
   {
      // FIXME ScopedClassLoaderDomain constructor
      super(manager, name, parentFirst);
      this.loader = new WeakReference(loader);
      this.parentDelegation = parentDelegation;
   }

   protected ClassLoader getClassLoader()
   {
      ClassLoader cl = (ClassLoader)loader.get();
      if (cl != null)
      {
         return cl;
      }
      return null;
   }
   
   public void removeAspectDefinition(String name)
   {
      AspectDefinition def = super.internalRemoveAspectDefintion(name);
      if (def != null)
      {
         Object o = myPerVMAspects.remove(name);
      }
   }
   
   public Object getPerVMAspect(AspectDefinition def)
   {
      return getPerVMAspect(def.getName());
   }

   public Object getPerVMAspect(String def)
   {
      if (parentDelegation == true)
      {
         //We will alway be loading up the correct class
         Object aspect = super.getPerVMAspect(def);
         return aspect;
      }
      else
      {
         return getPerVmAspectWithNoParentDelegation(def);
      }
   }
   
   private Object getPerVmAspectWithNoParentDelegation(String def)
   {
      Object aspect = myPerVMAspects.get(def);
      if (aspect != null)
      {
         return aspect;
      }

      aspect = super.getPerVMAspect(def);
      if (aspect != null)
      {
         LoaderRepository loadingRepository = getAspectRepository(aspect);
         LoaderRepository myRepository = getScopedRepository();
         if (loadingRepository == myRepository)
         {
            //The parent does not load this class
            myPerVMAspects.put(def, aspect);
         }
         else
         {
            //The class has been loaded by a parent classloader, find out if we also have a copy
            try
            {
               Class clazz = myRepository.loadClass(aspect.getClass().getName());
               if (clazz == aspect.getClass())
               {
                  notMyPerVMAspects.put(def, Boolean.TRUE);
               }
               else
               {
                  //We have a different version of the class deployed
                  AspectDefinition aspectDefinition = getAspectDefinition(def);
                  //Override the classloader to create the aspect instance
                  aspect = createPerVmAspect(def, aspectDefinition, getClassLoader());
                  myPerVMAspects.put(def, aspect);
               }
            }
            catch (ClassNotFoundException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      
      return aspect;
   }
   
   private LoaderRepository getAspectRepository(Object aspect)
   {
      ClassLoader cl = aspect.getClass().getClassLoader();
      ClassLoader parent = cl;
      while (parent != null)
      {
         if (parent instanceof RepositoryClassLoader)
         {
            return ((RepositoryClassLoader)parent).getLoaderRepository();
         }
         parent = parent.getParent();
      }
      return null;
   }
   
   private HeirarchicalLoaderRepository3 getScopedRepository()
   {
      HeirarchicalLoaderRepository3 myRepository = (HeirarchicalLoaderRepository3)((RepositoryClassLoader)getClassLoader()).getLoaderRepository();
      return myRepository;
   }
}
