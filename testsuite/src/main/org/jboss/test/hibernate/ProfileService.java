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
package org.jboss.test.hibernate;

import org.jboss.test.hibernate.model.User;
import org.jboss.naming.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * A service bean used to interact with a bound hibernate session for
 * testing purposes.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole</a>
 * @version $Revision: 57211 $
 */
public class ProfileService
{
   private static final String SESSION_FACTORY_NAME = "java:/hibernate/SessionFactory";

   public User loadUser(long id) throws HibernateException
   {
      return loadUser( new Long(id) );
   }

   public User loadUser(Long id) throws HibernateException
   {
      return (User) getSession().load(User.class, id);
   }

   public List listUsers() throws HibernateException
   {
      return getSession()
            .createQuery("from User")
            .list();
   }

   public User locateUser(String handle) throws HibernateException
   {
      return (User) getSession()
            .createQuery("from User as u where u.handle = :handle")
            .setString("handle", handle)
            .uniqueResult();
   }

   public User storeUser(User user) throws HibernateException
   {
      getSession().saveOrUpdate(user);
      getSession().flush();
      return user;
   }

   private Session getSession()
   {
      try
      {
         SessionFactory sf = ( SessionFactory ) Util.lookup( SESSION_FACTORY_NAME, SessionFactory.class );
         return sf.getCurrentSession();
      }
      catch( HibernateException e )
      {
         throw e;
      }
      catch( Exception e )
      {
         throw new HibernateException( "Unable to locate current session", e ); 
      }
   }
}
