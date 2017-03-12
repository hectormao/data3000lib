package com.data3000.data3000lib.ngc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.data3000.admin.bd.PltUsuario;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoAlma;

public interface SistemaArchivoNgc {
	/**
	 * Obtiene los directorios hijos
	 * @param padre
	 * @param usuario 
	 * @return
	 */
	public List<DocSistArch> getHijos(DocSistArch padre, PltUsuario usuario)  throws Exception;

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
	public List<DocCampTipo> getCamposTipo(DocSerieDoc docTipoArchivo) throws Exception;
	
	/**
	 * Registra en la base de datos un nuevo tipo de documento
	 * @param docTipoArchivo
	 * @param listaCrear
	 * @param listaActualizar
	 * @param listaEliminar
	 * @param listaCrearCampo
	 * @throws Exception
	 */
	public void registrarTipoDocumentos(DocSerieDoc docTipoArchivo, List<Object> listaCrear, List<Object> listaActualizar, List<Object> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception;
	
	/**
	 * Actualiza en la base de datos un tipo de documento
	 * @param docTipoArchivo
	 * @param listaCrear
	 * @param listaActualizar
	 * @param listaEliminar
	 * @param listaCrearCampo
	 * @throws Exception
	 */
	public void actualizarTipoDocumentos(DocSerieDoc docTipoArchivo, List<Object> listaCrear, List<Object> listaActualizar, List<Object> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception;
	
	/**
	 * Obtiene los tipos de documentos registrados
	 * @return
	 */
	public List<DocSerieDoc> getTipos() throws Exception;
	
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
	 * @param permisos 
	 * @param archivo
	 */
	public void cargarArchivo(DocArchivo docArchivo, DocArchivoVersion version, byte[] data, List<DocCampArch> listaMeta, List<DocAcl> permisos) throws Exception;
	
	/**
	 * Anula o elimina un tipo de archivo del sistema
	 * @param docTipoArchivo
	 * @throws Exception
	 */
	public void anularTipoDocumentos(DocSerieDoc docTipoArchivo) throws Exception ;

	/**
	 * Crea un nuevo directorio
	 * @param directorio
	 * @param permisos
	 */
	public void registrarDirectorio(DocSistArch directorio,	List<DocAcl> permisos) throws Exception;
	
	
	
	/**
	 * Calcula el checksum de un archivo
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String calcularChecksum(byte[] data) throws Exception;
	
	/**
	 * Modifica los datos de un directorio
	 * @param directorio
	 * @param permisos
	 */
	public void modificarDirectorio(DocSistArch directorio, List<DocAcl> permisosNuevos, List<DocAcl> permisosEdicion, List<DocAcl> permisosEliminacion) throws Exception;
	
	/**
	 * Obtiene un directorio a partir de su id
	 * @param sistArchIdn
	 * @return
	 */
	public DocSistArch getDirectorio(long sistArchIdn)  throws Exception;

	/**
	 * Anula un directorio
	 * @param directorio
	 */
	public void anularDirectorio(DocSistArch directorio) throws Exception;

	/**
	 * Copiar un archivo a un destino
	 * @param archivo
	 * @param nuevoNombre
	 * @param destino
	 * @throws Exception
	 */
	public void copiarArchivo(DocArchivo archivo, String nuevoNombre, DocSistArch destino) throws Exception;

	
	/**
	 * Copiar un directorio a un destino
	 * @param directorio
	 * @param nuevoNombre
	 * @param destino
	 * @throws Exception
	 */
	public void copiarDirectorio(DocSistArch directorio, String nuevoNombre, DocSistArch destino)  throws Exception;
	
	/**
	 * Obtiene un directorio hijo que tenga ese nombre
	 * @param destino
	 * @param nombre
	 * @return
	 */
	public DocSistArch getHijo(DocSistArch destino, String nombre)  throws Exception ;
	
	
	/**
	 * Obtiene la ruta absoluta del archivo a partir del id de la version
	 * @param idVersion
	 * @return
	 */
	public Path getArchivo(Long idVersion)  throws IOException;
	
	/**
	 * Obtiene una serie Documental por su id
	 * @param idSerie
	 * @return
	 * @throws IOException
	 */
	public DocSerieDoc getSerieDoc(long idSerie) throws IOException;
	
	/**
	 * Obtiene las series hijas
	 * @param padre
	 * @param usuario 
	 * @return
	 */
	public List<DocSerieDoc> getHijosSerie(DocSerieDoc padre, PltUsuario usuario)  throws Exception;
	
	
	/**
	 * Obtiene los tipos de almacenamiento de una serie documental
	 * @param docSerieDoc
	 * @return
	 */
	public List<DocTipoAlma> getTiposAlmacenamientoSerie(DocSerieDoc docSerieDoc);

	
	
}
