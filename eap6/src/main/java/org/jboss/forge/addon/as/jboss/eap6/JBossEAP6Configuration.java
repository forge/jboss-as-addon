/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.jboss.eap6;

import org.jboss.forge.addon.as.jboss.common.JBossConfiguration;

/**
 * The JBoss EAP 6 Configuration
 * 
 * @author Jeremie Lagarde
 */
public class JBossEAP6Configuration extends JBossConfiguration
{
   private static final String ASNAME = "eap6";

   /**
    * The default version
    */
   private static final String DEFAULT_VERSION = "6.3.0.Final";

   /**
    * The default path
    */
   private static final String DEFAULT_PATH = "target/jboss-eap6-dist";

   /**
    * The default port
    */
   static final int DEFAULT_PORT = 9999;

   @Override
   protected String getASName()
   {
      return ASNAME;
   }

   @Override
   protected String getDefaultVersion()
   {
      return DEFAULT_VERSION;
   }

   @Override
   public String getDefaultPath()
   {
      return DEFAULT_PATH;
   }

   @Override
   protected int getDefaultPort()
   {
      return DEFAULT_PORT;
   }

}
