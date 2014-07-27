package com.data3000.data3000lib.ngc;

import java.util.List;

import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoArchivo;

public interface SistemaArchivoNgc {
	/**
	 * Obtiene los directorios hijos
	 * @param padre
	 * @return
	 */
	public List<DocSistArch> getHijos(DocSistArch padre)  throws Exception;

	/**
	 * Obtiene los campos ya registrados en el sistema ordenados por su nombre
	 * @return
	 */
	public List<DocCampo> getCampos() throws Exception;

	/**
	 * obtiene los campos reslacionados a este tipo de archivo ordenados por su orden
	 * @param docTipoArchivo
	 * @return
	 * @throws Exception
	 */
	public List<DocCampTipo> getCamposTipo(DocTipoArchivo docTipoArchivo) throws Exception;
	
	/**
	 * Registra en la base de datos un nuevo tipo de documento
	 * @param docTipoArchivo
	 * @param listaCrear
	 * @param listaActualizar
	 * @param listaEliminar
	 * @param listaCrearCampo
	 * @throws Exception
	 */
	public void registrarTipoDocumentos(DocTipoArchivo docTipoArchivo, List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar, List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception;
	
	/**
	 * Actualiza en la base de datos un tipo de documento
	 * @param docTipoArchivo
	 * @param listaCrear
	 * @param listaActualizar
	 * @param listaEliminar
	 * @param listaCrearCampo
	 * @throws Exception
	 */
	public void actualizarTipoDocumentos(DocTipoArchivo docTipoArchivo, List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar, List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception;
}
