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
package org.jboss.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration; 
import java.util.LinkedList;

/** An implementation of Group that allows that acts as a stack of Principals
with a single Principal Group member active at any time.
When one adds a Principal to a NestablePrincipal the Principal is pushed onto
the active Princpal stack and any of the Group methods operate as though the
Group contains only the Principal. When removing the Principal that corresponds
to the active Principal, the active Principal is popped from the stack and
the new active Principal is effectively set to the new top of the stack.

The typical usage of this class is when doing a JAAS LoginContext login
to runAs a new Principal with a new CallerPrincipal identity
without destroying the current CallerPrincipal identity and roles.

@author  Scott.Stark@jboss.org
@version $Revision: 57203 $
*/
public class NestablePrincipal extends SimplePrincipal implements Group, Cloneable
{   
   /** The serialVersionUID */
   private static final long serialVersionUID = -6163710574424115701L;
   
   /** The stack of the Principals. Elements are pushed/poped by
        inserting/removing element 0.
    */
    private LinkedList principalStack;

    /** Creates new NestablePrincipal with the given name
    */
    public NestablePrincipal(String name)
    {
        super(name);
        principalStack = new LinkedList();
    }

// --- Begin Group interface methods
    /** Returns an enumeration that contains the single active Principal.
    @return an Enumeration of the single active Principal.
    */
    public Enumeration members()
    {
        return new IndexEnumeration();
    }

    /** Removes the first occurence of user from the Principal stack.

    @param user the principal to remove from this group.
    @return true if the principal was removed, or 
     * false if the principal was not a member.
    */
    public boolean removeMember(Principal user)
    {
        return principalStack.remove(user);
    }

    /** Pushes the user onto the Principal stack and makes it the active
        Principal.
    @return true always.
    */
    public boolean addMember(Principal user)
    {
        principalStack.addFirst(user);
        return true;
    }

    /**
     * Returns true if the passed principal is a member of the group. 
     * This method does a recursive search, so if a principal belongs to a 
     * group which is a member of this group, true is returned.
     *
     * @param member the principal whose membership is to be checked.
     *
     * @return true if the principal is a member of this group, 
     * false otherwise.
     */
    public boolean isMember(Principal member)
    {
        if( principalStack.size() == 0 )
            return false;

        Object activePrincipal = principalStack.getFirst();
        return member.equals(activePrincipal);
    }
    
    public synchronized Object clone() throws CloneNotSupportedException   
    { 
       NestablePrincipal clone = (NestablePrincipal) super.clone();
       if(clone != null)
         clone.principalStack = (LinkedList)this.principalStack.clone(); 
       return clone;
    }
    
// --- End Group interface methods

    private class IndexEnumeration implements Enumeration
    {
        private boolean hasMoreElements;

        IndexEnumeration()
        {
            hasMoreElements = principalStack.size() > 0;
        }
        public boolean hasMoreElements()
        {
            return hasMoreElements;
        }
        public Object nextElement()
        {
            Object next = principalStack.getFirst();
            hasMoreElements = false;
            return next;
        }
    }
}
