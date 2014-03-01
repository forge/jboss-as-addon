/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.jboss.common.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class Server<MODELCONTROLLERCLIENT>
{
   private final ScheduledExecutorService timerService;
   private final ServerInfo serverInfo;
   private Process process;
   private ConsoleConsumer console;
   private final String shutdownId;

   protected Server(final ServerInfo serverInfo)
   {
      this(serverInfo, null);
   }

   protected Server(final ServerInfo serverInfo, final String shutdownId)
   {
      this.serverInfo = serverInfo;
      this.shutdownId = shutdownId;
      timerService = Executors.newScheduledThreadPool(1);
   }

   /**
    * The console that is associated with the server.
    * 
    * @return the console
    */
   protected final ConsoleConsumer getConsole()
   {
      return console;
   }

   /**
    * Starts the server.
    * 
    * @throws IOException the an error occurs creating the process
    */
   public final synchronized void start() throws IOException
   {
      SecurityActions.registerShutdown(this);
      final List<String> cmd = createLaunchCommand();
      final ProcessBuilder processBuilder = new ProcessBuilder(cmd);
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();
      console = startConsoleConsumer(process.getInputStream(), shutdownId);
      long timeout = serverInfo.getStartupTimeout() * 1000;
      boolean serverAvailable = false;
      long sleep = 50;
      init();
      while (timeout > 0 && !serverAvailable)
      {
         serverAvailable = isRunning();
         if (!serverAvailable)
         {
            if (processHasDied(process))
               break;
            try
            {
               Thread.sleep(sleep);
            }
            catch (InterruptedException e)
            {
               serverAvailable = false;
               break;
            }
            timeout -= sleep;
            sleep = Math.max(sleep / 2, 100);
         }
      }
      if (serverAvailable)
      {
         timerService.scheduleWithFixedDelay(new Reaper(), 20, 10, TimeUnit.SECONDS);
      }
      else
      {
         destroyProcess();
         throw new IllegalStateException(String.format("Managed server was not started within [%d] s",
                  serverInfo.getStartupTimeout()));
      }
   }

   /**
    * Stops the server.
    */
   public final synchronized void stop()
   {
      try
      {
         stopServer();
      }
      finally
      {
         if (process != null)
         {
            process.destroy();
            try
            {
               process.waitFor();
            }
            catch (InterruptedException ignore)
            {
               // no-op
            }
         }
         timerService.shutdown();
      }
   }

   /**
    * Invokes any optional initialization that should take place after the process has been launched. Note the server
    * may not be completely started when the method is invoked.
    * 
    * @throws IOException if an IO error occurs
    */
   protected abstract void init() throws IOException;

   /**
    * Stops the server before the process is destroyed. A no-op override will just destroy the process.
    */
   protected abstract void stopServer();

   /**
    * Checks the status of the server and returns {@code true} if the server is fully started.
    * 
    * @return {@code true} if the server is fully started, otherwise {@code false}
    */
   public abstract boolean isRunning();

   /**
    * Returns the client that used to execute management operations on the server.
    * 
    * @return the client to execute management operations
    */
   public abstract MODELCONTROLLERCLIENT getClient();

   /**
    * Creates the command to launch the server for the process.
    * 
    * @return the commands used to launch the server
    */
   protected abstract List<String> createLaunchCommand();

   /**
    * Checks whether the server is running or not. If the server is no longer running the {@link #isRunning()} should
    * return {@code false}.
    */
   public abstract void checkServerState();

   private int destroyProcess()
   {
      if (process == null)
         return 0;
      process.destroy();
      try
      {
         return process.waitFor();
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static boolean processHasDied(final Process process)
   {
      try
      {
         process.exitValue();
         return true;
      }
      catch (IllegalThreadStateException e)
      {
         // good
         return false;
      }
   }

   private ConsoleConsumer startConsoleConsumer(final InputStream stream, final String shutdownId)
   {
      final ConsoleConsumer result = new ConsoleConsumer(stream, shutdownId);
      final Thread t = new Thread(result);
      t.setDaemon(true);
      t.start();
      return result;
   }

   private class Reaper implements Runnable
   {

      @Override
      public void run()
      {
         checkServerState();
         if (!isRunning())
         {
            stop();
         }
      }
   }

   /**
    * Runnable that consumes the output of the process.
    * 
    * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
    */
   public class ConsoleConsumer implements Runnable
   {

      private final InputStream in;
      private final String shutdownId;
      private final CountDownLatch latch;

      protected ConsoleConsumer(final InputStream in, final String shutdownId)
      {
         this.in = in;
         latch = new CountDownLatch(1);
         this.shutdownId = shutdownId;
      }

      @Override
      public void run()
      {

         try
         {
            byte[] buf = new byte[512];
            int num;
            while ((num = in.read(buf)) != -1)
            {
               serverInfo.getOut().write(buf, 0, num);
               if (shutdownId != null && new String(buf).contains(shutdownId))
               {
                  latch.countDown();
                  if (isRunning())
                  {
                     stop();
                  }
               }
            }

            serverInfo.getOut().flush();
         }
         catch (IOException ignore)
         {
         }
      }

      public void awaitShutdown(final long seconds) throws InterruptedException
      {
         if (shutdownId == null)
            latch.countDown();
         latch.await(seconds, TimeUnit.SECONDS);
      }

   }
}