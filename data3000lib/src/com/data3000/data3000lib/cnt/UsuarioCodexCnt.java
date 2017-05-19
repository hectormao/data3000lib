package com.data3000.data3000lib.cnt;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.data3000.admin.cnt.UsuarioCnt;
import com.data3000.admin.exc.PltException;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;

public class UsuarioCodexCnt extends UsuarioCnt  {
	
	private Listbox lstEmpresasDisponibles;
	private Listbox lstEmpresasSeleccionadas;
	
	private Button btnSeleccionar;
	private Button btnSeleccionarTodos;
	private Button btnQuitar;
	private Button btnQuitarTodos;
	
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private List<DocAcl> listaEliminar;
	
	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}



	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}



	@Override
	public void onCreate$winUsuario(Event event) throws Exception{
		super.onCreate$winUsuario(event);
		
		cargarEmpresas();
		listaEliminar = new ArrayList<DocAcl>();
		
		if (formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR)){
			
			cargarAclUsuario();
			
		}
	}
	
	private void cargarAclUsuario(){
		List<DocAcl> accesos = sistemaArchivoNgc.getAccesoEntidadesUsuario(usu);
		for(DocAcl acl : accesos){
			
			int idx = 0;
			while(idx < lstEmpresasDisponibles.getItemCount()){
				Listitem liComboDisponible = lstEmpresasDisponibles.getItemAtIndex(idx);
				DocSistArch entidadCombo = liComboDisponible.getValue();
				if(entidadCombo.getSistArchIdn() == acl.getDocSistArch().getSistArchIdn()){					
					lstEmpresasDisponibles.removeChild(liComboDisponible);
					agregarAcl(acl);					
					break;
				}
				
				idx++;
			}
			
		}
	}

	private void cargarEmpresas(){
		
		
		List<DocSistArch> entidades = sistemaArchivoNgc.getEntidades();
		
		for(DocSistArch entidad : entidades){
			
			agregarEntidad(entidad);
		}
		
	}

	private void agregarEntidad(DocSistArch entidad) {
		Listitem arg0 = new Listitem();
		
		Listcell celdaCheck = new Listcell();
		Listcell celdaNombre = new Listcell(entidad.getSistArchNombre());
		arg0.appendChild(celdaCheck);
		arg0.appendChild(celdaNombre);
		arg0.setValue(entidad);
		arg0.setTooltiptext(entidad.getSistArchDescripcion());
		
		
		
		int idx = 0;
		Listitem liAntes = null;
		while(idx < lstEmpresasDisponibles.getItemCount()){
			Listitem liCombo = lstEmpresasDisponibles.getItemAtIndex(idx);
			DocSistArch entidadCombo = liCombo.getValue();
			if(entidadCombo.getSistArchNombre().compareToIgnoreCase(entidad.getSistArchNombre()) > 0){
				liAntes = liCombo;
				break;
			}
			
			idx++;
		}
		
		if(liAntes != null){
			lstEmpresasDisponibles.insertBefore(arg0, liAntes);
		} else {
			lstEmpresasDisponibles.appendChild(arg0);
		}
		
		
		/*Collections.sort(lstEmpresasDisponibles.getItems(), new Comparator<Listitem>() {

			@Override
			public int compare(Listitem o1, Listitem o2) {
				
				DocSistArch ent1 = o1.getValue();
				DocSistArch ent2 = o1.getValue();
				
				return ent1.getSistArchNombre().compareToIgnoreCase(ent2.getSistArchNombre());
			}
		});*/
		
	}



	public void onClick$btnSeleccionar(Event event){
		while(true){
			Listitem seleccion = lstEmpresasDisponibles.getSelectedItem();
			if(seleccion == null){
				break;
			}
			agregarItem(seleccion);
		}
		
	}
	
	public void onClick$btnSeleccionarTodos(Event event){
		while(lstEmpresasDisponibles.getItemCount() > 0){
			Listitem seleccion = lstEmpresasDisponibles.getItemAtIndex(0);
			agregarItem(seleccion);
		}
	}
	
	private void agregarAcl(DocAcl acl){
		
		Listitem liAcl = new Listitem();
		Listcell celdaCheck = new Listcell();
		liAcl.appendChild(celdaCheck);
		Listcell celdaEntidad = new Listcell(acl.getDocSistArch().getSistArchNombre());
		liAcl.appendChild(celdaEntidad);
		liAcl.setTooltiptext(acl.getDocSistArch().getSistArchDescripcion());
		liAcl.setValue(acl);
		lstEmpresasSeleccionadas.appendChild(liAcl);
		
		
		int idx = 0;
		Listitem liAntes = null;
		while(idx < lstEmpresasSeleccionadas.getItemCount()){
			Listitem liCombo = lstEmpresasSeleccionadas.getItemAtIndex(idx);
			DocAcl aclCombo = liCombo.getValue();
			if(aclCombo.getDocSistArch().getSistArchNombre().compareToIgnoreCase(acl.getDocSistArch().getSistArchNombre()) > 0){
				liAntes = liCombo;
				break;
			}
			idx ++;
		}
		
		if(liAntes != null){
			lstEmpresasSeleccionadas.insertBefore(liAcl, liAntes);
		} else {
			lstEmpresasSeleccionadas.appendChild(liAcl);
		}
		
		
		/*Collections.sort(lstEmpresasSeleccionadas.getItems(), new Comparator<Listitem>() {

			@Override
			public int compare(Listitem o1, Listitem o2) {
				
				DocAcl acl1 = o1.getValue();
				DocAcl acl2 = o1.getValue();
				
				return acl1.getDocSistArch().getSistArchNombre().compareToIgnoreCase(acl2.getDocSistArch().getSistArchNombre());
			}
		});*/
		
	}

	private void agregarItem(Listitem selecion){
		DocSistArch entidad = selecion.getValue();
		DocAcl acl = new DocAcl();
		acl.setAclSiEscritura(false);
		acl.setAclSiLectura(true);
		acl.setAudiFechModi(new Date());
		acl.setAudiMotiAnul(null);
		acl.setAudiSiAnul(false);
		acl.setAudiUsuario(usuario.getLogin());
		acl.setDocSistArch(entidad);
		acl.setPltUsuario(usu);
		
		agregarAcl(acl);
		
		
		lstEmpresasDisponibles.removeChild(selecion);
		
		
	}
	
	public void onClick$btnQuitar(Event event){
		while(true){
			Listitem seleccion = lstEmpresasSeleccionadas.getSelectedItem();
			if(seleccion == null){
				break;
			}
			
			eliminarItemSeleccion(seleccion);
			
			
		}
	}
	
	private void eliminarItemSeleccion(Listitem seleccion){
		DocAcl acl = seleccion.getValue();
		
		if(acl.getAclIdn() != 0L){
			listaEliminar.add(acl);
		}
		
		DocSistArch entidad = acl.getDocSistArch();
		agregarEntidad(entidad);
		
		lstEmpresasSeleccionadas.removeChild(seleccion);
		
	}
	
	public void onClick$btnQuitarTodos(Event event){
		
		while(lstEmpresasSeleccionadas.getItemCount() > 0){
			Listitem seleccion = lstEmpresasSeleccionadas.getItemAtIndex(0);
			eliminarItemSeleccion(seleccion);	
		}
		
	}
	
	@Override
	public void onClick$btnAceptar(Event evt) throws Exception{
		try {
			
		
		establecerDatos();
		
		if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			usuarioNgc.crearUsuario(usu, usu.getLogin());
			asociarRolesUsuario(usu);
		} else if (formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR)) {
			String Clave1 = txtConfirmarClave.getValue();
			String Clave2 = txtClave.getValue();
			if(!Clave1.equalsIgnoreCase(Clave2)){
				throw new PltException(ConstantesAdmin.ERR0012);
			}
			usuarioNgc.modificarUsuario(usu);
			
			//Se eliminan todos los roles asociados anteriormente
			plataformaNgc.eliminarRolesUsuario(usu);
			
			//Se asocian los roles nuevos al usuario.
			asociarRolesUsuario(usu);
			
		}else if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			String nota = solicitarNota();
			
			usu.setAudiFechModi(new Date());
			usu.setAudiMotiAnul(nota);
			usu.setAudiSiAnul(true);
			usu.setAudiUsuario(usuario.getLogin());
			usuarioNgc.anularUsuario(usu);
			
		}
		
		
		//insertar o eliminar ACL
		for(Listitem li : lstEmpresasSeleccionadas.getItems()){
			DocAcl acl = li.getValue();
			
			if(acl.getAclIdn() == 0L){
				acl.setAudiUsuario(usuario.getLogin());
				acl.setAudiFechModi(new Date());
				sistemaArchivoNgc.registrarAcl(acl);
			}	
		}
		
		//insertar o eliminar ACL
		for(DocAcl acl : listaEliminar){				
			sistemaArchivoNgc.eliminarAcl(acl);
		}
		
		Events.sendEvent(new Event(Events.ON_CLOSE,this.self,usu));
		} catch (PltException e) {
			logger.error(new StringBuilder("Error metodo onClick$btnAceptar usuario").append(e.getClass().getName()).append(": ").append(e.getMessage()), e);
			Messagebox.show(Labels.getLabel(e.getCodigo()), "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

}
