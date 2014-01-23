// Copyright (c) 2003-2008 by Leif Frenzel. All rights reserved.
// This code is made available under the terms of the Eclipse Public License,
// version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
package net.sf.eclipsefp.haskell.debug.core.internal.launch;

/** <p>contains constant name definitions for launch configuration
  * attributes.</p>
  *
  * @author Leif Frenzel
  * @author Alejandro Serrano
  *
  */
public interface ILaunchAttributes {

  String EMPTY = ""; //$NON-NLS-1$

  String RUN_IN_BACKGROUND = "RUN_IN_BACKGROUND"; //$NON-NLS-1$
  String WORKING_DIRECTORY = "WORKING_DIRECTORY";   //$NON-NLS-1$
  String ARGUMENTS         = "ARGUMENTS"; //$NON-NLS-1$
  String EXTRA_ARGUMENTS   = "EXTRA_ARGUMENTS"; //$NON-NLS-1$
  String EXECUTABLE        = "EXECUTABLE"; //$NON-NLS-1$
  String PROJECT_NAME      = "PROJECT_NAME"; //$NON-NLS-1$
  String FILES             = "FILES"; //$NON-NLS-1$
  String DELEGATE          = "DELEGATE"; //$NON-NLS-1$
  String STANZA            = "STANZA"; //$NON-NLS-1$
  String RTS_ARGUMENTS     = "RTS_ARGUMENTS"; //$NON-NLS-1$

  String SYNC_STREAMS      = "SYNC_STREAMS"; //$NON-NLS-1$
  String RELOAD_COMMAND    = "RELOAD_COMMAND";//$NON-NLS-1$
  String RELOAD            = "RELOAD";//$NON-NLS-1$
  String COMMAND           = "COMMAND";//$NON-NLS-1$
  String COMMAND_ON_RELOAD = "COMMAND_ON_RELOAD";//$NON-NLS-1$

  String NEEDS_HTTP_PROXY  = "NEEDS_HTTP_PROXY";//$NON-NLS-1$
  /**
   * start time of the process
   */
  String START_TIME  = "START_TIME";//$NON-NLS-1$
}