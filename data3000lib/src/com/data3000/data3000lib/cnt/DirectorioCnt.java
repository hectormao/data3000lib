package com.data3000.data3000lib.cnt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
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
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;

public class DirectorioCnt extends WindowComposer {
	
	private Window winDirectorio;
	private Textbox txtNombre;
	private Textbox txtDescripcion;
	private Tree trPermisos;
	
	/**
	 * BEANS
	 */
	private PlataformaNgc plataformaNgc;
	private UsuarioNgc usuarioNgc;
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private DocSistArch directorioPadre;
	private DocSistArch directorio;
	
	@Override
	public void doAfterCompose(Window winDirectorio) throws Exception{
		
		super.doAfterCompose(winDirectorio);
		
		
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			directorioPadre = (DocSistArch) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			directorio = new DocSistArch();
		} else if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR) || formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			directorio = (DocSistArch) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			directorioPadre = directorio.getDocSistArch();
		}
		cargarArbolPermisos();
		
		
		
	}

	private void cargarArbolPermisos() throws Exception{
		
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
		Treecell celdaNombreUsuario = new Treecell(Labels.getLabel("app.usuario"));
		celdaNombreUsuario.setStyle("font-weight:bold;");
		filaUsuario.appendChild(celdaNombreUsuario);
		itemUsuario.appendChild(filaUsuario);
		hijosRaiz.appendChild(itemUsuario);
		
		trPermisos.appendChild(hijosRaiz);
		
		agregarRoles(itemRol);
		
		agregarUsuarios(itemUsuario);
		
		
		
	}

	private void agregarUsuarios(Treeitem item) throws Exception{
		List<PltUsuario> usuarios = usuarioNgc.getUsuariosDiferentesA((PltUsuario)usuario);
		Treechildren hijos = new Treechildren();
		for(PltUsuario usuario : usuarios){
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
			
			chkSiEscritura.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					if(chkSiEscritura.isChecked()){
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

	private void agregarRoles(Treeitem item) throws Exception{
		List<PltRol> roles = plataformaNgc.getRoles();
		Treechildren hijos = new Treechildren();
		for(PltRol rol : roles){
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
			
			
			chkSiEscritura.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					if(chkSiEscritura.isChecked()){
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
	
	public void onClick$btnCancelar(Event event){
		Events.sendEvent(Events.ON_CLOSE,winDirectorio,null);
	}
	
	public void onClick$btnAceptar(Event event) throws Exception{
		
		
		
		directorio.setAudiChecksum(null);
		directorio.setAudiFechModi(new Date());
		directorio.setAudiMotiAnul(null);
		directorio.setAudiSiAnul(false);
		directorio.setAudiUsuario(usuario.getLogin());
		directorio.setDocSistArch(directorioPadre);
		directorio.setSistArchDescripcion(txtDescripcion.getValue());
		directorio.setSistArchNombre(txtNombre.getValue());
		directorio.setPltUsuario((PltUsuario)usuario);
		
		
		List<DocAcl> permisos = new ArrayList<DocAcl>();
		
		Treechildren hijosRaiz = trPermisos.getTreechildren();
		tomarPermisos(permisos, hijosRaiz);
		
		sistemaArchivoNgc.registrarDirectorio(directorio,permisos);
		
		
		Events.sendEvent(Events.ON_CLOSE,winDirectorio,ConstantesAdmin.EXITO);
		
	}
	
	public void tomarPermisos(List<DocAcl> permisos, Treechildren arbol){
		
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
						permiso.setDocArchivo(null);
						permiso.setDocSistArch(directorio);
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

	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}

	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}
	
	
	
}
