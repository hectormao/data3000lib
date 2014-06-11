package com.data3000.data3000lib.ngc;

import java.util.List;

import com.data3000.data3000lib.bd.DocSistArch;

public interface SistemaArchivoNgc {
	/**
	 * Obtiene los directorios hijos
	 * @param padre
	 * @return
	 */
	public List<DocSistArch> getHijos(DocSistArch padre)  throws Exception;
}
