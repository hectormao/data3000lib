package com.data3000.data3000lib.ngc;

import java.util.List;

import com.data3000.data3000lib.bd.DocSistArch;
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

	public SistemaArchivoDAO getSistemaArchivoDAO() {
		return sistemaArchivoDAO;
	}

	public void setSistemaArchivoDAO(SistemaArchivoDAO sistemaArchivoDAO) {
		this.sistemaArchivoDAO = sistemaArchivoDAO;
	}
	
	
	

}
