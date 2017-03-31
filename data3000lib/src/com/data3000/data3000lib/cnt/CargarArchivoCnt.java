package com.data3000.data3000lib.cnt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.data3000.admin.bd.PltRol;
import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.ngc.PlataformaNgc;
import com.data3000.admin.ngc.UsuarioNgc;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.TipoCampo;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.Anexo;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class CargarArchivoCnt extends WindowComposer implements ListitemRenderer<DocCampTipo> {

	/**
	 * BEANS
	 */
	
	private UsuarioNgc usuarioNgc;
	private SistemaArchivoNgc sistemaArchivoNgc;

	private Window winCargarArchivo;
	private Tree trSerie;
	private Listbox lstMeta;
	private Textbox txtArchivo;
	private Textbox txtDescripcion;
	private Textbox txtTagVersion;
	private Tree trPermisos;
	private Listbox lstAnexo;

	private Media archivo;

	private DocSistArch directorio;

	private Logger logger = Logger.getLogger(this.getClass());
	
	
	private String rutaTmp;
	
	class AnexoTmp {
		String nombreArchivo;
		Path archivoTmp;
	}
	
	@Override
	public void doAfterCompose(Window winTipoDocumento) throws Exception {
		super.doAfterCompose(winTipoDocumento);

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		txtTagVersion.setValue(sdf.format(new Date()));

		directorio = (DocSistArch) argumentos.get(ConstantesAdmin.OBJETO_PADRE);
		
		
		rutaTmp = plataformaNgc.getEnv(ConstantesData3000.RUTA_TMP);
		
		

	}

	private void cargarArbolSeries() throws Exception {
		
		List<DocSerieDoc> series = sistemaArchivoNgc.getSeriesDirectorio(directorio);
		
		Treechildren hijosRaiz = new Treechildren();
		trSerie.appendChild(hijosRaiz);
		
		for(DocSerieDoc serie : series){
			Treeitem tiSerie = new Treeitem();
			tiSerie.setLabel(serie.toString());
			tiSerie.setTooltiptext(serie.getSerieDocDescripcion());
			tiSerie.setValue(serie);
			hijosRaiz.appendChild(tiSerie);
			
			Treechildren hijosSerie = new Treechildren();
			tiSerie.appendChild(hijosSerie);
			
			cargarArbolSeries(hijosSerie,serie);
		}
		
		
	}
	
	
	public void onClick$btnNuevoAnexo(Event evt){
		
		final Listitem li = new Listitem();
		
		Listcell cldArchivo = new Listcell();
		final Textbox txtNombreArchivo = new Textbox();
		txtNombreArchivo.setWidth("90%");
		txtNombreArchivo.setReadonly(true);
		cldArchivo.appendChild(txtNombreArchivo);		
		li.appendChild(cldArchivo);
		
		Listcell cldBoton = new Listcell();
		final Button btnCarga = new Button();
		btnCarga.setWidth("80%");
		btnCarga.setUpload("zk.Data3000UF");
		btnCarga.setLabel("...");
		cldBoton.appendChild(btnCarga);
		
		btnCarga.addEventListener(Events.ON_UPLOAD, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				
				UploadEvent evtUpd = (UploadEvent) arg0;
				
				Media archivo = evtUpd.getMedia();
				
				txtNombreArchivo.setValue(archivo.getName());
				
				Path tmp = Paths.get(rutaTmp);
				
				if(! Files.exists(tmp)){
					Files.createDirectories(tmp);
				}
				
				Path ruta = Files.createTempFile(tmp,"ANX_", ".tmp");
				
				AnexoTmp anexo = new AnexoTmp();
				anexo.archivoTmp = ruta;
				anexo.nombreArchivo = archivo.getName();
				
				Files.write(ruta, archivo.getByteData(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				
				li.setValue(anexo);
				
				btnCarga.setDisabled(true);
				
				
				
			}
		});
		
		
		li.appendChild(cldBoton);
		
		lstAnexo.appendChild(li);
		
		
	}
	
	public void onClick$btnEliminarAnexo(Event evt) throws Exception{
		
		int idx = 0;
		while(idx < lstAnexo.getItemCount()){
			
			Listitem li = lstAnexo.getItemAtIndex(idx);
			
			if(li.isSelected()){
			
				AnexoTmp anexo = li.getValue();
				if(anexo != null){
					Path ruta = anexo.archivoTmp;
					Files.deleteIfExists(ruta);
				}
				
				lstAnexo.removeChild(li);
				idx = 0;
			} else {
				idx ++;
			}
		}
		
		
	}
	

	private void cargarArbolSeries(Treechildren hijosSerie, DocSerieDoc serie) throws Exception {
		List<DocSerieDoc> series = sistemaArchivoNgc.getHijosSerie(serie, (PltUsuario) usuario);
		if(series == null || (series != null && series.isEmpty())){		
			return;
		}
		
		for(DocSerieDoc serieHija : series){
			Treeitem tiSerieHija = new Treeitem();
			tiSerieHija.setLabel(serieHija.toString());
			tiSerieHija.setTooltiptext(serieHija.getSerieDocDescripcion());
			tiSerieHija.setValue(serieHija);
			hijosSerie.appendChild(tiSerieHija);
			
			Treechildren hijosSerieHija = new Treechildren();
			tiSerieHija.appendChild(hijosSerieHija);
			
			cargarArbolSeries(hijosSerieHija,serieHija);
		}
		
	}

	private void cargarArbolPermisos() throws Exception {

		Treechildren hijosRaiz = new Treechildren();
		trPermisos.appendChild(hijosRaiz);

		Treeitem itemRol = new Treeitem();
		Treerow filaRol = new Treerow();
		Treecell celdaNombreRol = new Treecell(Labels.getLabel("app.rol"));
		celdaNombreRol.setStyle("font-weight:bold;");
		filaRol.appendChild(celdaNombreRol);
		itemRol.appendChild(filaRol);
		hijosRaiz.appendChild(itemRol);

		Treeitem itemUsuario = new Treeitem();
		Treerow filaUsuario = new Treerow();
		Treecell celdaNombreUsuario = new Treecell(
				Labels.getLabel("app.usuario"));
		celdaNombreUsuario.setStyle("font-weight:bold;");
		filaUsuario.appendChild(celdaNombreUsuario);
		itemUsuario.appendChild(filaUsuario);
		hijosRaiz.appendChild(itemUsuario);

		trPermisos.appendChild(hijosRaiz);

		agregarRoles(itemRol);

		agregarUsuarios(itemUsuario);

	}

	private void agregarUsuarios(Treeitem item) throws Exception {
		List<PltUsuario> usuarios = usuarioNgc
				.getUsuariosDiferentesA((PltUsuario) usuario);
		Treechildren hijos = new Treechildren();
		for (PltUsuario usuario : usuarios) {
			Treeitem itemUsuario = new Treeitem();
			Treerow filaUsuario = new Treerow();

			itemUsuario.setValue(usuario);

			StringBuilder nombre = new StringBuilder(usuario.getUsuaNombre());
			nombre.append(" (");
			nombre.append(usuario.getLogin());
			nombre.append(")");

			Treecell celdaNombre = new Treecell(nombre.toString());
			filaUsuario.appendChild(celdaNombre);

			final Checkbox chkSiLectura = new Checkbox();
			Treecell celdaSiLectura = new Treecell();
			celdaSiLectura.appendChild(chkSiLectura);
			filaUsuario.appendChild(celdaSiLectura);

			final Checkbox chkSiEscritura = new Checkbox();
			Treecell celdaSiEscritura = new Treecell();
			celdaSiEscritura.appendChild(chkSiEscritura);
			filaUsuario.appendChild(celdaSiEscritura);

			chkSiEscritura.addEventListener(Events.ON_CHECK,
					new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							if (chkSiEscritura.isChecked()) {
								chkSiLectura.setChecked(true);
								chkSiLectura.setDisabled(true);
							} else {
								chkSiLectura.setDisabled(false);
							}

						}
					});

			itemUsuario.setAttribute("chkSiLectura", chkSiLectura);
			itemUsuario.setAttribute("chkSiEscritura", chkSiEscritura);

			itemUsuario.appendChild(filaUsuario);
			hijos.appendChild(itemUsuario);

		}

		item.appendChild(hijos);

	}

	private void agregarRoles(Treeitem item) throws Exception {
		List<PltRol> roles = plataformaNgc.getRoles();
		Treechildren hijos = new Treechildren();
		for (PltRol rol : roles) {
			Treeitem itemRol = new Treeitem();
			Treerow filarol = new Treerow();

			itemRol.setValue(rol);

			Treecell celdaNombre = new Treecell(rol.getRolNombre());
			filarol.appendChild(celdaNombre);

			final Checkbox chkSiLectura = new Checkbox();
			Treecell celdaSiLectura = new Treecell();
			celdaSiLectura.appendChild(chkSiLectura);
			filarol.appendChild(celdaSiLectura);

			final Checkbox chkSiEscritura = new Checkbox();
			Treecell celdaSiEscritura = new Treecell();
			celdaSiEscritura.appendChild(chkSiEscritura);
			filarol.appendChild(celdaSiEscritura);

			chkSiEscritura.addEventListener(Events.ON_CHECK,
					new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							if (chkSiEscritura.isChecked()) {
								chkSiLectura.setChecked(true);
								chkSiLectura.setDisabled(true);
							} else {
								chkSiLectura.setDisabled(false);
							}

						}
					});

			itemRol.setAttribute("chkSiLectura", chkSiLectura);
			itemRol.setAttribute("chkSiEscritura", chkSiEscritura);

			itemRol.appendChild(filarol);
			hijos.appendChild(itemRol);

		}

		item.appendChild(hijos);

	}

	public void onCreate$winCargarArchivo(Event evt) {
		try {
			if (directorio == null) {
				throw new Exception(Labels.getLabel("error.1002"));
			}
			
			cargarArbolPermisos();
			
			cargarArbolSeries();
		} catch (Exception ex) {
			logger.error(
					new StringBuilder("Error al abrir cargar archivo: ")
							.append(ex.getClass().getName()).append(" - ")
							.append(ex.getMessage()).toString(), ex);
			Messagebox.show(ex.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
			Events.sendEvent(Events.ON_CLOSE, winCargarArchivo, null);
		}
	}

	public void onSelect$trSerie(Event evt) throws Exception {
		Treeitem ci = trSerie.getSelectedItem();
		if (ci != null) {
			DocSerieDoc tipo = ci.getValue();
			List<DocCampTipo> campos = sistemaArchivoNgc.getCamposTipo(tipo);

			ListModel<DocCampTipo> modeloCampos = lstMeta.getModel();
			if (modeloCampos == null) {
				modeloCampos = new ListModelList<DocCampTipo>();
				lstMeta.setModel(modeloCampos);
				lstMeta.setItemRenderer(this);
			}

			((ListModelList<DocCampTipo>) modeloCampos).clear();
			((ListModelList<DocCampTipo>) modeloCampos).addAll(campos);

		}
	}

	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}

	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}

	

	@Override
	public void render(Listitem li, DocCampTipo campoTipo, int idx)
			throws Exception {

		DocCampo campo = campoTipo.getDocCampo();

		Listcell celdaLabel = new Listcell(campo.getCampoNombre());
		celdaLabel.setTooltiptext(campo.getCampoDescripcion());

		li.appendChild(celdaLabel);

		Listcell celdaCampo = new Listcell();
		TipoCampo tipoCampo = TipoCampo.getTipoPorID(campo.getCampoTipo());
		Component cmp = tipoCampo
				.getComponente(campoTipo.isCampTipoRequerido());
		celdaCampo.appendChild(cmp);
		li.appendChild(celdaCampo);
		li.setAttribute("campo", cmp);

		li.setValue(campoTipo);

	}

	public void onUpload$btnCargar(UploadEvent evt) throws Exception {

		archivo = evt.getMedia();

		txtArchivo.setValue(archivo.getName());

	}

	public void onClick$btnAceptar(Event evt) throws Exception {

		if (archivo == null) {
			throw new WrongValueException(txtArchivo,
					Labels.getLabel("error.1001"));
		}

		Treeitem ci = trSerie.getSelectedItem();
		if (ci == null) {
			throw new WrongValueException(trSerie,Labels.getLabel("error.0005"));
		}

		DocSerieDoc tipo = ci.getValue();

		try {

			String nombreArchivo = archivo.getName();

			DocArchivo docArchivo = sistemaArchivoNgc.getArchivo(directorio,
					nombreArchivo);

			if (docArchivo == null) {

				docArchivo = new DocArchivo();
				docArchivo.setArchNombre(nombreArchivo);
				docArchivo.setArchDescripcion(txtDescripcion.getValue());
				docArchivo.setAudiChecksum(null);
				docArchivo.setAudiFechModi(new Date());
				docArchivo.setAudiMotiAnul(null);
				docArchivo.setAudiSiAnul(false);
				docArchivo.setAudiUsuario(usuario.getLogin());
				docArchivo.setDocSistArch(directorio);
				docArchivo.setDocSerieDoc(tipo);
				docArchivo.setPltUsuario((PltUsuario)usuario);
			}

			// creo la version
			DocArchivoVersion version = new DocArchivoVersion();
			version.setArchVersChecksum("XXX");
			version.setArchVersDescripcion(txtDescripcion.getValue());
			version.setArchVersRuta("XXX");
			version.setArchVersTag(txtTagVersion.getValue());
			version.setAudiChecksum(null);
			version.setAudiFechModi(new Date());
			version.setAudiSiAnul(false);
			version.setAudiUsuario(usuario.getLogin());
			version.setDocArchivo(docArchivo);
			version.setAudiMotiAnul(null);

			// cargar los metadatos

			List<DocCampArch> listaMeta = new ArrayList<DocCampArch>();

			for (Listitem li : lstMeta.getItems()) {
				DocCampTipo campoTipo = li.getValue();
				DocCampo campo = campoTipo.getDocCampo();
				Component cmp = (Component) li.getAttribute("campo");
				TipoCampo tipoCampo = TipoCampo.getTipoPorID(campo
						.getCampoTipo());

				String valor = tipoCampo.getValor(cmp);

				DocCampArch metaDato = new DocCampArch();
				metaDato.setAudiChecksum(null);
				metaDato.setAudiFechModi(new Date());
				metaDato.setAudiMotiAnul(null);
				metaDato.setAudiSiAnul(false);
				metaDato.setAudiUsuario(usuario.getLogin());
				metaDato.setCampArchValor(valor);
				metaDato.setDocCampo(campo);
				metaDato.setDocArchivoVersion(version);
				listaMeta.add(metaDato);

			}

			
			
			
			List<DocAcl> permisos = new ArrayList<DocAcl>();
			
			Treechildren hijosRaiz = trPermisos.getTreechildren();
			tomarPermisos(docArchivo, permisos, hijosRaiz);
			
			List<Anexo> anexos = new ArrayList<>();
			
			for(Listitem li : lstAnexo.getItems()){
				AnexoTmp anexoTmp = li.getValue();
				if(anexoTmp != null){
					Anexo anexo = new Anexo();
					
					anexo.setArchVersChecksum("XXX");
					anexo.setArchVersDescripcion(null);
					anexo.setArchVersRuta("XXX");
					anexo.setArchVersTag(null);
					anexo.setAudiChecksum(null);
					anexo.setAudiFechModi(new Date());
					anexo.setAudiSiAnul(false);
					anexo.setAudiUsuario(usuario.getLogin());
					anexo.setDocArchivo(docArchivo);
					anexo.setAudiMotiAnul(null);
					anexo.setDocArchivoVersion(version);
					anexo.setArchivoTmp(anexoTmp.archivoTmp);
					anexo.setNombreArchivo(anexoTmp.nombreArchivo);
					
					anexos.add(anexo);
				}
			}
			
			
			sistemaArchivoNgc.cargarArchivo(docArchivo, version, archivo.getByteData(), listaMeta, permisos, anexos);

			Events.sendEvent(new Event(Events.ON_CLOSE, this.self, ConstantesAdmin.EXITO));
		} catch (Exception ex) {
			String mensaje = new StringBuilder("Error al registrar archivo: ")
					.append(ex.getClass().getName()).append(": ")
					.append(ex.getMessage()).toString();
			logger.error(mensaje, ex);
			Messagebox.show(mensaje, "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	
	public void tomarPermisos(DocArchivo docArchivo, List<DocAcl> permisos, Treechildren arbol){
		
		Collection<Treeitem> itemsHijo = arbol.getItems();
		
		
		
		if(itemsHijo == null){
			return;
		} else if(! itemsHijo.isEmpty()){
			for(Treeitem item : itemsHijo){				
				Object valor = item.getValue();
				if(valor != null && (valor instanceof PltUsuario || valor instanceof PltRol)){
					Checkbox chkSiLectura = (Checkbox) item.getAttribute("chkSiLectura");
					Checkbox chkSiEscritura = (Checkbox) item.getAttribute("chkSiEscritura");
					
					boolean siLeer = chkSiLectura.isChecked();
					boolean siEscribir = chkSiEscritura.isChecked();
					
					if(siLeer || siEscribir){
						DocAcl permiso = new DocAcl();
						permiso.setAclSiEscritura(siEscribir);
						permiso.setAclSiLectura(siLeer);
						permiso.setAudiChecksum(null);
						permiso.setAudiFechModi(new Date());
						permiso.setAudiMotiAnul(null);
						permiso.setAudiSiAnul(false);
						permiso.setAudiUsuario(usuario.getLogin());
						permiso.setDocArchivo(docArchivo);
						permiso.setDocSistArch(null);
						if(valor instanceof PltRol){
							permiso.setPltRol((PltRol) valor);
							permiso.setPltUsuario(null);
						} else {
							permiso.setPltRol(null);
							permiso.setPltUsuario((PltUsuario)valor);
						}
						
						permisos.add(permiso);
						
					} else {
						//eliminar permiso si es necesario
					}
					
					
				}
				
			}
		}
		
		
		
		
		
	}
	

	public void onClick$btnCancelar(Event evt) throws Exception {
		Events.sendEvent(new Event(Events.ON_CLOSE, this.self, null));
	}

	

	public UsuarioNgc getUsuarioNgc() {
		return usuarioNgc;
	}

	public void setUsuarioNgc(UsuarioNgc usuarioNgc) {
		this.usuarioNgc = usuarioNgc;
	}
	
	

}
