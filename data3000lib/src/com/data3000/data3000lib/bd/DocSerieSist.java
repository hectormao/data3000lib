package com.data3000.data3000lib.bd;
// Generated 11/03/2017 10:49:44 AM by Hibernate Tools 3.5.0.Final

import java.util.Date;

/**
 * DocSerieSist generated by hbm2java
 */
public class DocSerieSist implements java.io.Serializable {

	private long serieSistIdn;
	private DocSerieDoc docSerieDoc;
	private DocSistArch docSistArch;
	private String audiUsuario;
	private Date audiFechModi;
	private boolean audiSiAnul;
	private String audiMotiAnul;
	private String audiChecksum;

	public DocSerieSist() {
	}

	public DocSerieSist(long serieSistIdn, DocSerieDoc docSerieDoc, DocSistArch docSistArch, String audiUsuario,
			Date audiFechModi, boolean audiSiAnul) {
		this.serieSistIdn = serieSistIdn;
		this.docSerieDoc = docSerieDoc;
		this.docSistArch = docSistArch;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
	}

	public DocSerieSist(long serieSistIdn, DocSerieDoc docSerieDoc, DocSistArch docSistArch, String audiUsuario,
			Date audiFechModi, boolean audiSiAnul, String audiMotiAnul, String audiChecksum) {
		this.serieSistIdn = serieSistIdn;
		this.docSerieDoc = docSerieDoc;
		this.docSistArch = docSistArch;
		this.audiUsuario = audiUsuario;
		this.audiFechModi = audiFechModi;
		this.audiSiAnul = audiSiAnul;
		this.audiMotiAnul = audiMotiAnul;
		this.audiChecksum = audiChecksum;
	}

	public long getSerieSistIdn() {
		return this.serieSistIdn;
	}

	public void setSerieSistIdn(long serieSistIdn) {
		this.serieSistIdn = serieSistIdn;
	}

	public DocSerieDoc getDocSerieDoc() {
		return this.docSerieDoc;
	}

	public void setDocSerieDoc(DocSerieDoc docSerieDoc) {
		this.docSerieDoc = docSerieDoc;
	}

	public DocSistArch getDocSistArch() {
		return this.docSistArch;
	}

	public void setDocSistArch(DocSistArch docSistArch) {
		this.docSistArch = docSistArch;
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