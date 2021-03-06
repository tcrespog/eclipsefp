package net.sf.eclipsefp.haskell.debug.core.internal.debug;

import net.sf.eclipsefp.haskell.debug.core.internal.launch.ILaunchAttributes;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;

/**
 * Delegate source path computer: get the source paths for the project,
 * obtained from the project name attribute of the launch configuration
 * @author JP Moresmau
 *
 */
public class HaskellSourcePathComputerDelegate implements
    ISourcePathComputerDelegate {

  /**
   * we only return the project, since getSourceName() now returns the file relative to the project
   * since we may have the same file name (Main.hs) in different folders
   */
  @Override
  public ISourceContainer[] computeSourceContainers(
      final ILaunchConfiguration configuration, final IProgressMonitor monitor )
      throws CoreException {
    String projectName=configuration.getAttribute( ILaunchAttributes.PROJECT_NAME ,""); //$NON-NLS-1$
    IProject p=ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
    ISourceContainer[] ret=new ISourceContainer[1];
    ret[0] = new FolderSourceContainer(p, true);
   /* Collection<IContainer> cs=ResourceUtil.getSourceFolders( p );
    ISourceContainer[] ret=new ISourceContainer[cs.size()];
    int a=0;
    for (IContainer c:cs){
      ISourceContainer sourceContainer=null;
      if (c.getType() == IResource.PROJECT) {
        sourceContainer = new ProjectSourceContainer((IProject)c, false);
      } else if (c.getType() == IResource.FOLDER) {
        sourceContainer = new FolderSourceContainer(c, true);
      }
      if (sourceContainer!=null){
        ret[a++]=sourceContainer;
      }
    }*/

    return ret;
  }

}
