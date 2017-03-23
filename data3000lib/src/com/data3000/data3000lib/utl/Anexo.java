package com.data3000.data3000lib.utl;

import java.nio.file.Path;

import com.data3000.data3000lib.bd.DocArchivoVersion;

public class Anexo extends DocArchivoVersion {
	
	private String nombreArchivo;
	private Path archivoTmp;
	
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public Path getArchivoTmp() {
		return archivoTmp;
	}
	public void setArchivoTmp(Path archivoTmp) {
		this.archivoTmp = archivoTmp;
	}
	
	

}
