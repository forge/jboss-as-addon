/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.jboss.wf.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.as.jboss.common.JBossConfiguration;
import org.jboss.forge.addon.as.jboss.common.ui.JBossConfigurationWizard;
import org.jboss.forge.addon.as.jboss.wf.WildFlyConfiguration;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;

/**
 * The WildFly 8 Configuration Wisard
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class })
public class WildFlyConfigurationWizard extends JBossConfigurationWizard
{

   @Inject
   private WildFlyConfiguration config;

   private DependencyBuilder wildfly8Dist = DependencyBuilder.create()
            .setGroupId("org.wildfly")
            .setArtifactId("wildfly-dist")
            .setPackaging("zip");

   @Override
   protected JBossConfiguration getConfig()
   {
      return config;
   }

   @Override
   protected DependencyBuilder getJBossDistribution()
   {
      return wildfly8Dist;
   }

}
