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
package org.jboss.ha.framework.interfaces;

import java.io.Serializable;
import java.util.Vector;
import java.util.ArrayList;

/** 
 *
 *   @author  <a href="mailto:bill@burkecentral.com">Bill Burke</a>.
 *   @author  <a href="mailto:sacha.labourey@cogito-info.ch">Sacha Labourey</a>.
 *   @version $Revision: 57188 $
 *
 * <p><b>Revisions:</b><br>
 * <p><b>28.07.2002 - Sacha Labourey:</b>
 * <ul>
 * <li> Added network-partition merge callback for listeners</li>
 * </ul>
 */

public interface HAPartition
{
   // *******************************
   // *******************************
   // Partition information accessors
   // *******************************
   // *******************************
   //
   /**
    * Return the name of this node in the current partition. The name is
    * dynamically determined by the partition. The name will be the String
    * returned by <code>getClusterNode().getName()</code>.
    * 
    * @return The node name
    * 
    * @see #getClusterNode()
    */   
   public String getNodeName();
   /**
    * The name of the partition. Either set when creating the partition
    * (MBEAN definition) or uses the default name
    * @return Name of the current partition
    */   
   public String getPartitionName();

   /**
    * Accessor to the DRM that is linked to this partition.
    * @return the DRM linked to this partition
    */   
   public DistributedReplicantManager getDistributedReplicantManager();
   /**
    * Accessor the the DistributedState (DS) that is linked to this partition.
    * @return the DistributedState service
    */   
   public DistributedState getDistributedStateService ();

   // ***************************
   // ***************************
   // RPC multicast communication
   // ***************************
   // ***************************
   //
   /**
    * The partition receives RPC calls from other nodes in the cluster and demultiplex
    * them, according to a service name, to a particular service. Consequently, each
    * service must first subscribe with a particular service name in the partition. The subscriber
    * does not need to implement any specific interface: the call is handled
    * dynamically through reflection.
    * @param serviceName Name of the subscribing service (demultiplexing key)
    * @param handler object to be called when receiving a RPC for its key.
    */   
   public void registerRPCHandler(String serviceName, Object handler);
   /**
    * Unregister the service from the partition
    * @param serviceName Name of the service key (on which the demultiplexing occurs)
    * @param subscriber The target object that unsubscribes
    */   
   public void unregisterRPCHandler(String serviceName, Object subscriber);

   // Called only on all members of this partition on all nodes
   //
   /**
    * Invoke a synchronous RPC call on all nodes of the partition/cluster
    * @param serviceName Name of the target service name on which calls are de-multiplexed
    * @param methodName name of the Java method to be called on remote services
    * @param args array of Java Object representing the set of parameters to be
    * given to the remote method
    * @param types The types of the parameters
    * @param excludeSelf indicates if the RPC must also be made on the current
    * node of the partition or only on remote nodes
    * @throws Exception Throws if a communication exception occurs
    * @return an array of answers from remote nodes
    */
   public ArrayList callMethodOnCluster(String serviceName, String methodName,
         Object[] args, Class[] types, boolean excludeSelf) throws Exception;


   /**
    *
    * @param serviceName
    * @param methodName
    * @param args
    * @param excludeSelf
    * @return
    * @throws Exception
    * @deprecated Use {@link #callMethodOnCluster(String, String, Object[], Class[], boolean)} instead
    */
   public ArrayList callMethodOnCluster(String serviceName, String methodName,
         Object[] args, boolean excludeSelf) throws Exception;

   /**
    * Invoke a asynchronous RPC call on all nodes of the partition/cluster. The
    * call will return immediately and will not wait that the nodes answer. Thus
    * no answer is available.
    * @param serviceName Name of the target service name on which calls are de-multiplexed
    * @param methodName name of the Java method to be called on remote services
    * @param args array of Java Object representing the set of parameters to be
    * given to the remote method
    * @param types The types of the parameters
    * @param excludeSelf indicates if the RPC must also be made on the current
    * node of the partition or only on remote nodes
    * @throws Exception Throws if a communication exception occurs
    */   
   public void callAsynchMethodOnCluster (String serviceName, String methodName,
         Object[] args, Class[] types, boolean excludeSelf) throws Exception;


   /**
    *
    * @param serviceName
    * @param methodName
    * @param args
    * @param excludeSelf
    * @throws Exception
    * @deprecated Use {@link #callAsynchMethodOnCluster(String, String, Object[], Class[], boolean)} instead
    */
   public void callAsynchMethodOnCluster (String serviceName, String methodName,
         Object[] args, boolean excludeSelf) throws Exception;

   /**
    * Calls method on Cluster coordinator node only.  The cluster coordinator node is the first node to join the
    * cluster or the first node in the current cluster view.
    * @param serviceName Name of the target service name on which calls are de-multiplexed
    * @param methodName name of the Java method to be called on remote services
    * @param args array of Java Object representing the set of parameters to be
    * given to the remote method
    * @param types The types of the parameters
    * node of the partition or only on remote nodes
    * @param excludeSelf indicates if the RPC will be made on the current node even if the current node
    * is the coordinator
    * @throws Exception Throws if a communication exception occurs
    * @return an array of answers from remote nodes
    */
      public ArrayList callMethodOnCoordinatorNode(String serviceName, String methodName,
             Object[] args, Class[] types, boolean excludeSelf) throws Exception;


   // *************************
   // *************************
   // State transfer management
   // *************************
   // *************************
   //
   
   /**
    * State management is higly important for clustered services. Consequently, services that wish to manage their state
    * need to subscribe to state transfer events. When their node start, a state is pushed from another node to them.
    * When another node starts, they may be asked to provide such a state to initialise the newly started node.
    */   
   public interface HAPartitionStateTransfer
   {
      /**
       * Called when a new node need to be initialized. This is called on any existing node to determine a current state for this service.
       * @return A serializable representation of the state
       */      
      public Serializable getCurrentState ();
      /**
       * This callback method is called when a new service starts on a new node: the state that it should hold is transfered to it through this callback
       * @param newState The serialized representation of the state of the new service.
       */      
      public void setCurrentState(Serializable newState);
   }
   
   /**
    * Register a service that will participate in state transfer protocol and receive callbacks
    * @param serviceName Name of the service that subscribes for state stransfer events. This name must be identical for all identical services in the cluster.
    * @param subscriber Object implementing {@link HAPartitionStateTransfer} and providing or receiving state transfer callbacks
    */   
   public void subscribeToStateTransferEvents (String serviceName, HAPartition.HAPartitionStateTransfer subscriber);
   /**
    * Unregister a service from state transfer callbacks.
    * @param serviceName Name of the service that participates in the state transfer protocol
    * @param subscriber Service implementing the state transfer callback methods
    */   
   public void unsubscribeFromStateTransferEvents (String serviceName, HAPartition.HAPartitionStateTransfer subscriber);

   // *************************
   // *************************
   // Group Membership listeners
   // *************************
   // *************************
   //
   /**
    * When a new node joins the cluster or an existing node leaves the cluster
    * (or simply dies), membership events are raised.
    *
    */   
   public interface HAMembershipListener
   {
      /** Called when a new partition topology occurs. This callback is made
       * using the JG protocol handler thread and so you cannot execute new
       * cluster calls that need this thread. If you need to do that implement
       * the aynchronous version of the listener interface.
       *
       * @param deadMembers A list of nodes that have died since the previous view
       * @param newMembers A list of nodes that have joined the partition since the previous view
       * @param allMembers A list of nodes that built the current view
       */      
      public void membershipChanged(Vector deadMembers, Vector newMembers, Vector allMembers);
   }

   /** A tagging interface for HAMembershipListener callbacks that will
    * be performed in a thread seperate from the JG protocl handler thread.
    * The ordering of view changes is preserved, but listeners are free to
    * execute cluster calls.
    */
   public interface AsynchHAMembershipListener extends HAMembershipListener
   {
      // Nothing new
   }

   public interface HAMembershipExtendedListener extends HAPartition.HAMembershipListener
   {
      /** Extends HAMembershipListener to receive a specific callback when a
       * network-partition merge occurs. The same restriction on interaction
       * with the JG protocol stack applies.
       *
       * @param deadMembers A list of nodes that have died since the previous view
       * @param newMembers A list of nodes that have joined the partition since the previous view
       * @param allMembers A list of nodes that built the current view
       * @param originatingGroups A list of list of nodes that were previously partionned and that are now merged
       */      
      public void membershipChangedDuringMerge(Vector deadMembers, Vector newMembers,
            Vector allMembers, Vector originatingGroups);
   }

   /** A tagging interface for HAMembershipExtendedListener callbacks that will
    * be performed in a thread seperate from the JG protocl handler thread.
    * The ordering of view changes is preserved, but listeners are free to
    * execute cluster calls.
    */
   public interface AsynchHAMembershipExtendedListener extends HAMembershipExtendedListener
   {
      // Nothing new
   }

   /**
    * Subscribes to receive {@link HAMembershipListener} events.
    * @param listener The membership listener object
    */   
   public void registerMembershipListener(HAMembershipListener listener);
   /**
    * Unsubscribes from receiving {@link HAMembershipListener} events.
    * @param listener The listener wishing to unsubscribe
    */   
   public void unregisterMembershipListener(HAMembershipListener listener);
   /**
    * Returns whether this partition will synchronously notify any 
    * HAMembershipListeners of membership changes using the calling thread
    * from the underlying <code>ClusterPartition</code>.
    * 
    * @return <code>true</code> if registered listeners that don't implement
    *         <code>AsynchHAMembershipExtendedListener</code> or
    *         <code>AsynchHAMembershipListener</code> will be notified
    *         synchronously of membership changes; <code>false</code> if
    *         those listeners will be notified asynchronously.  Default
    *         is <code>false</code>.
    */
   public boolean getAllowSynchronousMembershipNotifications();
   /**
    * Sets whether this partition will synchronously notify any 
    * HAMembershipListeners of membership changes using the calling thread
    * from the underlying <code>ClusterPartition</code>.
    * 
    * @param allowSync  <code>true</code> if registered listeners that don't 
    *         implement <code>AsynchHAMembershipExtendedListener</code> or
    *         <code>AsynchHAMembershipListener</code> should be notified
    *         synchronously of membership changes; <code>false</code> if
    *         those listeners can be notified asynchronously.  Default
    *         is <code>false</code>.
    */
   public void setAllowSynchronousMembershipNotifications(boolean allowSync);
   /**
    * Each time the partition topology changes, a new view is computed. A view is a list of members,
    * the first member being the coordinator of the view. Each view also has a distinct identifier.
    * @return The identifier of the current view
    */   
   public long getCurrentViewId();
   /**
    * Return the list of member nodes that built the current view i.e. the current partition.
    * @return An array of Strings containing the node names
    */   
   public Vector getCurrentView ();

   /**
    * Return the member nodes that built the current view i.e. the current partition.
    * @return   An array of ClusterNode listing the current members of the partitionn.
    *           This array will be in the same order in all nodes in the cluster that
    *           have received the current view.
    */
   public ClusterNode[] getClusterNodes ();

   /**
    * Return member node for the current cluster node.  
    * @return ClusterNode containing the current node name
    */
   public ClusterNode getClusterNode ();
}