package com.data3000.data3000lib.cnt;


import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocArchivoVersion;

public class VerArchivoCnt extends WindowComposer {
	
	private Window winVerArchivo;
	private Iframe frmContenido;
	
	private DocArchivoVersion version;
	
	@Override
	public void doAfterCompose(Window winVerArchivo) throws Exception{		
		super.doAfterCompose(winVerArchivo);
		version = (DocArchivoVersion) argumentos.get(ConstantesAdmin.ARG_SELECCION);
		if(version != null){
			frmContenido.setSrc(new StringBuilder("GetArchivo?idVersion=").append(version.getArchVersIdn()).toString());
		}
		
	}

}
