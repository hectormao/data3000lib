package com.data3000.data3000lib.bd;
// Generated 11/03/2017 08:16:33 AM by Hibernate Tools 3.5.0.Final

import java.util.Date;

import com.data3000.admin.bd.PltRol;
import com.data3000.admin.bd.PltUsuario;

/**
 * DocAcl generated by hbm2java
 */
public class DocAcl implements java.io.Serializable {

	private long aclIdn;
	private PltRol pltRol;
	private PltUsuario pltUsuario;
	private DocSistArch docSistArch;
	private DocArchivo docArchivo;
	private boolean aclSiLectura;
	private boolean aclSiEscritura;
	private String audiUsuario;
	private Date audiFechModi;
	private boolean audiSiAnul;
	private String audiMotiAnul;
	private String audiChecksum;

	public DocAcl() {
	}

	public DocAcl(long aclIdn, boolean aclSiLectura, boolean aclSiEscritura, String audiUsuario, Date audiFechModi,
			boolean audiSiAnul) {
		this.aclIdn = aclIdn;
		this.aclSiLectura = aclSiLectura;
		this.aclSiEscritura = aclSiEscritura;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
	}

	public DocAcl(long aclIdn, PltRol pltRol, PltUsuario pltUsuario, DocSistArch docSistArch, DocArchivo docArchivo,
			boolean aclSiLectura, boolean aclSiEscritura, String audiUsuario, Date audiFechModi, boolean audiSiAnul,
			String audiMotiAnul, String audiChecksum) {
		this.aclIdn = aclIdn;
		this.pltRol = pltRol;
		this.pltUsuario = pltUsuario;
		this.docSistArch = docSistArch;
		this.docArchivo = docArchivo;
		this.aclSiLectura = aclSiLectura;
		this.aclSiEscritura = aclSiEscritura;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
		this.audiMotiAnul = audiMotiAnul;
		this.audiChecksum = audiChecksum;
	}

	public long getAclIdn() {
		return this.aclIdn;
	}

	public void setAclIdn(long aclIdn) {
		this.aclIdn = aclIdn;
	}

	public PltRol getPltRol() {
		return this.pltRol;
	}

	public void setPltRol(PltRol pltRol) {
		this.pltRol = pltRol;
	}

	public PltUsuario getPltUsuario() {
		return this.pltUsuario;
	}

	public void setPltUsuario(PltUsuario pltUsuario) {
		this.pltUsuario = pltUsuario;
	}

	public DocSistArch getDocSistArch() {
		return this.docSistArch;
	}

	public void setDocSistArch(DocSistArch docSistArch) {
		this.docSistArch = docSistArch;
	}

	public DocArchivo getDocArchivo() {
		return this.docArchivo;
	}

	public void setDocArchivo(DocArchivo docArchivo) {
		this.docArchivo = docArchivo;
	}

	public boolean isAclSiLectura() {
		return this.aclSiLectura;
	}

	public void setAclSiLectura(boolean aclSiLectura) {
		this.aclSiLectura = aclSiLectura;
	}

	public boolean isAclSiEscritura() {
		return this.aclSiEscritura;
	}

	public void setAclSiEscritura(boolean aclSiEscritura) {
		this.aclSiEscritura = aclSiEscritura;
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

}
