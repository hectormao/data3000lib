package com.data3000.data3000lib.cnt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.TipoCampo;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;

public class TipoDocumentoCnt extends WindowComposer {
	
	
	private Window winTipoDocumento;
	private Textbox txtNombre;
	private Textbox txtDescripcion;
	private Listbox lstMetadato;
	private Toolbarbutton btnNuevoCampo;
	private Toolbarbutton btnEliminarCampo;
	
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private final String TIPO_NOMBRE = "tipoNombre";
	private final String TIPO_TIPO = "tipoTipo";
	private final String TIPO_DESCRIPCION = "tipoDescripcion";
	private final String TIPO_REQUERIDO = "tiporequerido";
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private List<DocCampTipo> listaEliminar = new ArrayList<DocCampTipo>();
	private List<DocCampTipo> listaActualizar = new ArrayList<DocCampTipo>();
	private List<DocCampTipo> listaCrear = new ArrayList<DocCampTipo>();
	private List<DocCampo> listaCrearCampo = new ArrayList<DocCampo>();
	
	private DocSerieDoc docSerieDoc;
	private List<DocCampTipo> listaCampos;
	
	
	@Override
	public void doAfterCompose(Window winTipoDocumento) throws Exception{		
		super.doAfterCompose(winTipoDocumento);
		docSerieDoc = (DocSerieDoc) argumentos.get(ConstantesAdmin.ARG_SELECCION);
		if(docSerieDoc == null){
			docSerieDoc = new DocSerieDoc();
			listaCampos = new ArrayList<DocCampTipo>();			
		} else {
			listaCampos = sistemaArchivoNgc.getCamposTipo(docSerieDoc);
			cargarDatos();
		}
		
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			soloConsulta();
		}
		
		
	}
	
	
	
	
	
	
	private void cargarDatos() throws Exception{
		
		if(docSerieDoc !=  null){
			txtNombre.setValue(docSerieDoc.getSerieDocNombre());
			txtDescripcion.setValue(docSerieDoc.getSerieDocDescripcion());
		}
		
		if(listaCampos != null && ! listaCampos.isEmpty()){
			for(DocCampTipo campoTipo : listaCampos){
				agregarCampo(campoTipo);
			}
			
		}
		
	}




	/**
	 * metodo para agregar un nuevo campo al tipo de documento
	 * @param evt
	 */
	public void onClick$btnNuevoCampo(Event evt){
		try{
		agregarCampo(null);
		} catch(Exception ex){
			logger.error(new StringBuilder("Error al crear fila tipo campo: ").append(ex.getClass().getName()).append(": ").append(ex.getMessage()).toString(),ex);
			Messagebox.show(ex.getMessage(), Labels.getLabel("data3000.error"), Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	
	public void onClick$btnEliminarCampo(Event evt){
		try{
			while(true){
				Listitem li = lstMetadato.getSelectedItem();
				if(li == null){
					break;
				}
				if(li != null){				
					DocCampTipo campoTipo = li.getValue();				
					if(campoTipo != null){
						listaEliminar.add(campoTipo);					
					}
					
					lstMetadato.removeChild(li);				
				}
			}
			
		} catch(Exception ex){
			logger.error(new StringBuilder("Error al crear fila tipo campo: ").append(ex.getClass().getName()).append(": ").append(ex.getMessage()).toString(),ex);
			Messagebox.show(ex.getMessage(), Labels.getLabel("data3000.error"), Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	private void agregarCampo(DocCampTipo campoTipo) throws Exception{
		Listitem li = new Listitem();
		
		DocCampo tipoCampo = null;
		
		if(campoTipo != null){
			tipoCampo = campoTipo.getDocCampo();
		}
		
		
		List<DocCampo> listaCampos = sistemaArchivoNgc.getCampos();
		
		final Combobox cmbCampo = new Combobox();
		cmbCampo.setConstraint("no empty");
		cmbCampo.setWidth("80%");
		DocCampo camposeleccionado = null;
		if(listaCampos != null){
			for(DocCampo campo : listaCampos){
				Comboitem ci = new Comboitem();
				ci.setLabel(campo.getCampoNombre());
				ci.setDescription(campo.getCampoDescripcion());
				ci.setValue(campo);
				cmbCampo.appendChild(ci);
				if(tipoCampo != null && tipoCampo.getCampoIdn() == campo.getCampoIdn()){
					cmbCampo.setSelectedItem(ci);
					camposeleccionado = campo;
				}
			}
		}
		
		
		Listcell celdaNombre = new Listcell();
		celdaNombre.appendChild(cmbCampo);		
		li.appendChild(celdaNombre);
		
		
		TipoCampo[] tipos = TipoCampo.values();
		final Combobox cmbTipo = new Combobox();
		cmbTipo.setConstraint("no empty");
		cmbTipo.setReadonly(true);
		for(TipoCampo tipo : tipos){
			
			Comboitem ci = new Comboitem();
			
			String nombre = Labels.getLabel(tipo.getNombre());
			if(nombre == null){
				nombre = tipo.getNombre();
			}
			ci.setLabel(nombre);
			ci.setValue(tipo);
			cmbTipo.appendChild(ci);
			
			if(tipoCampo != null && ((int) tipoCampo.getCampoTipo()) == tipo.getId()){
				cmbTipo.setSelectedItem(ci);
			}
		}
		
		
		
		Listcell celdaTipo = new Listcell();
		celdaTipo.appendChild(cmbTipo);		
		li.appendChild(celdaTipo);
		
		final Textbox txtDescripcion = new Textbox();
		txtDescripcion.setWidth("100%");
		if(tipoCampo != null){
			txtDescripcion.setValue(tipoCampo.getCampoDescripcion());
		}		
		Listcell celdaDescripcion = new Listcell();
		celdaDescripcion.appendChild(txtDescripcion);		
		li.appendChild(celdaDescripcion);
		
		Checkbox chkRequerido = new Checkbox();
		if(campoTipo != null){
			chkRequerido.setChecked(campoTipo.isCampTipoRequerido());
		}
		Listcell celdaRequerido = new Listcell();
		celdaRequerido.appendChild(chkRequerido);		
		li.appendChild(celdaRequerido);
		
		li.setAttribute(TIPO_NOMBRE, cmbCampo);
		li.setAttribute(TIPO_TIPO, cmbTipo);
		li.setAttribute(TIPO_DESCRIPCION, txtDescripcion);
		li.setAttribute(TIPO_REQUERIDO, chkRequerido);
		
		
		cmbCampo.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				Comboitem ci = cmbCampo.getSelectedItem();
				if(ci != null){
					
					DocCampo campo = ci.getValue();
					
					cmbTipo.setDisabled(true);
					txtDescripcion.setDisabled(true);
					for(Comboitem ciTipo : cmbTipo.getItems()){
						TipoCampo tipo = ciTipo.getValue();
						if(tipo.getId() == (int)campo.getCampoTipo()){
							cmbTipo.setSelectedItem(ciTipo);
							break;
						}
					}
					txtDescripcion.setValue(campo.getCampoDescripcion());
				} else {
					cmbTipo.setDisabled(false);
					cmbTipo.setSelectedIndex(-1);
					
					txtDescripcion.setValue(null);
					txtDescripcion.setDisabled(false);
				}
				
			}
		});
		
		
		if(camposeleccionado != null){
			cmbTipo.setDisabled(true);
			txtDescripcion.setDisabled(true);
		}
		
		li.setValue(campoTipo);
		lstMetadato.appendChild(li);
	}
	
	public void onClick$btnAceptar(Event evt) throws Exception{
		
		
		
		if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			establecerDatos();
			sistemaArchivoNgc.registrarTipoDocumentos(docSerieDoc,listaCrear,listaActualizar,listaEliminar,listaCrearCampo);
		} else if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_EDITAR)){
			establecerDatos();
			sistemaArchivoNgc.actualizarTipoDocumentos(docSerieDoc,listaCrear,listaActualizar,listaEliminar,listaCrearCampo);
		} else if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			String nota = solicitarNota();
			
			docSerieDoc.setAudiFechModi(new Date());
			docSerieDoc.setAudiMotiAnul(nota);
			docSerieDoc.setAudiSiAnul(true);
			docSerieDoc.setAudiUsuario(usuario.getLogin());
			sistemaArchivoNgc.anularTipoDocumentos(docSerieDoc);
			
		}
		
		
		Events.sendEvent(new Event(Events.ON_CLOSE,this.self,ConstantesAdmin.EXITO));
	}
	
	
	
	
	private void establecerDatos() throws Exception {
		
		docSerieDoc.setSerieDocDescripcion(txtNombre.getValue());
		docSerieDoc.setSerieDocDescripcion(txtDescripcion.getValue());
		
		docSerieDoc.setAudiFechModi(new Date());
		docSerieDoc.setAudiChecksum(null);
		docSerieDoc.setAudiMotiAnul(null);
		docSerieDoc.setAudiSiAnul(false);
		docSerieDoc.setAudiUsuario(usuario.getLogin());
		
		int orden = 0;
		for(Listitem li : lstMetadato.getItems()){
			DocCampTipo campoTipo = li.getValue();
			Checkbox chkRequerido = (Checkbox) li.getAttribute(TIPO_REQUERIDO);
			if(campoTipo == null){
				//crear objeto
				campoTipo = new DocCampTipo();				
				establecerCampo(campoTipo,li);
				campoTipo.setDocSerieDoc(docSerieDoc);
				campoTipo.setCampTipoOrden((short)orden);				
				
				campoTipo.setCampTipoRequerido(chkRequerido.isChecked());
				campoTipo.setAudiFechModi(new Date());
				campoTipo.setAudiChecksum(null);
				campoTipo.setAudiMotiAnul(null);
				campoTipo.setAudiSiAnul(false);
				campoTipo.setAudiUsuario(usuario.getLogin());
				listaCrear.add(campoTipo);
			} else {
				//actualizar
				
				boolean actualizar = false;
				
				DocCampo campoAntes = campoTipo.getDocCampo();
				establecerCampo(campoTipo, li);
				DocCampo campoDespues = campoTipo.getDocCampo();
				
				
				
				if(campoAntes.getCampoIdn() != campoDespues.getCampoIdn()){
					actualizar = true;
				}
				
				if(orden != (int) campoTipo.getCampTipoOrden()){
					campoTipo.setCampTipoOrden((short)orden);
					actualizar = true;
				}
				
				if(campoTipo.isCampTipoRequerido() != chkRequerido.isChecked()){
					campoTipo.setCampTipoRequerido(chkRequerido.isChecked());
					actualizar = true;
				}
				
				if(actualizar){
					campoTipo.setAudiFechModi(new Date());
					campoTipo.setAudiChecksum(null);
					campoTipo.setAudiMotiAnul(null);
					campoTipo.setAudiSiAnul(false);
					campoTipo.setAudiUsuario(usuario.getLogin());
					listaActualizar.add(campoTipo);
				}
			}
			
			orden ++;
		}
		
	}




	private void establecerCampo(DocCampTipo campoTipo, Listitem li) {
		
		 
		
		Combobox cmbCampo = (Combobox) li.getAttribute(TIPO_NOMBRE);
		Combobox cmbTipo = (Combobox) li.getAttribute(TIPO_TIPO);
		Textbox txtDescripcion = (Textbox) li.getAttribute(TIPO_DESCRIPCION);
		
		
		Comboitem ci = cmbCampo.getSelectedItem();
			
		DocCampo campo = null;		
		TipoCampo tipo = cmbTipo.getSelectedItem().getValue();
		String descripcion = txtDescripcion.getValue();
		
		if(ci == null){
			//nuevo campo
			campo = new DocCampo();
			campo.setCampoNombre(cmbCampo.getValue());
			campo.setCampoTipo((byte) tipo.getId());
			campo.setCampoDescripcion(descripcion);
			
			campo.setAudiFechModi(new Date());
			campo.setAudiChecksum(null);
			campo.setAudiMotiAnul(null);
			campo.setAudiSiAnul(false);
			campo.setAudiUsuario(usuario.getLogin());
			
			listaCrearCampo.add(campo);
		} else {
			campo = ci.getValue();
		}
		
		campoTipo.setDocCampo(campo);
		
	}
	



	public void onClick$btnCancelar(Event evt) throws Exception{
		Events.sendEvent(new Event(Events.ON_CLOSE,this.self,null));
	}


	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}


	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}
	
	

}
