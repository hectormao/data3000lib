package com.data3000.data3000lib.ngc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.data3000.admin.bd.PltRol;
import com.data3000.admin.bd.PltUsuaRol;
import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.ngc.PlataformaNgc;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSerieSist;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoAlma;
import com.data3000.data3000lib.dao.SistemaArchivoDAO;
import com.data3000.data3000lib.utl.Anexo;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class SistemaArchivoNgcImpl implements SistemaArchivoNgc{

	private SistemaArchivoDAO sistemaArchivoDAO;
	
	private PlataformaNgc plataformaNgc;
	
	@Override
	public List<DocSistArch> getHijos(DocSistArch padre, PltUsuario usuario, String buscar) throws Exception{
		
		List<DocSistArch> directorios = null;
		
		if(buscar == null){
		
			if(padre == null){
				directorios = sistemaArchivoDAO.getHijosRaiz();
			} else {
				directorios = sistemaArchivoDAO.getHijos(padre);
			}
		} else {
			List<DocSistArch> filtro = sistemaArchivoDAO.getSistemaPorNombre(buscar);
			
			Map<Long,DocSistArch> resultadoFiltro = new HashMap<>();
			
			for(DocSistArch dirEncontrado : filtro){
				DocSistArch hijo = getHijoPorFiltro(padre,dirEncontrado);
				if(hijo != null){
					resultadoFiltro.put(hijo.getSistArchIdn(), hijo);
				}
			}
			
			directorios = new ArrayList<>();
						
			if(! resultadoFiltro.isEmpty()){
				directorios.addAll(resultadoFiltro.values());
				Collections.sort(directorios, new Comparator<DocSistArch>() {

					@Override
					public int compare(DocSistArch o1, DocSistArch o2) {						
						return o1.getSistArchNombre().toUpperCase().compareTo(o2.getSistArchNombre().toUpperCase());
					}
					
				});
			}
		}
		List<DocSistArch> directoriosPermiso = new ArrayList<DocSistArch>();
		
		for(DocSistArch directorio : directorios){
			
			if(directorio.getPltUsuario().getUsuaIdn() == usuario.getUsuaIdn()){
				directoriosPermiso.add(directorio);
			} else {
				Set<DocAcl> permisos = directorio.getDocAcls();
				for(DocAcl permiso : permisos){
					PltUsuario usuarioPermiso = permiso.getPltUsuario();
					PltRol rolPermiso = permiso.getPltRol();
					if(usuarioPermiso != null && usuarioPermiso.getUsuaIdn() == usuario.getUsuaIdn()){
						if(permiso.isAclSiLectura()){
							directoriosPermiso.add(directorio);
							break;
						}
					} else if(rolPermiso != null){
						
						Set<PltUsuaRol> roles = usuario.getPltUsuaRols();
						boolean encontro = false;
						for(PltUsuaRol usuarioRol : roles){
							if(rolPermiso.getRolIdn() == usuarioRol.getPltRol().getRolIdn() && permiso.isAclSiLectura()){
								directoriosPermiso.add(directorio);
								encontro = true;
								break;
							}
						}
						if(encontro){
							break;
						}
						
					}
				}
			}
			
		}
		
		return directoriosPermiso;
	}

	
	private DocSistArch getHijoPorFiltro(DocSistArch padre, DocSistArch dir) throws Exception{
		
		if(padre == null && dir.getDocSistArch() == null){
			return dir;
		} else if(padre != null && dir.getDocSistArch() == null){
			return null;
		} else if(padre != null && dir.getDocSistArch() != null && padre.getSistArchIdn() == dir.getDocSistArch().getSistArchIdn()){
			return dir;
		} else{	
			DocSistArch padreDir = sistemaArchivoDAO.getDirectorio(dir.getDocSistArch().getSistArchIdn());
			if(padreDir.isAudiSiAnul()){
				return null;
			} else {
				return getHijoPorFiltro(padre, padreDir);
			}
			
		}
	}


	@Override
	public List<DocCampo> getCampos() throws Exception {
		
		return sistemaArchivoDAO.getCampos();
	}
	
	@Override
	public void registrarTipoDocumentos(DocSerieDoc docTipoArchivo, List<Object> listaCrear, List<Object> listaActualizar, List<Object> listaEliminar, List<DocCampo> listaCrearCampo, DocSerieSist serieSistema) throws Exception {
		sistemaArchivoDAO.registrarTipoDocumentos(docTipoArchivo, listaCrear, listaActualizar, listaEliminar, listaCrearCampo, serieSistema);
		
	}


	@Override
	public void actualizarTipoDocumentos(DocSerieDoc docTipoArchivo, List<Object> listaCrear, List<Object> listaActualizar, List<Object> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
		sistemaArchivoDAO.actualizarTipoDocumentos(docTipoArchivo, listaCrear, listaActualizar, listaEliminar, listaCrearCampo);
		
	}
	
	
	
	
	@Override
	public List<DocCampTipo> getCamposTipo(DocSerieDoc docTipoArchivo) throws Exception {
		return sistemaArchivoDAO.getCamposTipo(docTipoArchivo);
	}
	
	@Override
	public DocArchivo getArchivo(DocSistArch directorio, String nombreArchivo) throws Exception {
		
		return sistemaArchivoDAO.getArchivo(directorio, nombreArchivo);
	}


	@Override
	public void cargarArchivo(DocArchivo docArchivo, DocArchivoVersion version, byte[] data, List<DocCampArch> listaMeta, List<DocAcl> permisos, List<Anexo> anexos) throws Exception {
		
		
		sistemaArchivoDAO.registrarArchivo(docArchivo,version, listaMeta, permisos, anexos);
		
		try{
			Path archivo = getRutaArchivo(version);
			
			Files.write(archivo, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
			
			//calcular checksum 
			String checksum = calcularChecksum(data);
			version.setArchVersChecksum(checksum);
			//actualizar ruta de la version
			version.setArchVersRuta(archivo.toAbsolutePath().toString());
			
			
			
			for(Anexo anexo : anexos){
				Path ruta = getRutaArchivo(anexo);
				Files.move(anexo.getArchivoTmp(), ruta);
				
				byte[] dataAnx = Files.readAllBytes(ruta);				
				//calcular checksum 
				String checksumAnx = calcularChecksum(dataAnx);
				anexo.setArchVersChecksum(checksumAnx);
				//actualizar ruta de la version
				anexo.setArchVersRuta(ruta.toAbsolutePath().toString());
				
			}
			
			sistemaArchivoDAO.modificarVersion(version,anexos);
			
		} catch (Exception e) {
			//eliminar version
			sistemaArchivoDAO.delete(version);
			try{
				sistemaArchivoDAO.delete(docArchivo);
			} catch(Exception e2){
				// si no se puede eliminar no pasa nada se deja el archivo
				;
			}
			
			throw e;
		}
		
		
		
		
	}
	
	public String calcularChecksum(byte[] data) throws Exception{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		byte[] digestBytes = messageDigest.digest(data);
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < digestBytes.length; i++) {
			sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
	}
	
	private Path getRutaArchivo(DocArchivoVersion version) throws Exception{
		if(version.getArchVersRuta().equalsIgnoreCase("XXX")){
			
			String rutaArchivos = plataformaNgc.getEnv(ConstantesData3000.RUTA_ARCHIVOS);
			
			String nombreDirectorio = new StringBuilder("DIR_").append(version.getDocArchivo().getDocSistArch().getSistArchIdn()).toString();
			String nombreArchivo = new StringBuilder("ARC_").append(version.getDocArchivo().getArchIdn()).toString();
			
			
			Long idVersion = null;
			String nombre = null;
			
			if(version instanceof Anexo){
				DocArchivoVersion padre = version.getDocArchivoVersion();
				// es un anexo entonces tomo el id de version del padre
				idVersion = padre.getArchVersIdn();
				nombre = ((Anexo)version).getNombreArchivo();
			} else {
				// es el archivo normal entonces tomo el id de la version
				idVersion = version.getArchVersIdn();
				nombre = version.getDocArchivo().getArchNombre();
			}
			String nombreVersion = new StringBuilder("VER_").append(idVersion).toString();
			
			
			
			
			Path archivo = Paths.get(rutaArchivos, nombreDirectorio, nombreArchivo, nombreVersion, nombre);
			if(! Files.exists(archivo.getParent())){
				Files.createDirectories(archivo.getParent());
			}
			
			
			return archivo;
			
		} else {
			return Paths.get(version.getArchVersRuta());
		}
		
	}
	
	@Override
	public List<DocSerieDoc> getTipos() throws Exception {
	
		return sistemaArchivoDAO.getTipos();
	}
	
	public SistemaArchivoDAO getSistemaArchivoDAO() {
		return sistemaArchivoDAO;
	}

	public void setSistemaArchivoDAO(SistemaArchivoDAO sistemaArchivoDAO) {
		this.sistemaArchivoDAO = sistemaArchivoDAO;
	}


	public PlataformaNgc getPlataformaNgc() {
		return plataformaNgc;
	}


	public void setPlataformaNgc(PlataformaNgc plataformaNgc) {
		this.plataformaNgc = plataformaNgc;
	}


	@Override
	public void anularTipoDocumentos(DocSerieDoc docTipoArchivo) throws Exception {
		this.sistemaArchivoDAO.anularTipoDocumentos(docTipoArchivo);
		
	}


	@Override
	public long registrarDirectorio(DocSistArch directorio, List<DocAcl> permisos) throws Exception {
		
		return sistemaArchivoDAO.registrarDirectorio(directorio,permisos);
		
		
	}


	@Override
	public void modificarDirectorio(DocSistArch directorio, List<DocAcl> permisosNuevos, List<DocAcl> permisosEdicion, List<DocAcl> permisosEliminacion, List<DocAcl> permisosAnterior) throws Exception {
		sistemaArchivoDAO.modificarDirectorio(directorio,permisosNuevos,permisosEdicion,permisosEliminacion,permisosAnterior);
		
	}


	@Override
	public DocSistArch getDirectorio(long sistArchIdn) throws Exception{
		
		return sistemaArchivoDAO.getDirectorio(sistArchIdn);
	}


	@Override
	public void anularDirectorio(DocSistArch directorio) throws Exception {
		sistemaArchivoDAO.anularDirectorio(directorio);
		
	}


	@Override
	public void copiarArchivo(DocArchivo archivo, String nuevoNombre, DocSistArch destino) throws Exception {		
		
		List<DocArchivoVersion> listaVersiones = sistemaArchivoDAO.getVersionesArchivo(archivo);
		List<Path> origenes = new ArrayList<Path>();
		List<Path> destinos = new ArrayList<Path>();
		List<DocCampArch> listaCampos = new ArrayList<DocCampArch>();
		List<DocAcl> listaPermisos = sistemaArchivoDAO.getPermisosArchivo(archivo);
		archivo.setArchIdn(0L);
		archivo.setArchNombre(nuevoNombre);
		archivo.setDocSistArch(destino);
		for(DocArchivoVersion version : listaVersiones){			
			List<DocCampArch> camposVersion = sistemaArchivoDAO.getCamposVersion(version);
			
			version.setArchVersIdn(0L);
			version.setDocArchivo(archivo);
			
			for(DocCampArch campo : camposVersion){
				campo.setCampArchIdn(0L);
				campo.setDocArchivoVersion(version);
				listaCampos.add(campo);
			}
			
			//copiando los archivos
			Path pathOrigen = getRutaArchivo(version);
			origenes.add(pathOrigen);
			
			version.setArchVersRuta("XXX");
			
			Path pathDestino = getRutaArchivo(version);
			destinos.add(pathDestino);			
			
			
			version.setArchVersRuta(pathDestino.toAbsolutePath().toString());
		}
		
		for(DocAcl permiso : listaPermisos){			
			permiso.setAclIdn(0L);
			permiso.setDocArchivo(archivo);			
		}
		
		sistemaArchivoDAO.copiarArchivo(archivo,listaVersiones,listaCampos,listaPermisos);
		for(int i = 0; i < origenes.size(); i++){
			
			Path pathOrigen = origenes.get(i);
			Path pathDestino = destinos.get(i);
			
			Files.copy(pathOrigen, pathDestino);
			
		}
		
	}


	@Override
	public void copiarDirectorio(DocSistArch directorio, String nuevoNombre, DocSistArch destino) throws Exception {		
		
	}


	@Override
	public DocSistArch getHijo(DocSistArch destino, String nombre) throws Exception {		
		return sistemaArchivoDAO.getDirectorio(destino,nombre);
	}


	@Override
	public Path getArchivo(Long idVersion) throws IOException{
		try{
			DocArchivoVersion version = sistemaArchivoDAO.getVersionArchivo(idVersion);
			if(version == null){
				return null;
			} else {
				Path path = Paths.get(version.getArchVersRuta());
				return path;
			}
		} catch(Exception ex){
			throw new IOException(ex);
		}
	}


	@Override
	public DocSerieDoc getSerieDoc(long idSerie) throws IOException {
		
		return sistemaArchivoDAO.getSerieDoc(idSerie);
	}


	@Override
	public List<DocSerieDoc> getHijosSerie(DocSerieDoc padre, PltUsuario usuario) throws Exception {
		if(padre == null){
			return sistemaArchivoDAO.getHijosRaizSerie();
		} else {
			return sistemaArchivoDAO.getHijosSerie(padre);
		}
	}
	
	@Override
	public List<DocSerieDoc> getSeriesEntidad(DocSistArch entidad)  throws Exception {
		
		return sistemaArchivoDAO.getHijosRaizSerie(entidad);
		
	}


	@Override
	public List<DocTipoAlma> getTiposAlmacenamientoSerie(DocSerieDoc docSerieDoc) {
		
		return sistemaArchivoDAO.getTiposAlmacenamientoSerie(docSerieDoc);
	}


	@Override
	public List<DocSerieDoc> getSeriesDirectorio(DocSistArch dir) {
		if(dir == null){
			return null;
		}
		
		List<DocSerieDoc> resultado =  sistemaArchivoDAO.getSeriesDirectorio(dir);
		if(resultado != null && ! resultado.isEmpty() ){
			
			Collections.sort(resultado, new Comparator<DocSerieDoc>() {

				@Override
				public int compare(DocSerieDoc o1, DocSerieDoc o2) {
					
					int cmp = o1.getSerieDocCodigo().compareTo(o2.getSerieDocCodigo());
					if(cmp == 0){
						cmp = o1.getSerieDocNombre().compareTo(o2.getSerieDocNombre());
					}
					return cmp;
				}
			});
			
			return resultado;
		} else {
			DocSistArch padre = dir.getDocSistArch();
			if(padre == null){
				return null;
			} else {
				return getSeriesDirectorio(padre);
			}
		}
	}
	
	
	
	

	@Override
	public List<DocSistArch> getEntidades() {
		
		List<DocSistArch> listaEntidades = sistemaArchivoDAO.getEntidades(); 
		
		if(listaEntidades != null && ! listaEntidades.isEmpty()){
			Collections.sort(listaEntidades, new Comparator<DocSistArch>() {

				@Override
				public int compare(DocSistArch o1, DocSistArch o2) {					
					return o1.getSistArchNombre().toUpperCase().compareTo(o2.getSistArchNombre().toUpperCase());
				}
			});
		}
		
		return listaEntidades;
	}


	@Override
	public List<DocSerieDoc> getSeriesDocumentales() {
		return sistemaArchivoDAO.getSeriesDocumentales();
	}


	@Override
	public void RegistrarSeriesSistema(DocSerieSist docSerieSist) {
		sistemaArchivoDAO.registrarSeriesSistema(docSerieSist);
	}


	@Override
	public List<DocAcl> getObtenerPermisosRol(long id) {
		return sistemaArchivoDAO.ConsultarPermisos(id);
	}


	@Override
	public List<DocSerieSist> getSeriesSisEditar(long sisArchi) {
		return sistemaArchivoDAO.consultarSeriesSeleccionadas(sisArchi);
	}


	@Override
	public void ClearSeriesDocumentales(List<DocSerieSist> lista) {
		sistemaArchivoDAO.clearSeriesSistema(lista);
		
	}


	@Override
	public void registrarAcl(DocAcl acl) throws Exception{
		sistemaArchivoDAO.save(acl);		
		
	}
	
	@Override
	public void eliminarAcl(DocAcl acl) throws Exception{
		sistemaArchivoDAO.delete(acl);		
		
	}


	@Override
	public List<DocAcl> getAccesoEntidadesUsuario(PltUsuario usu) {
		
		return sistemaArchivoDAO.getAclUsuarioEntidades(usu);
	}
	


}
