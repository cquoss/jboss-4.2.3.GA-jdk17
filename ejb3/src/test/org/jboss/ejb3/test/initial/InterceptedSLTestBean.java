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
package org.jboss.ejb3.test.initial;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.ejb.EJBContext;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.ejb.PostActivate;
import javax.annotation.PreDestroy;
import javax.ejb.PrePassivate;
import javax.ejb.Stateless;

/**
 * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
 * @version $Revision: 57207 $
 */
@Stateless
@Interceptors ({FirstInterceptor.class, SecondInterceptor.class})
public class InterceptedSLTestBean implements InterceptedSLTest
{
   @Resource
   EJBContext ejbCtx;

   public int testMethod(int i)
   {
      System.out.println("InterceptedSLTestBean testMethod");
      return i;
   }

   public int throwsThrowable(int i)throws Throwable
   {
      System.out.println("InterceptedSLTestBean throwsThrowable");
      throw new Throwable();
   }

   @AroundInvoke
   public Object myInterceptor(InvocationContext ctx) throws Exception
   {
      System.out.println("Intercepting in InterceptedSLTestBean.myInterceptor()");
      int val = (Integer)ctx.getContextData().get("DATA");

      int ret = (Integer)ctx.proceed();
      ejbCtx.setRollbackOnly();
      val = (Integer)ctx.getContextData().get("DATA");
      ret += val;
      if (ctx.getTarget() != this) throw new RuntimeException("ctx.getBean() != this: " + ctx.getTarget() + " != " + this);
      return ret;
   }

   @PostConstruct
   public void postConstruct()
   {
      TestStatusBean.postConstruct = true;
   }

   @PreDestroy
   public void preDestroy()
   {
      TestStatusBean.preDestroy = true;
   }

   @PrePassivate
   public void prePassivate()
   {
      throw new RuntimeException("SLSBs don't have PrePassivate methods");
   }

   @PostActivate
   public void postActivate()
   {
      throw new RuntimeException("SLSBs don't have PostActivate methods");
   }

}
