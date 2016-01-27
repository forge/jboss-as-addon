/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.as.jboss.common.util;

import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FilesTest
{

   /**
    * Test method for {@link org.jboss.forge.addon.as.jboss.common.util.Files#createPath(java.lang.String[])}.
    */
   @Test
   public void testCreatePath()
   {
      Assume.assumeTrue(OperatingSystemUtils.isLinux());
      String javaHome = "/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/";
      String path = Files.createPath(javaHome, "bin", "java");
      Assert.assertEquals(javaHome + "/bin/java", path);
   }

}
