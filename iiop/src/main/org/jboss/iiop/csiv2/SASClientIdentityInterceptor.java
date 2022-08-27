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
package org.jboss.iiop.csiv2;

/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

import java.security.Principal;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.ORB;
import org.omg.CSI.AuthorizationElement;
import org.omg.CSI.EstablishContext;
import org.omg.CSI.GSS_NT_ExportedNameHelper;
import org.omg.CSI.ITTAnonymous;
import org.omg.CSI.IdentityToken;
import org.omg.CSI.MTContextError;
import org.omg.CSI.SASContextBody;
import org.omg.CSI.SASContextBodyHelper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.CSIIOP.CompoundSecMech;
import org.omg.CSIIOP.CompoundSecMechList;
import org.omg.CSIIOP.CompoundSecMechListHelper;
import org.omg.CSIIOP.EstablishTrustInClient;
import org.omg.CSIIOP.IdentityAssertion;
import org.omg.CSIIOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.GSSUP.InitialContextToken;
import org.jacorb.orb.MinorCodes;
import org.jboss.logging.Logger;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.RunAsIdentity;

/**
 * This implementation of 
 * <code>org.omg.PortableInterceptor.ClientRequestInterceptor</code> inserts 
 * the security attribute service (SAS) context into outgoing IIOP requests 
 * and handles the SAS messages received from the target security service 
 * in the SAS context of incoming IIOP replies.
 * 
 * @author  <a href="mailto:reverbel@ime.usp.br">Francisco Reverbel</a>
 * @version $Revision: 57194 $
 */
public class SASClientIdentityInterceptor
   extends LocalObject
   implements ClientRequestInterceptor
{
   /** @since 4.0.1 */
   static final long serialVersionUID = -3416778273722755220L;
   private static final Logger log = 
      Logger.getLogger(SASClientIdentityInterceptor.class);
   private static final boolean traceEnabled = log.isTraceEnabled();


   // Constants ------------------------------------------------------
   private static final int sasContextId =
      org.omg.IOP.SecurityAttributeService.value;

   /*
    * Pre-built empty tokens
    */
   private static final IdentityToken absentIdentityToken;
   static {
      absentIdentityToken = new IdentityToken();
      absentIdentityToken.absent(true);
   }
   private static final AuthorizationElement[] noAuthorizationToken = {};
   private static final byte[] noAuthenticationToken = {};

   // Fields ---------------------------------------------------------
   private Codec codec;

   /* 
    * Username and password of this server, in case it does not use an 
    * SSL certificate to authenticate itself when acting as a client.
    */ 
   private static final String serverUsername = "j2ee"; // hardcoded (REVISIT!)
   private static final String serverPassword = "j2ee"; // hardcoded (REVISIT!)
     
   // Constructor ----------------------------------------------------
    
   public SASClientIdentityInterceptor(Codec codec)
   {
      this.codec = codec;
   }
    
   // Methods  -------------------------------------------------------

    
   // org.omg.PortableInterceptor.Interceptor operations ------------
    
   public String name()
   {
      return "SASClientIdentityInterceptor";
   }

   public void destroy()
   {
      // do nothing
   }    
    
   // ClientRequestInterceptor operations ---------------------------
    
   public void send_request(ClientRequestInfo ri)
   {
      try
      {
         CompoundSecMech secMech = 
            CSIv2Util.getMatchingSecurityMech(
               ri,
               codec,
               (short)(EstablishTrustInClient.value 
                       + IdentityAssertion.value),    /* client supports */
               (short)0                               /* client requires */);
         if (secMech == null)
            return;

         if (traceEnabled)
         {
            StringBuffer tmp = new StringBuffer();
            CSIv2Util.toString(secMech, tmp);
            log.trace(tmp);
         }
         // these "null tokens" will be changed if needed
         IdentityToken identityToken = absentIdentityToken;
         byte[] encodedAuthenticationToken = noAuthenticationToken;

         if ((secMech.sas_context_mech.target_supports
              & IdentityAssertion.value) != 0)
         {
            // will create identity token
            Principal p = null;
            RunAsIdentity runAs = SecurityAssociation.peekRunAsIdentity();
            if (runAs != null)
            {
               // will use run-as identity
               p = runAs; 
            }
            else
            {
               // will use caller identity
               p = SecurityAssociation.getPrincipal(); 
            }

            if (p != null)
            {
               // The name scope needs to be externalized
               String name = p.getName();
               if (name.indexOf('@') < 0)
                  name += "@default"; // hardcoded (REVISIT!)
               byte[] principalName = name.getBytes("UTF-8");
               
               // encode the principal name as mandated by RFC2743
               byte[] encodedName = 
                  CSIv2Util.encodeGssExportedName(principalName);
               
               // encapsulate the encoded name
               Any any = ORB.init().create_any();
               byte[] encapsulatedEncodedName = null;
               GSS_NT_ExportedNameHelper.insert(any, encodedName);
               try
               {
                  encapsulatedEncodedName = codec.encode_value(any);
               }
               catch (InvalidTypeForEncoding e)
               {
                  throw new RuntimeException("Unexpected exception: " + e);
               }
               
               // create identity token
               identityToken = new IdentityToken();
               identityToken.principal_name(encapsulatedEncodedName);
            }
            else if ((secMech.sas_context_mech.supported_identity_types
                      & ITTAnonymous.value) != 0)
            {
               // no run-as or caller identity and the target 
               // supports ITTAnonymous: use the anonymous identity
               identityToken = new IdentityToken();
               identityToken.anonymous(true);
            }
         }
            
         if ((secMech.as_context_mech.target_requires
              & EstablishTrustInClient.value) != 0)
         {
            // will create authentication token with the 
            // configured pair serverUsername/serverPassword
            byte[] encodedTargetName = secMech.as_context_mech.target_name;
            String name = serverUsername;
            if (name.indexOf('@') < 0)
            {
               byte[] decodedTargetName = 
                  CSIv2Util.decodeGssExportedName(encodedTargetName);
               String targetName = new String(decodedTargetName, "UTF-8");
               name += "@" + targetName; // "@default"
            }
            byte[] username = name.getBytes("UTF-8");
            // I don't know why there is not a better way 
            // to go from char[] -> byte[]
            byte[] password = serverPassword.getBytes("UTF-8");
            
            // create authentication token
            InitialContextToken authenticationToken = 
               new InitialContextToken(username,
                                       password,
                                       encodedTargetName);
            // ASN.1-encode it, as defined in RFC 2743
            encodedAuthenticationToken =
               CSIv2Util.encodeInitialContextToken(authenticationToken, codec);
         }

         if (identityToken != absentIdentityToken
             || encodedAuthenticationToken != noAuthenticationToken)
         {
            // at least one non-null token was created, 
            // create EstablishContext message with it
            EstablishContext message = 
               new EstablishContext(0, // stateless ctx id
                                    noAuthorizationToken,
                                    identityToken,
                                    encodedAuthenticationToken); 
            
            // create SAS context with the EstablishContext message
            SASContextBody contextBody = new SASContextBody();
            contextBody.establish_msg(message);
            
            // stuff the SAS context into the outgoing request
            Any any = ORB.init().create_any();
            SASContextBodyHelper.insert(any, contextBody);
            ServiceContext sc =
               new ServiceContext(sasContextId, codec.encode_value(any));
            ri.add_request_service_context(sc,
                                           true /*replace existing context*/);
         }
      }
      catch (java.io.UnsupportedEncodingException e)
      {
         throw new MARSHAL("Unexpected exception: " + e);
      }
      catch (InvalidTypeForEncoding e)
      {
         throw new MARSHAL("Unexpected exception: " + e);
      }
   }

   public void send_poll(ClientRequestInfo ri)
   {
      // do nothing
   }

   public void receive_reply(ClientRequestInfo ri)
   {
      try
      {
         ServiceContext sc = ri.get_reply_service_context(sasContextId);
         Any msg = codec.decode_value(sc.context_data,
            SASContextBodyHelper.type());
         SASContextBody contextBody = SASContextBodyHelper.extract(msg);

         // At this point contextBody should contain a 
         // CompleteEstablishContext message, which does not require any 
         // treatment. ContextError messages should arrive via 
         // receive_exception().

         if (traceEnabled)
            log.trace("receive_reply: got SAS reply, type " +
                      contextBody.discriminator());

         if (contextBody.discriminator() == MTContextError.value)
         {
            // should not happen
            log.warn("Unexpected ContextError in SAS reply");
            throw new NO_PERMISSION("Unexpected ContextError in SAS reply",
               MinorCodes.SAS_CSS_FAILURE,
               CompletionStatus.COMPLETED_YES);
         }
      }
      catch (BAD_PARAM e)
      {
         // no service context with sasContextId: do nothing
      }
      catch (FormatMismatch e)
      {
         throw new MARSHAL("Could not parse SAS reply: " + e,
            0,
            CompletionStatus.COMPLETED_YES);
      }
      catch (TypeMismatch e)
      {
         throw new MARSHAL("Could not parse SAS reply: " + e,
            0,
            CompletionStatus.COMPLETED_YES);
      }
   }

   public void receive_exception(ClientRequestInfo ri)
   {
      try
      {
         ServiceContext sc = ri.get_reply_service_context(sasContextId);
         Any msg = codec.decode_value(sc.context_data,
            SASContextBodyHelper.type());
         SASContextBody contextBody = SASContextBodyHelper.extract(msg);

         // At this point contextBody may contain a either a 
         // CompleteEstablishContext message or a ContextError message.
         // Neither message requires any treatment. We decoded the context
         // body just to check that it contains a well-formed message.

         if (traceEnabled)
            log.trace("receive_exceptpion: got SAS reply, type " +
                      contextBody.discriminator());
      }
      catch (BAD_PARAM e)
      {
         // no service context with sasContextId: do nothing
      }
      catch (FormatMismatch e)
      {
         throw new MARSHAL("Could not parse SAS reply: " + e,
            MinorCodes.SAS_CSS_FAILURE,
            CompletionStatus.COMPLETED_MAYBE);
      }
      catch (TypeMismatch e)
      {
         throw new MARSHAL("Could not parse SAS reply: " + e,
            MinorCodes.SAS_CSS_FAILURE,
            CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public void receive_other(ClientRequestInfo ri)
   {
      // do nothing
   }

   CompoundSecMech getSecurityMech(ClientRequestInfo ri)
   {
      CompoundSecMechList csmList = null;
      CompoundSecMech securityMech = null;
      try
      {
          TaggedComponent tc = ri.get_effective_component(TAG_CSI_SEC_MECH_LIST.value);

          Any any = codec.decode_value(tc.component_data,
             CompoundSecMechListHelper.type());

          csmList = CompoundSecMechListHelper.extract(any);

          // at this point you can inspect the fields csmList.stateful 
          // and csmList.mechanism_list. The latter is an array of
          // org.omg.CSIIOP.CompoundSecMech instances, which in our IORs 
          // has length 1. 
          //
          // The actual info you want is in csmList.mechanism_list[0].
         securityMech = csmList.mechanism_list[0];
      }
      catch (BAD_PARAM e)
      {
          // no component with TAG_CSI_SEC_MECH_LIST was found
      }
      catch (org.omg.IOP.CodecPackage.TypeMismatch tm)
      {
          // unexpected exception in codec.decode_value
      }
      catch (org.omg.IOP.CodecPackage.FormatMismatch tm)
      {
          // unexpected exception in codec.decode_value
      }
      return securityMech;
   }
}
