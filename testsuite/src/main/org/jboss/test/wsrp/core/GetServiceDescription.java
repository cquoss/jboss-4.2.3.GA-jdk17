// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.wsrp.core;

import java.io.Serializable;


public class GetServiceDescription implements Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 3141377839888888840L;
   protected RegistrationContext registrationContext;
   protected java.lang.String[] desiredLocales;

   public GetServiceDescription()
   {
   }

   public GetServiceDescription(RegistrationContext registrationContext, java.lang.String[] desiredLocales)
   {
      this.registrationContext = registrationContext;
      this.desiredLocales = desiredLocales;
   }

   public RegistrationContext getRegistrationContext()
   {
      return registrationContext;
   }

   public void setRegistrationContext(RegistrationContext registrationContext)
   {
      this.registrationContext = registrationContext;
   }

   public java.lang.String[] getDesiredLocales()
   {
      return desiredLocales;
   }

   public void setDesiredLocales(java.lang.String[] desiredLocales)
   {
      this.desiredLocales = desiredLocales;
   }
}