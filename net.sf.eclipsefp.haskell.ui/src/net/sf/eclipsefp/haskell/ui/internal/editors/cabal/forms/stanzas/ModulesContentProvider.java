/**
 * (c) 2011, Alejandro Serrano
 * Released under the condidtions of the EPL.
 */
package net.sf.eclipsefp.haskell.ui.internal.editors.cabal.forms.stanzas;

import java.util.ArrayList;
import java.util.Vector;
import net.sf.eclipsefp.haskell.core.cabalmodel.CabalSyntax;
import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescriptionStanza;
import org.apache.tools.ant.util.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ModulesContentProvider implements ITreeContentProvider {

  public class ModulesVisitor implements IResourceVisitor {

    public ArrayList<String> elts;
    public Vector<String> possiblePrefixes;

    public ModulesVisitor( final ArrayList<String> whereAdd,
        final Vector<String> dirs ) {
      this.elts = whereAdd;
      this.possiblePrefixes = new Vector<String>();
      for( String dir: dirs ) {
        this.possiblePrefixes.add( dir.trim() + "/" );
      }
    }

    public boolean visit( final IResource resource ) {
      String path = resource.getProjectRelativePath().toString();
      if( resource instanceof IFile ) {
        for( String dir: possiblePrefixes ) {
          if( path.startsWith( dir ) ) {
            String filePath = path.substring( dir.length() );
            if( filePath.endsWith( ".hs" ) ) {
              String module = filePath.substring( 0, filePath.length() - 3 ).replace( '/', '.' );
              this.elts.add( module );
            } else if( filePath.endsWith( ".lhs" ) ) {
              String module = filePath.substring( 0, filePath.length() - 4 ).replace( '/', '.' );
              this.elts.add( module );
            }
          }
        }
      }
      return true;
    }
  }

  public String[] elements;

  public void dispose() {
    // Do nothing
  }

  public void inputChanged( final Viewer viewer, final Object oldInput,
      final Object newInput ) {
    if( newInput == null || !( newInput instanceof ModulesContentProviderRoot ) ) {
      this.elements = new String[ 0 ];
    } else {
      try {
        ModulesContentProviderRoot root = ( ModulesContentProviderRoot )newInput;
        String sourceDirs = null;
        if( root.getStanza().getProperties()
            .containsKey( CabalSyntax.FIELD_HS_SOURCE_DIRS.getCabalName() ) ) {
          sourceDirs = root.getStanza().getProperties()
              .get( CabalSyntax.FIELD_HS_SOURCE_DIRS.getCabalName() );
        } else {
          sourceDirs = "";
        }

        ArrayList<String> modules = new ArrayList<String>();
        ModulesVisitor visitor = new ModulesVisitor( modules,
            StringUtils.split( sourceDirs, ',' ) );
        root.getProject().accept( visitor );

        PackageDescriptionStanza pkg = root.getDescription().getPackageStanza();
        if( pkg != null ) {
          if( pkg.getProperties().containsKey( CabalSyntax.FIELD_DATA_FILES ) ) {
            // There are data files, so Paths_package is also provided
            modules.add( "Paths_"
                + pkg.getProperties().get( CabalSyntax.FIELD_NAME ) );
          }
        }

        this.elements = modules.toArray( new String[ modules.size() ] );
      } catch( Throwable ex ) {
        this.elements = new String[ 0 ];
      }
    }
  }

  public Object[] getElements( final Object inputElement ) {
    return elements;
  }

  public Object[] getChildren( final Object parentElement ) {
    return new Object[ 0 ];
  }

  public Object getParent( final Object element ) {
    return null;
  }

  public boolean hasChildren( final Object element ) {
    return false;
  }

}
