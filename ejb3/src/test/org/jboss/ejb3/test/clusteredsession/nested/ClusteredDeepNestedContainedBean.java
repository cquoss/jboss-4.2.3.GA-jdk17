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

package org.jboss.ejb3.test.clusteredsession.nested;

import javax.ejb.Local;
import javax.ejb.Stateful;

import org.jboss.annotation.ejb.Clustered;
import org.jboss.annotation.ejb.cache.tree.CacheConfig;
import org.jboss.ejb3.test.stateful.nested.base.xpc.DeepNestedContained;
import org.jboss.ejb3.test.stateful.nested.base.xpc.DeepNestedContainedBean;

/**
 * A ClusteredDeepNestedStatefulBean.
 * 
 * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
 * @version $Revision: 1.1 $
 */
@Stateful(name="testDeepNestedContained")
@Clustered
@CacheConfig(maxSize=10000, idleTimeoutSeconds=1, replicationIsPassivation=false) 
@Local(DeepNestedContained.class)
public class ClusteredDeepNestedContainedBean extends DeepNestedContainedBean
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
}
