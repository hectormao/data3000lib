package com.data3000.data3000lib.ngc;

import java.util.List;

import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoArchivo;
import com.data3000.data3000lib.dao.SistemaArchivoDAO;

public class SistemaArchivoNgcImpl implements SistemaArchivoNgc{

	SistemaArchivoDAO sistemaArchivoDAO;
	
	@Override
	public List<DocSistArch> getHijos(DocSistArch padre) throws Exception{
		
		if(padre == null){
			return sistemaArchivoDAO.getHijosRaiz();
		} else {
			return sistemaArchivoDAO.getHijos(padre);
		}
		
		
		
	}

	
	@Override
	public List<DocCampo> getCampos() throws Exception {
		
		return sistemaArchivoDAO.getCampos();
	}
	
	@Override
	public void registrarTipoDocumentos(DocTipoArchivo docTipoArchivo, List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar, List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
		sistemaArchivoDAO.registrarTipoDocumentos(docTipoArchivo, listaCrear, listaActualizar, listaEliminar, listaCrearCampo);
		
	}


	@Override
	public void actualizarTipoDocumentos(DocTipoArchivo docTipoArchivo, List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar, List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
		sistemaArchivoDAO.actualizarTipoDocumentos(docTipoArchivo, listaCrear, listaActualizar, listaEliminar, listaCrearCampo);
		
	}
	
	
	@Override
	public List<DocCampTipo> getCamposTipo(DocTipoArchivo docTipoArchivo) throws Exception {
		return sistemaArchivoDAO.getCamposTipo(docTipoArchivo);
	}
	
	public SistemaArchivoDAO getSistemaArchivoDAO() {
		return sistemaArchivoDAO;
	}

	public void setSistemaArchivoDAO(SistemaArchivoDAO sistemaArchivoDAO) {
		this.sistemaArchivoDAO = sistemaArchivoDAO;
	}


	


	

	
	
	
	

}
