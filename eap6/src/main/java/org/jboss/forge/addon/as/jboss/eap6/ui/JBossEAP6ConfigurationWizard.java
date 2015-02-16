/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.jboss.eap6.ui;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.as.jboss.common.JBossConfiguration;
import org.jboss.forge.addon.as.jboss.common.ui.JBossConfigurationWizard;
import org.jboss.forge.addon.as.jboss.eap6.JBossEAP6Configuration;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;

/**
 * The JBoss EAP6 Configuration Wisard
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class })
public class JBossEAP6ConfigurationWizard extends JBossConfigurationWizard
{

   @Inject
   private JBossEAP6Configuration config;

   private DependencyBuilder jbossEAP6Dist = DependencyBuilder.create()
            .setGroupId("org.jboss.as")
            .setArtifactId("jboss-eap-dist")
            .setVersion("[6.0.0,7.0.0[")
            .setPackaging("zip");

   @Override
   protected JBossConfiguration getConfig()
   {
      return config;
   }

   @Override
   protected DependencyBuilder getJBossDistribution()
   {
      return jbossEAP6Dist;
   }
   
}