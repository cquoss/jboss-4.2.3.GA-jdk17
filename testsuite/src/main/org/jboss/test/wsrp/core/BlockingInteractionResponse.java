// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package org.jboss.test.wsrp.core;

import java.io.Serializable;


public class BlockingInteractionResponse implements Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1388307080095678493L;
   protected UpdateResponse updateResponse;
   protected java.lang.String redirectURL;
   protected Extension[] extensions;

   public BlockingInteractionResponse()
   {
   }

   public BlockingInteractionResponse(UpdateResponse updateResponse, java.lang.String redirectURL, Extension[] extensions)
   {
      this.updateResponse = updateResponse;
      this.redirectURL = redirectURL;
      this.extensions = extensions;
   }

   public UpdateResponse getUpdateResponse()
   {
      return updateResponse;
   }

   public void setUpdateResponse(UpdateResponse updateResponse)
   {
      this.updateResponse = updateResponse;
   }

   public java.lang.String getRedirectURL()
   {
      return redirectURL;
   }

   public void setRedirectURL(java.lang.String redirectURL)
   {
      this.redirectURL = redirectURL;
   }

   public Extension[] getExtensions()
   {
      return extensions;
   }

   public void setExtensions(Extension[] extensions)
   {
      this.extensions = extensions;
   }
}
