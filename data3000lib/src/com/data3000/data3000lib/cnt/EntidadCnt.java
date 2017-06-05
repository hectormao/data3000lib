package com.data3000.data3000lib.cnt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.data3000.admin.bd.PltRol;
import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.exc.PltException;
import com.data3000.admin.ngc.UsuarioNgc;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSerieSist;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class EntidadCnt extends WindowComposer{
	
	private Window winSistemaArchivos;
	private Textbox txtNombre;
	private Textbox txtDescripcion;
	private Tree trPermisos;
	//private Listbox lbxSeriesDoc;
	
	/**
	 * BEANS
	 */
	
	private UsuarioNgc usuarioNgc;
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private DocSistArch directorioPadre;
	private DocSistArch directorio;
	
	private Map<Long,DocAcl> permisosRol;
	private Map<Long,DocAcl> permisosUsuario;
	private boolean isEditar;
	private List<DocAcl> listaPermisos;
	//private List<DocSerieSist> listaSeries;
	private boolean isBorrar = false;
	
	@Override
	public void doAfterCompose(Window winSistemaArchivos) throws Exception{
		
		super.doAfterCompose(winSistemaArchivos);
		
		
		permisosRol = new HashMap<Long, DocAcl>();
		permisosUsuario = new HashMap<Long, DocAcl>();
		directorioPadre = (DocSistArch) argumentos.get(ConstantesAdmin.ARG_SELECCION);
		directorio = new DocSistArch();
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR)){
			directorio = (DocSistArch) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			directorioPadre = directorio.getDocSistArch();
			cargarDatosDirectorio();
			isEditar = true;
			
//			Set<DocAcl> permisos = directorio.getDocAcls();
//			for(DocAcl acl : permisos){
//				
//				PltUsuario aclUsuario = acl.getPltUsuario();
//				PltRol aclRol = acl.getPltRol();
//				
//				if(aclUsuario != null){
//					permisosUsuario.put(aclUsuario.getUsuaIdn(), acl);					
//				} else {
//					permisosRol.put(aclRol.getRolIdn(), acl);
//				}
//				
//				
//			}
			
			
			
		}
		
		
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			directorio = (DocSistArch) argumentos.get(ConstantesAdmin.ARG_SELECCION);
			directorioPadre = directorio;
			cargarDatosDirectorio();
			isBorrar = true;
			super.soloConsulta();
		}
		
		
	}
	
	public void onCreate$winSistemaArchivos(Event evt) throws Exception{
		try{
			if(isEditar || isBorrar){
			if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR) || formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
				if(directorio != null && directorio.getPltUsuario().getUsuaIdn() != ((PltUsuario)usuario).getUsuaIdn()){
					throw new PltException(ConstantesData3000.ERR1005);
				}
			}

			
			 listaPermisos = new ArrayList<DocAcl>();
			   listaPermisos = sistemaArchivoNgc.getObtenerPermisosRol(directorio.getSistArchIdn());
		}
//		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
//			cargarDatosDirectorio();
//			 listaPermisos = new ArrayList<DocAcl>();
//			   listaPermisos = sistemaArchivoNgc.getObtenerPermisosRol(directorio.getSistArchIdn());
//		}
			//cargarSeriesDocumentales();
			cargarArbolPermisos();
		} catch(PltException ex){
			Events.sendEvent(Events.ON_CLOSE, this.self, null);
			Messagebox.show(Labels.getLabel(ex.getCodigo()), "Error", Messagebox.OK, Messagebox.ERROR);
			logger.error("Al editar directorio",ex);
		}
		
	}
        
	/*private void cargarSeriesDocumentales(){
		
		List<DocSerieDoc> lista = new ArrayList<DocSerieDoc>();
		lista = sistemaArchivoNgc.getSeriesDocumentales();
		//listaSeries = new ArrayList<DocSerieSist>();
		if(isEditar || isBorrar){
		//listaSeries = sistemaArchivoNgc.getSeriesSisEditar(directorio.getSistArchIdn());
		}
		for (DocSerieDoc docSerieDoc : lista) {
			Listitem item = new Listitem();
			item.setValue(docSerieDoc);
			item.setLabel(docSerieDoc.getSerieDocNombre());
			item.setTooltiptext("Código:"+docSerieDoc.getSerieDocCodigo());
			if(isEditar || isBorrar){
			for (DocSerieSist docSerieSist : listaSeries) {
				if(docSerieDoc.getSerieDocIdn() ==  docSerieSist.getDocSerieDoc().getSerieDocIdn()){
					item.setSelected(true);
					
				}
			}
			if (isBorrar) {
				item.setDisabled(true);
			}
		  }
			lbxSeriesDoc.appendChild(item);
		}
		
	}*/
	
	private void cargarDatosDirectorio(){
		txtNombre.setValue(directorio.getSistArchNombre());
		txtDescripcion.setValue(directorio.getSistArchDescripcion());
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
			
			DocAcl acl = permisosUsuario.get(usuario.getUsuaIdn());
			
			Treeitem itemUsuario = new Treeitem();			
			Treerow filaUsuario = new Treerow();
			
			itemUsuario.setValue(usuario);
//			if(acl==null && isEditar){
//				 for (DocAcl pltRol : listaPermisos) {
//					 if(pltRol.getPltUsuario() != null){
//						if(pltRol.getPltUsuario().getUsuaIdn() == usuario.getUsuaIdn()){
//							itemUsuario.setAttribute(ConstantesData3000.ATRIBUTO_ACL, pltRol);	
//						}
//					 }
//					}
//			}else{
				itemUsuario.setAttribute(ConstantesData3000.ATRIBUTO_ACL, acl);	
//			}
			StringBuilder nombre = new StringBuilder(usuario.getUsuaNombre());
			nombre.append(" (");
			nombre.append(usuario.getLogin());
			nombre.append(")");
			
			Treecell celdaNombre = new Treecell(nombre.toString());
			filaUsuario.appendChild(celdaNombre);
			
			final Checkbox chkSiLectura = new Checkbox();
			boolean siLectura = acl != null && acl.isAclSiLectura();			
			chkSiLectura.setChecked(siLectura);
			Treecell celdaSiLectura = new Treecell();
			celdaSiLectura.appendChild(chkSiLectura);
			filaUsuario.appendChild(celdaSiLectura);
			
			
			
			
			
			final Checkbox chkSiEscritura = new Checkbox();
			boolean siEscritura = acl != null && acl.isAclSiEscritura();
			chkSiEscritura.setChecked(siEscritura);
			
			
			if(siEscritura){
				chkSiLectura.setChecked(true);
				chkSiLectura.setDisabled(true);
			}
			if(isEditar || isBorrar){
				if(listaPermisos!= null){
				for (DocAcl pltUsuario : listaPermisos) {
				  if(pltUsuario.getPltUsuario() != null){
					if(usuario.getUsuaIdn() == pltUsuario.getPltUsuario().getUsuaIdn()){
						chkSiLectura.setChecked(pltUsuario.isAclSiLectura());
						chkSiEscritura.setChecked(pltUsuario.isAclSiEscritura());
						
					  }
					 
				    }
				  }
				 if(isBorrar){
						chkSiLectura.setDisabled(true);
						chkSiEscritura.setDisabled(true);
					 }
				}
			}
			
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
			
			DocAcl acl = permisosRol.get(rol.getRolIdn());
			
			Treeitem itemRol = new Treeitem();
			Treerow filarol = new Treerow();

			itemRol.setValue(rol);
//			if(acl==null && isEditar){
//				for (DocAcl pltRol : listaPermisos) {
//					if(pltRol.getPltRol() != null){
//					if(pltRol.getPltRol().getRolIdn() == rol.getRolIdn()){
//						itemRol.setAttribute(ConstantesData3000.ATRIBUTO_ACL, pltRol);	
//					}
//				  }
//				}
//			}else{
				itemRol.setAttribute(ConstantesData3000.ATRIBUTO_ACL, acl);	
//			}
			
			
			Treecell celdaNombre = new Treecell(rol.getRolNombre());
			filarol.appendChild(celdaNombre);
			
			final Checkbox chkSiLectura = new Checkbox();
			
			boolean siLectura = acl != null && acl.isAclSiLectura();			
			chkSiLectura.setChecked(siLectura);
			
			Treecell celdaSiLectura = new Treecell();
			celdaSiLectura.appendChild(chkSiLectura);
			filarol.appendChild(celdaSiLectura);
			
			
			
			final Checkbox chkSiEscritura = new Checkbox();
			
			boolean siEscritura = acl != null && acl.isAclSiEscritura();
			chkSiEscritura.setChecked(siEscritura);
			
			if(siEscritura){
				chkSiLectura.setChecked(true);
				chkSiLectura.setDisabled(true);
			}
			if(isEditar || isBorrar){
				if(listaPermisos!=null){
				for (DocAcl pltUsuario : listaPermisos) {
					if(pltUsuario.getPltRol() != null){
					 if(rol.getRolIdn() == pltUsuario.getPltRol().getRolIdn()){
						chkSiLectura.setChecked(pltUsuario.isAclSiLectura());
						chkSiEscritura.setChecked(pltUsuario.isAclSiEscritura());
					}
				  }
				}
				if(isBorrar){
					chkSiLectura.setDisabled(true);
					chkSiEscritura.setDisabled(true);
				}
			  }
			}
			
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
		Events.sendEvent(Events.ON_CLOSE,winSistemaArchivos,null);
	}
	
	public void onClick$btnAceptar(Event event) throws Exception{
		
		
		if(formulario.getTipo().equals(ConstantesAdmin.FORMULARIO_TIPO_BORRAR)){
			String nota = solicitarNota();
			
			directorioPadre.setAudiFechModi(new Date());
			directorioPadre.setAudiMotiAnul(nota);
			directorioPadre.setAudiSiAnul(true);
			sistemaArchivoNgc.anularDirectorio(directorioPadre);
			
	}else{
		directorio.setAudiChecksum(null);
		directorio.setAudiFechModi(new Date());
		directorio.setAudiMotiAnul(null);
		directorio.setAudiSiAnul(false);
		directorio.setAudiUsuario(usuario.getLogin());
		directorio.setDocSistArch(null);
		directorio.setSistArchDescripcion(txtDescripcion.getValue());
		directorio.setSistArchNombre(txtNombre.getValue());
		directorio.setSistArchTipo(ConstantesData3000.SISTEMA_ARCHIVO_ENTIDAD);
	

		 
		 
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			directorio.setPltUsuario((PltUsuario)usuario);
		}
		
		List<DocAcl> permisosNuevos = new ArrayList<DocAcl>();
		List<DocAcl> permisosEdicion = new ArrayList<DocAcl>();
		List<DocAcl> permisosEliminacion = new ArrayList<DocAcl>();
		
		if(!isEditar){
			directorio.getDocAcls().clear();	
		}
		
		Treechildren hijosRaiz = trPermisos.getTreechildren();
		tomarPermisos(permisosNuevos, permisosEdicion, permisosEliminacion, hijosRaiz);
		
		if(!isEditar){
			directorio.getDocAcls().addAll(permisosNuevos);
			directorio.getDocAcls().addAll(permisosEdicion);
		}
		
		if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_INSERTAR)){
			long id = sistemaArchivoNgc.registrarDirectorio(directorio,permisosNuevos);
			directorio.setSistArchIdn(id);
			/*for (Listitem docAcl : lbxSeriesDoc.getItems()) {
				if(docAcl.isSelected()){
					DocSerieSist docSerieSist = new DocSerieSist();
					docSerieSist.setDocSerieDoc(docAcl.getValue());
					docSerieSist.setDocSistArch(directorio);
					docSerieSist.setAudiUsuario(usuario.getLogin());
					docSerieSist.setAudiFechModi(new Date());
					docSerieSist.setAudiSiAnul(false);
					sistemaArchivoNgc.RegistrarSeriesSistema(docSerieSist);
				}
			}*/
		} 
		else if(formulario.getTipo().equalsIgnoreCase(ConstantesAdmin.FORMULARIO_TIPO_EDITAR)){
			sistemaArchivoNgc.modificarDirectorio(directorio,permisosNuevos, permisosEdicion, permisosEliminacion,listaPermisos);
			/*sistemaArchivoNgc.ClearSeriesDocumentales(listaSeries);
			for (Listitem item : lbxSeriesDoc.getItems()) {
				if(item.isSelected()){
					DocSerieSist docSerieSist = new DocSerieSist();
					docSerieSist.setDocSerieDoc(item.getValue());
					docSerieSist.setDocSistArch(directorio);
					docSerieSist.setAudiUsuario(usuario.getLogin());
					docSerieSist.setAudiFechModi(new Date());
					docSerieSist.setAudiSiAnul(false);
					sistemaArchivoNgc.RegistrarSeriesSistema(docSerieSist);
				}
			}*/
		}
		
	}
		
		Events.sendEvent(Events.ON_CLOSE,winSistemaArchivos,ConstantesAdmin.EXITO);
		
	}
	
		public void tomarPermisos(List<DocAcl> permisosNuevos, List<DocAcl> permisosEdicion, List<DocAcl> permisosEliminacion, Treechildren arbol){
			
			Collection<Treeitem> itemsHijo = arbol.getItems();
			
			
			
			if(itemsHijo == null){
				return;
			} else if(! itemsHijo.isEmpty()){
				for(Treeitem item : itemsHijo){				
					Object valor = item.getValue();
					if(valor != null && (valor instanceof PltUsuario || valor instanceof PltRol)){
						
						DocAcl acl = (DocAcl) item.getAttribute(ConstantesData3000.ATRIBUTO_ACL);
						
						Checkbox chkSiLectura = (Checkbox) item.getAttribute("chkSiLectura");
						Checkbox chkSiEscritura = (Checkbox) item.getAttribute("chkSiEscritura");
						
						boolean siLeer = chkSiLectura.isChecked();
						boolean siEscribir = chkSiEscritura.isChecked();
						
						if(siLeer || siEscribir){
							if(acl == null){
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
								if(isEditar){
									permisosEdicion.add(permiso);
								}else {
								permisosNuevos.add(permiso);
								}
							} else {
								boolean actualizar = false;
								if(acl.isAclSiLectura() != siLeer){
									acl.setAclSiLectura(siLeer);
									actualizar = true;
								}
								
								if(acl.isAclSiEscritura() != siEscribir){
									acl.setAclSiEscritura(siEscribir);
									actualizar = true;
								}
								
								if(actualizar){
									acl.setAudiUsuario(usuario.getLogin());
									acl.setAudiFechModi(new Date());
									permisosEdicion.add(acl);
									
								}
							}
							
						} else {
							if(acl != null){
								permisosEliminacion.add(acl);
							}
						}
						
						
					}
					
				}
			}
			
			
			
			
			
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
