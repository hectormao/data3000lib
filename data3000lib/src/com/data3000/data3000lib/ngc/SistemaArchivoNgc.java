package com.data3000.data3000lib.ngc;

import java.util.List;

import org.zkoss.util.media.Media;

import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
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
	
	/**
	 * Obtiene los tipos de documentos registrados
	 * @return
	 */
	public List<DocTipoArchivo> getTipos() throws Exception;
	
	/**
	 * Obtiene un archivo por su nombre en un directorio
	 * @param directorio
	 * @param nombreArchivo
	 * @return
	 */
	public DocArchivo getArchivo(DocSistArch directorio, String nombreArchivo) throws Exception;
	
	/**
	 * carga un archivo en la plataforma
	 * @param docArchivo
	 * @param version
	 * @param archivo
	 */
	public void cargarArchivo(DocArchivo docArchivo, DocArchivoVersion version, byte[] data, List<DocCampArch> listaMeta) throws Exception;
}
