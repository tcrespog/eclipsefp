package net.sf.eclipsefp.haskell.scion.internal.servers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import net.sf.eclipsefp.haskell.scion.client.ScionInstance;
import net.sf.eclipsefp.haskell.scion.client.ScionPlugin;
import net.sf.eclipsefp.haskell.scion.client.ScionServerEventType;
import net.sf.eclipsefp.haskell.scion.exceptions.ScionServerStartupException;
import net.sf.eclipsefp.haskell.scion.internal.util.Trace;
import net.sf.eclipsefp.haskell.scion.internal.util.ScionText;
import net.sf.eclipsefp.haskell.util.PlatformUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWTException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation of {@link ScionServer} using standard in/out to communicate
 * with Scion
 * 
 * @author JP Moresmau
 * @author B. Scott Michel (scooter.phd@gmail.com)
 */
public class StdStreamScionServer extends ScionServer {
  /** The input receiver Job */
  private InputReceiver       inputReceiver;
  /** scion-server response prefix */
  private static final String PREFIX = "scion:";

  public StdStreamScionServer(IProject project, IPath serverExecutable, Writer serverOutput, File directory) {
    super(project, serverExecutable, serverOutput, directory);
  }

  protected synchronized void doStartServer(IProject project) throws ScionServerStartupException {
    List<String> command = new LinkedList<String>();
    command.add(serverExecutable.toOSString());
    command.add("-i ");

    // Launch the process
    ProcessBuilder builder = new ProcessBuilder(command);
    if (directory != null && directory.exists()) {
      builder.directory(directory);
    }
    builder.redirectErrorStream(true);
    try {
      process = builder.start();
    } catch (Throwable ex) {
      throw new ScionServerStartupException(ScionText.scionServerCouldNotStart_message, ex);
    }
    // Connect to the process's stdout to capture messages
    // Assume that status messages and such will be UTF8 only
    try {
      serverOutStream = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF8"));
      serverInStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "UTF8"));

      String receiverName = getClass().getSimpleName() + "/" + project;
      inputReceiver = new InputReceiver(receiverName);
      inputReceiver.start();
    } catch (UnsupportedEncodingException ex) {
      // make compiler happy, because UTF8 is always supported
    }
  }

  @Override
  public void doStopServer() {
    if (inputReceiver != null) {
      // Stop the input receiver.
      try {
        inputReceiver.setTerminate();
        inputReceiver.interrupt();
        inputReceiver.join();
      } catch (InterruptedException e) {
        // Nothing to do.
      }
    }
  }

  /**
   * Notify the {@link ScionInstance}'s listeners that an abnormal termination
   * just happened
   */
  private void signalAbnormalTermination() {
    ScionInstance scionInstance = ScionPlugin.getScionInstance(project);
    assert (scionInstance != null);
    scionInstance.notifyListeners(ScionServerEventType.ABNORMAL_TERMINATION);
  }

  /**
   * The input receiver thread.
   * 
   * This thread reads the scion-server's standard input and error, looking for
   * input lines that start with {@link StdStreamScionServer#PREFIX}. Matching
   * lines are sent to {@link ScionServer#processResponse(JSONObject)}. All
   * input is echoed to the {@link ScionServer#serverOutput} output stream.
   */
  public class InputReceiver extends Thread {
    private boolean terminateFlag;

    public InputReceiver(String name) {
      super(name);
      terminateFlag = false;
    }

    public void setTerminate() {
      terminateFlag = true;
    }

    @Override
    public void run() {
      while (!terminateFlag && serverOutStream != null) {
        JSONObject response;
        String responseString = new String();
        // long t0=System.currentTimeMillis();
        try {
          responseString = serverOutStream.readLine();
          if (responseString != null) {
            if (responseString.startsWith(PREFIX)) {
              response = new JSONObject(new JSONTokener(responseString.substring(PREFIX.length())));

              Trace.trace(serverName, FROM_SERVER_PREFIX.concat(response.toString()));
              serverOutput.write(FROM_SERVER_PREFIX.concat(response.toString()).concat(PlatformUtil.NL));
              serverOutput.flush();

              processResponse(response);
            } else {
              serverOutput.write(responseString + PlatformUtil.NL);
              serverOutput.flush();
            }
          } else {
            serverOutput.write(ScionText.scionServerGotEOF_message + PlatformUtil.NL);
            serverOutput.flush();
            Trace.trace(serverName, ScionText.scionServerGotEOF_message);
            stopServer();
            signalAbnormalTermination();
          }
        } catch (JSONException ex) {
          try {
            serverOutput.write(ScionText.scionJSONParseException_message + PlatformUtil.NL);
            serverOutput.write(responseString + PlatformUtil.NL);
            ex.printStackTrace(new PrintWriter(serverOutput));
            serverOutput.flush();

            ScionPlugin.logError(ScionText.scionJSONParseException_message, ex);
            stopServer();
            signalAbnormalTermination();
          } catch (IOException ex2) {
            // If we can't write to serverOutput...
          }
        } catch (IOException ex) {
          if (!terminateFlag) {
            ScionPlugin.logError(ScionText.scionServerNotRunning_message, ex);
            signalAbnormalTermination();
          }
        } catch (SWTException se) {
          // probably device has been disposed
          if (!terminateFlag) {
            ScionPlugin.logError(ScionText.scionServerNotRunning_message, se);
            signalAbnormalTermination();
          }
        }
        // long t1=System.currentTimeMillis();
        // System.err.println("receive+parse:"+(t1-t0));
      }
    }
  }
}