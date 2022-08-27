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
package javax.sql;

import java.util.EventListener;

/**
 * <p>A ConnectionEventListener is an object that registers to receive events generated by a PooledConnection.</p>
 *
 * <p>The ConnectionEventListener interface is implemented by a connection pooling component. A connection pooling component
 * will usually be provided by a JDBC driver vendor, or another system software vendor. A ConnectionEventListener is
 * notified by a JDBC driver when an application is finished using its Connection object. This event occurs after the
 * application calls close on its representation of the PooledConnection. A ConnectionEventListener is also notified when
 * a Connection error occurs due to the fact that the PooledConnection is unfit for future use---the server has crashed, for example.
 * The listener is notified, by the JDBC driver, just before the driver throws an SQLException to the application using the
 * PooledConnection.</p>
 */
public interface ConnectionEventListener extends EventListener {

  /**
   * Invoked when the application calls close() on its representation of the connection.
   *
   * @param connectionEvent - an event object describing the source of the event
   */
  public void connectionClosed(ConnectionEvent connectionEvent);

  /**
   * Invoked when a fatal connection error occurs, just before an SQLException is thrown to the application.
   *
   * @param connectionEvent - an event object describing the source of the event
   */
  public void connectionErrorOccurred(ConnectionEvent connectionEvent);
}
