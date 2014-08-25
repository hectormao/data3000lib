package com.data3000.data3000lib.ngc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.List;

import org.zkoss.util.media.Media;

import com.data3000.admin.ngc.PlataformaNgc;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoArchivo;
import com.data3000.data3000lib.dao.SistemaArchivoDAO;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class SistemaArchivoNgcImpl implements SistemaArchivoNgc{

	private SistemaArchivoDAO sistemaArchivoDAO;
	
	private PlataformaNgc plataformaNgc;
	
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
	
	@Override
	public DocArchivo getArchivo(DocSistArch directorio, String nombreArchivo) throws Exception {
		
		return sistemaArchivoDAO.getArchivo(directorio, nombreArchivo);
	}


	@Override
	public void cargarArchivo(DocArchivo docArchivo, DocArchivoVersion version, byte[] data, List<DocCampArch> listaMeta) throws Exception {
		
		
		sistemaArchivoDAO.registrarArchivo(docArchivo,version, listaMeta);
		
		try{
			Path archivo = getRutaArchivo(version);
			
			Files.write(archivo, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
			
			//calcular checksum 
			String checksum = calcularChecksum(data);
			version.setArchVersChecksum(checksum);
			//actualizar ruta de la version
			version.setArchVersRuta(archivo.toAbsolutePath().toString());
			
			sistemaArchivoDAO.update(version);
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
	
	private String calcularChecksum(byte[] data) throws Exception{
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
			String nombreVersion = new StringBuilder("VER_").append(version.getArchVersIdn()).toString();
			
			Path archivo = Paths.get(rutaArchivos, nombreDirectorio, nombreArchivo, nombreVersion, version.getDocArchivo().getArchNombre());
			if(! Files.exists(archivo.getParent())){
				Files.createDirectories(archivo.getParent());
			}
			
			
			return archivo;
			
		} else {
			return Paths.get(version.getArchVersRuta());
		}
		
	}
	
	@Override
	public List<DocTipoArchivo> getTipos() throws Exception {
	
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
	public void anularTipoDocumentos(DocTipoArchivo docTipoArchivo) throws Exception {
		this.sistemaArchivoDAO.anularTipoDocumentos(docTipoArchivo);
		
	}


}
