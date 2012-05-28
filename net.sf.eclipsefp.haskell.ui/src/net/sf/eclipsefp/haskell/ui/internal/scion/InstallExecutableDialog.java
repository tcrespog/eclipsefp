/**
 * Copyright (c) 2012 by JP Moresmau
 * This code is made available under the terms of the Eclipse Public License,
 * version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
 */
package net.sf.eclipsefp.haskell.ui.internal.scion;


import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import net.sf.eclipsefp.haskell.ui.internal.preferences.IPreferenceConstants;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import net.sf.eclipsefp.haskell.ui.util.HaskellUIImages;
import net.sf.eclipsefp.haskell.ui.util.IImageNames;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The dialog telling the user that she's missing some executables, and giving the option to install them
 * @author JP Moresmau
 *
 */
public class InstallExecutableDialog extends Dialog {
  private boolean buildWrapper=true;
  private boolean scionBrowser=true;
  private Button bUser;
  private Button bIgnore;

  public InstallExecutableDialog( final Shell parentShell,final boolean buildWrapper,final boolean scionBrowser ) {
    super( parentShell );
    this.buildWrapper=buildWrapper;
    this.scionBrowser=scionBrowser;
  }



  @Override
  protected void createButtonsForButtonBar( final Composite parent ) {
    super.createButtonsForButtonBar( parent );
    getButton( OK ).setText( UITexts.executablesmissing_install );
  }

  protected String getTitle(){
    return UITexts.executablesmissing_title;
  }

  protected String getMessage1(){
    return UITexts.executablesmissing_message1;
  }

  protected String getMessage2(){
    return UITexts.executablesmissing_message2;
  }


  @Override
  protected void configureShell( final Shell newShell ) {
    super.configureShell( newShell );
    newShell.setText( getTitle() );
    newShell.setImage( HaskellUIImages.getImage( IImageNames.HASKELL_MISC ) );
  }

  @Override
  protected Control createDialogArea( final Composite parent ) {
    Composite c=(Composite)super.createDialogArea( parent );
    ((GridLayout)c.getLayout()).numColumns=2;
    String msg=null;
    if (buildWrapper){
      if (scionBrowser){
        msg=NLS.bind( getMessage2(), "buildwrapper","scion-browser" );
      } else {
        msg=NLS.bind( getMessage1(), "buildwrapper");
      }
    } else {
      msg=NLS.bind( getMessage1(), "scion-browser");
    }
    Label l=new Label(c,SWT.NONE);
    l.setText( msg );
    GridData gd=new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan=2;
    l.setLayoutData( gd );

    bUser=new Button(c,SWT.CHECK);
    bUser.setText( UITexts.executablesmissing_user );

    bIgnore=new Button(c,SWT.CHECK);
    bIgnore.setText( UITexts.executablesmissing_ignore);

    return c;
  }

  protected String getIgnorePreference(){
    return IPreferenceConstants.IGNORE_MISSING_EXECUTABLE;
  }

  private void setIgnoreFlag(){
    IPreferenceStore prefs = HaskellUIPlugin.getDefault().getPreferenceStore();
    prefs.setValue( getIgnorePreference(), bIgnore.getSelection() );

  }

  @Override
  public boolean close() {
    setIgnoreFlag();
    return super.close();
  }

  @Override
  protected void okPressed() {
    final InstallExecutableRunnable j=new InstallExecutableRunnable();
    j.setBuildWrapper( buildWrapper );
    j.setCabalUpdate( true );
    j.setGlobal( !bUser.getSelection() );
    j.setScionBrowser( scionBrowser );
    new Thread(j).start();
    super.okPressed();
  }

}