package com.data3000.data3000lib.cnt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.print.Doc;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.TipoCampo;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.admin.vo.Dominio;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocTipoAlma;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class TipoDocumentoCnt extends WindowComposer {
	
	
	private Window winTipoDocumento;
	private Textbox txtCodigo;
	private Textbox txtNombre;
	private Textbox txtDescripcion;
	private Combobox cmbTipo;
	private Combobox cmbSoporte;
	private Listbox lstMetadato;
	private Listbox lstAlma;
	
	
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private final String TIPO_NOMBRE = "tipoNombre";
	private final String TIPO_TIPO = "tipoTipo";
	private final String TIPO_DESCRIPCION = "tipoDescripcion";
	private final String TIPO_REQUERIDO = "tiporequerido";
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private List<Object> listaEliminar = new ArrayList<>();
	private List<Object> listaActualizar = new ArrayList<>();
	private List<Object> listaCrear = new ArrayList<>();
	
	private List<DocCampo> listaCrearCampo = new ArrayList<DocCampo>();
	
	private DocSerieDoc docSerieDoc;
	private List<DocCampTipo> listaCampos;
	
	private DocSerieDoc padre;
	
	
	@Override
	public void doAfterCompose(Window winTipoDocumento) throws Exception{		
		super.doAfterCompose(winTipoDocumento);
		
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			padre = (DocSerieDoc) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			docSerieDoc = new DocSerieDoc();
			
		} else if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR) || formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			docSerieDoc = (DocSerieDoc) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			padre = docSerieDoc.getSerieDocPadre();
		}
		
		
		if(docSerieDoc.getSerieDocIdn() == 0L){			
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
			txtCodigo.setValue(docSerieDoc.getSerieDocCodigo());
			txtNombre.setValue(docSerieDoc.getSerieDocNombre());
			txtDescripcion.setValue(docSerieDoc.getSerieDocDescripcion());
			seleccionarComboDominio(cmbTipo, docSerieDoc.getSerieDocTipo());
			seleccionarComboDominio(cmbSoporte, docSerieDoc.getSerieDocSoporte());
		} 
		
		if(listaCampos != null && ! listaCampos.isEmpty()){
			for(DocCampTipo campoTipo : listaCampos){
				agregarCampo(campoTipo);
			}
			
		}
		
		List<DocTipoAlma> almas = sistemaArchivoNgc.getTiposAlmacenamientoSerie(docSerieDoc);
		if(almas != null && ! almas.isEmpty()){
			for(DocTipoAlma alma : almas){
				agregarAlma(alma);
			}
		}
		
		
		
	}

	
	/**
	 * metodo para agregar un nuevo campo al tipo de documento
	 * @param evt
	 */
	public void onClick$btnNuevoAlma(Event evt){
		try{
			DocTipoAlma alma = new DocTipoAlma();
			alma.setDocSerieDoc(docSerieDoc);
			agregarAlma(alma);
			listaCrear.add(alma);
		} catch(Exception ex){
			logger.error(new StringBuilder("Error al crear fila tipo campo: ").append(ex.getClass().getName()).append(": ").append(ex.getMessage()).toString(),ex);
			Messagebox.show(ex.getMessage(), Labels.getLabel("data3000.error"), Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	public void onClick$btnEliminarAlma(Event evt){
		
		try{
			while(true){
				Listitem li = lstAlma.getSelectedItem();
				if(li == null){
					break;
				}
				if(li != null){				
					DocTipoAlma campoAlma = li.getValue();				
					if(campoAlma != null && campoAlma.getTipoAlmaIdn() > 0L){
						listaEliminar.add(campoAlma);					
					} else {
						listaCrear.remove(campoAlma);
					}
					
					lstAlma.removeChild(li);				
				}
			}
			
		} catch(Exception ex){
			logger.error(new StringBuilder("Error al eliminar fila alma: ").append(ex.getClass().getName()).append(": ").append(ex.getMessage()).toString(),ex);
			Messagebox.show(ex.getMessage(), Labels.getLabel("data3000.error"), Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	
	
	private void agregarAlma(final DocTipoAlma alma) throws Exception{
		Listitem li = new Listitem();
		
		
		
		
		final Textbox txtNombre = new Textbox();
		txtNombre.setValue(alma.getTipoAlmaNombre());
		txtNombre.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				
				
				
				alma.setTipoAlmaNombre(txtNombre.getText());
				if(alma.getTipoAlmaIdn() > 0L && ! listaActualizar.contains(alma)){
					listaActualizar.add(alma);
				}
			}
		});
		
		
		Listcell celdaNombre = new Listcell();
		celdaNombre.appendChild(txtNombre);		
		li.appendChild(celdaNombre);
		
		
		final Textbox txtDescripcion = new Textbox();
		txtDescripcion.setValue(alma.getTipoAlmaDescripcion());
		txtDescripcion.setWidth("100%");
		txtDescripcion.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				alma.setTipoAlmaDescripcion(txtDescripcion.getText());
				if(alma.getTipoAlmaIdn() > 0L && ! listaActualizar.contains(alma)){
					listaActualizar.add(alma);
				}
			}
		});
		
		
		Listcell celdaDescripcion = new Listcell();
		celdaDescripcion.appendChild(txtDescripcion);		
		li.appendChild(celdaDescripcion);
		
		
		final Intbox intEdad = new Intbox();
		int valorInt = 0;
		
		Short valorEdad = alma.getTipoAlmaEdad();
		if(valorEdad != null){
			valorInt = valorEdad.intValue();
		}
		
		
		intEdad.setValue(valorInt);
		intEdad.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				short valor = 0;
				Integer valorInt = intEdad.getValue();
				if(valorInt != null){
					valor = valorInt.shortValue();
				}
				
				alma.setTipoAlmaEdad(valor);
				if(alma.getTipoAlmaIdn() > 0L && ! listaActualizar.contains(alma)){
					listaActualizar.add(alma);
				}
			}
		});
		
		
		Listcell celdaEdad = new Listcell();
		celdaEdad.appendChild(intEdad);		
		li.appendChild(celdaEdad);
		
		final Combobox cmbDisposicion = new Combobox();
		cmbDisposicion.setAttribute(ConstantesAdmin.DOMINIO, "DISPOSICION_FINAL");
		llenarComboDominio(cmbDisposicion, "DISPOSICION_FINAL");
		seleccionarComboDominio(cmbDisposicion, alma.getTipoAlmaDispfinal());
		
		
		cmbDisposicion.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				
				Comboitem ci = cmbDisposicion.getSelectedItem();
				Dominio dominio = (Dominio) ci.getValue();
				if(dominio == null){
					throw new WrongValueException(cmbDisposicion, Labels.getLabel("error.1007"));
				}
				alma.setTipoAlmaDispfinal(dominio.getValor());
				if(alma.getTipoAlmaIdn() > 0L && ! listaActualizar.contains(alma)){
					listaActualizar.add(alma);
				}
			}
		});
		
		Listcell celdaDisposicion = new Listcell();
		celdaDisposicion.appendChild(cmbDisposicion);		
		li.appendChild(celdaDisposicion);
		
		li.setValue(alma);
		lstAlma.appendChild(li);
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
		final Combobox cmbTipoCampo = new Combobox();
		cmbTipoCampo.setConstraint("no empty");
		cmbTipoCampo.setReadonly(true);
		for(TipoCampo tipo : tipos){
			
			Comboitem ci = new Comboitem();
			
			String nombre = Labels.getLabel(tipo.getNombre());
			if(nombre == null){
				nombre = tipo.getNombre();
			}
			ci.setLabel(nombre);
			ci.setValue(tipo);
			cmbTipoCampo.appendChild(ci);
			
			if(tipoCampo != null && ((int) tipoCampo.getCampoTipo()) == tipo.getId()){
				cmbTipoCampo.setSelectedItem(ci);
			}
		}
		
		
		
		Listcell celdaTipo = new Listcell();
		celdaTipo.appendChild(cmbTipoCampo);		
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
		li.setAttribute(TIPO_TIPO, cmbTipoCampo);
		li.setAttribute(TIPO_DESCRIPCION, txtDescripcion);
		li.setAttribute(TIPO_REQUERIDO, chkRequerido);
		
		
		cmbCampo.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				Comboitem ci = cmbCampo.getSelectedItem();
				if(ci != null){
					
					DocCampo campo = ci.getValue();
					
					cmbTipoCampo.setDisabled(true);
					txtDescripcion.setDisabled(true);
					for(Comboitem ciTipo : cmbTipoCampo.getItems()){
						TipoCampo tipo = ciTipo.getValue();
						if(tipo.getId() == (int)campo.getCampoTipo()){
							cmbTipoCampo.setSelectedItem(ciTipo);
							break;
						}
					}
					txtDescripcion.setValue(campo.getCampoDescripcion());
				} else {
					cmbTipoCampo.setDisabled(false);
					cmbTipoCampo.setSelectedIndex(-1);
					
					txtDescripcion.setValue(null);
					txtDescripcion.setDisabled(false);
				}
				
			}
		});
		
		
		if(camposeleccionado != null){
			cmbTipoCampo.setDisabled(true);
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
		
		
		Events.sendEvent(new Event(Events.ON_CLOSE,this.self,docSerieDoc));
	}
	
	
	
	
	private void establecerDatos() throws Exception {
		
		docSerieDoc.setSerieDocCodigo(txtCodigo.getValue());
		docSerieDoc.setSerieDocNombre(txtNombre.getValue());
		docSerieDoc.setSerieDocDescripcion(txtDescripcion.getValue());
		String tipoSerie = getSeleccionComboDominio(cmbTipo, true);
		docSerieDoc.setSerieDocTipo(tipoSerie);
		docSerieDoc.setSerieDocSoporte(getSeleccionComboDominio(cmbSoporte, true));
		
		if(tipoSerie.equals(ConstantesData3000.SERIE)){
			
		} else {
			
			switch (tipoSerie) {
				case ConstantesData3000.SERIE:{
					docSerieDoc.setSerieDocPadre(null);
				}break;
				case ConstantesData3000.SUBSERIE:{
					if(padre == null || (padre != null && ! padre.getSerieDocTipo().equals(ConstantesData3000.SERIE))){
						throw new WrongValueException(cmbTipo, Labels.getLabel("error.1008"));
					}
					docSerieDoc.setSerieDocPadre(padre);
				}break;
				case ConstantesData3000.TIPO_DOCUMENTO:{
					if(padre == null || (padre != null && ! padre.getSerieDocTipo().equals(ConstantesData3000.SUBSERIE))){
						throw new WrongValueException(cmbTipo, Labels.getLabel("error.1008"));
					}
					docSerieDoc.setSerieDocPadre(padre);
				}break;
	
				default:
					break;
			}
			
		}
		
		docSerieDoc.setAudiFechModi(new Date());
		docSerieDoc.setAudiChecksum(null);
		docSerieDoc.setAudiMotiAnul(null);
		docSerieDoc.setAudiSiAnul(false);
		docSerieDoc.setAudiUsuario(usuario.getLogin());
		
		
		//recorrer la lista de crear y actualizar para establecer los datos de auditoria
		for(Object obj : listaCrear){
			if(obj instanceof DocTipoAlma){
				DocTipoAlma alma = (DocTipoAlma)obj;
				
				alma.setAudiFechModi(new Date());
				alma.setAudiChecksum(null);
				alma.setAudiMotiAnul(null);
				alma.setAudiSiAnul(false);
				alma.setAudiUsuario(usuario.getLogin());
			} else if(obj instanceof DocCampTipo) {
				
				DocCampTipo campoTipo = (DocCampTipo) obj;
				
				
				campoTipo.setAudiFechModi(new Date());
				campoTipo.setAudiChecksum(null);
				campoTipo.setAudiMotiAnul(null);
				campoTipo.setAudiSiAnul(false);
				campoTipo.setAudiUsuario(usuario.getLogin());
				
			}
		}
		
		for(Object obj : listaActualizar){
			if(obj instanceof DocTipoAlma){
				DocTipoAlma alma = (DocTipoAlma)obj;
				
				alma.setAudiFechModi(new Date());
				alma.setAudiChecksum(null);
				alma.setAudiMotiAnul(null);
				alma.setAudiSiAnul(false);
				alma.setAudiUsuario(usuario.getLogin());
			} else if(obj instanceof DocCampTipo) {
				
				DocCampTipo campoTipo = (DocCampTipo) obj;
				
				
				campoTipo.setAudiFechModi(new Date());
				campoTipo.setAudiChecksum(null);
				campoTipo.setAudiMotiAnul(null);
				campoTipo.setAudiSiAnul(false);
				campoTipo.setAudiUsuario(usuario.getLogin());
				
			}
		}
		
		for(Object obj : listaEliminar){
			if(obj instanceof DocTipoAlma){
				DocTipoAlma alma = (DocTipoAlma)obj;
				
				alma.setAudiFechModi(new Date());
				alma.setAudiChecksum(null);
				alma.setAudiMotiAnul(null);
				alma.setAudiSiAnul(true);
				alma.setAudiUsuario(usuario.getLogin());
			} else if(obj instanceof DocCampTipo) {
				
				DocCampTipo campoTipo = (DocCampTipo) obj;
				
				
				campoTipo.setAudiFechModi(new Date());
				campoTipo.setAudiChecksum(null);
				campoTipo.setAudiMotiAnul(null);
				campoTipo.setAudiSiAnul(true);
				campoTipo.setAudiUsuario(usuario.getLogin());
				
			}
		}
		
		
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
		Combobox cmbTipoCampo = (Combobox) li.getAttribute(TIPO_TIPO);
		Textbox txtDescripcion = (Textbox) li.getAttribute(TIPO_DESCRIPCION);
		
		
		Comboitem ci = cmbCampo.getSelectedItem();
			
		DocCampo campo = null;		
		TipoCampo tipo = cmbTipoCampo.getSelectedItem().getValue();
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
