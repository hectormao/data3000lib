package com.data3000.data3000lib.cnt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoArchivo;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;

public class CargarArchivoCnt extends WindowComposer implements
		ComboitemRenderer<DocTipoArchivo>, ListitemRenderer<DocCampTipo> {

	/**
	 * BEANS
	 */
	private PlataformaNgc plataformaNgc;
	private UsuarioNgc usuarioNgc;
	private SistemaArchivoNgc sistemaArchivoNgc;

	private Window winCargarArchivo;
	private Combobox cmbTipoArchivo;
	private Listbox lstMeta;
	private Textbox txtArchivo;
	private Textbox txtDescripcion;
	private Textbox txtTagVersion;
	private Tree trPermisos;

	private Media archivo;

	private DocSistArch directorio;

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void doAfterCompose(Window winTipoDocumento) throws Exception {
		super.doAfterCompose(winTipoDocumento);

		List<DocTipoArchivo> listaTipos = sistemaArchivoNgc.getTipos();

		ListModelList<DocTipoArchivo> modelo = new ListModelList<DocTipoArchivo>();
		modelo.addAll(listaTipos);

		cmbTipoArchivo.setModel(modelo);
		cmbTipoArchivo.setItemRenderer(this);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		txtTagVersion.setValue(sdf.format(new Date()));

		directorio = (DocSistArch) argumentos.get(ConstantesAdmin.OBJETO_PADRE);

		cargarArbolPermisos();

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

	public void onSelect$cmbTipoArchivo(Event evt) throws Exception {
		Comboitem ci = cmbTipoArchivo.getSelectedItem();
		if (ci != null) {
			DocTipoArchivo tipo = ci.getValue();
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
	public void render(Comboitem ci, DocTipoArchivo tipo, int idx)
			throws Exception {
		ci.setLabel(tipo.getTipoArchNombre());
		ci.setDescription(tipo.getTipoArchDescripcion());
		ci.setValue(tipo);
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

		Comboitem ci = cmbTipoArchivo.getSelectedItem();
		if (ci == null) {
			throw new WrongValueException(cmbTipoArchivo,Labels.getLabel("error.0005"));
		}

		DocTipoArchivo tipo = ci.getValue();

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
				docArchivo.setDocTipoArchivo(tipo);
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
			
			
			sistemaArchivoNgc.cargarArchivo(docArchivo, version, archivo.getByteData(), listaMeta, permisos);

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

	public PlataformaNgc getPlataformaNgc() {
		return plataformaNgc;
	}

	public void setPlataformaNgc(PlataformaNgc plataformaNgc) {
		this.plataformaNgc = plataformaNgc;
	}

	public UsuarioNgc getUsuarioNgc() {
		return usuarioNgc;
	}

	public void setUsuarioNgc(UsuarioNgc usuarioNgc) {
		this.usuarioNgc = usuarioNgc;
	}
	
	

}
