package com.data3000.data3000lib.bd;

// Generated 29/04/2014 06:39:48 PM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.data3000.admin.utl.CampoTabla;
import com.data3000.admin.utl.ConstantesAdmin;

/**
 * DocArchivoVersion generated by hbm2java
 */
public class DocArchivoVersion implements java.io.Serializable {

	private long archVersIdn;
	private DocArchivo docArchivo;
	private String archVersTag;
	private String archVersRuta;
	private String archVersChecksum;
	private String archVersDescripcion;
	private String audiUsuario;
	private Date audiFechModi;
	private boolean audiSiAnul;
	private String audiMotiAnul;
	private String audiChecksum;

	public DocArchivoVersion() {
	}

	public DocArchivoVersion(long archVersIdn, DocArchivo docArchivo,
			String archVersTag, String archVersRuta, String archVersChecksum,
			String audiUsuario, Date audiFechModi, boolean audiSiAnul) {
		this.archVersIdn = archVersIdn;
		this.docArchivo = docArchivo;
		this.archVersTag = archVersTag;
		this.archVersRuta = archVersRuta;
		this.archVersChecksum = archVersChecksum;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
	}

	public DocArchivoVersion(long archVersIdn, DocArchivo docArchivo,
			String archVersTag, String archVersRuta, String archVersChecksum,
			String archVersDescripcion, String audiUsuario, Date audiFechModi,
			boolean audiSiAnul, String audiMotiAnul, String audiChecksum) {
		this.archVersIdn = archVersIdn;
		this.docArchivo = docArchivo;
		this.archVersTag = archVersTag;
		this.archVersRuta = archVersRuta;
		this.archVersChecksum = archVersChecksum;
		this.archVersDescripcion = archVersDescripcion;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
		this.audiMotiAnul = audiMotiAnul;
		this.audiChecksum = audiChecksum;
	}

	public long getArchVersIdn() {
		return this.archVersIdn;
	}

	public void setArchVersIdn(long archVersIdn) {
		this.archVersIdn = archVersIdn;
	}

	public DocArchivo getDocArchivo() {
		return this.docArchivo;
	}

	public void setDocArchivo(DocArchivo docArchivo) {
		this.docArchivo = docArchivo;
	}

	public String getArchVersTag() {
		return this.archVersTag;
	}

	public void setArchVersTag(String archVersTag) {
		this.archVersTag = archVersTag;
	}

	public String getArchVersRuta() {
		return this.archVersRuta;
	}

	public void setArchVersRuta(String archVersRuta) {
		this.archVersRuta = archVersRuta;
	}

	public String getArchVersChecksum() {
		return this.archVersChecksum;
	}

	public void setArchVersChecksum(String archVersChecksum) {
		this.archVersChecksum = archVersChecksum;
	}

	public String getArchVersDescripcion() {
		return this.archVersDescripcion;
	}

	public void setArchVersDescripcion(String archVersDescripcion) {
		this.archVersDescripcion = archVersDescripcion;
	}

	public String getAudiUsuario() {
		return this.audiUsuario;
	}

	public void setAudiUsuario(String audiUsuario) {
		this.audiUsuario = audiUsuario;
	}

	public Date getAudiFechModi() {
		return this.audiFechModi;
	}

	public void setAudiFechModi(Date audiFechModi) {
		this.audiFechModi = audiFechModi;
	}

	public boolean isAudiSiAnul() {
		return this.audiSiAnul;
	}

	public void setAudiSiAnul(boolean audiSiAnul) {
		this.audiSiAnul = audiSiAnul;
	}

	public String getAudiMotiAnul() {
		return this.audiMotiAnul;
	}

	public void setAudiMotiAnul(String audiMotiAnul) {
		this.audiMotiAnul = audiMotiAnul;
	}

	public String getAudiChecksum() {
		return this.audiChecksum;
	}

	public void setAudiChecksum(String audiChecksum) {
		this.audiChecksum = audiChecksum;
	}
	
	public static List<CampoTabla> getCamposTabla() {
		
		String[] camposMostrar = new String[]{"archVersTag","archVersDescripcion","audiUsuario","audiFechModi"};

		List<CampoTabla> camposTabla = new ArrayList<CampoTabla>();
		for(String campoMostrar : camposMostrar) {
			CampoTabla campo = new CampoTabla(campoMostrar);
			camposTabla.add(campo);
		}

		return camposTabla;
	}

}
