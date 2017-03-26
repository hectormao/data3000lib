package com.data3000.data3000lib.cnt;

import java.awt.MenuItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Tbody;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.cmp.TablaDatos;
import com.data3000.admin.ngc.PlataformaNgc;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.admin.vo.Formulario;
import com.data3000.admin.vo.FormularioHijo;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class EscritorioCnt extends WindowComposer {
	
	private Tree trFileSystem;
	
	private Logger logger;
	
	
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	
	private Div divTrabajo;
	
	private Menupopup menuArchivo;
	private Toolbarbutton btnCrearEntidad;
	private Toolbarbutton btnCrearDependencia;
	private Toolbarbutton btnNuevoDirectorio;
//	private Toolbarbutton btnEditarDirectorio;
//	private Toolbarbutton btnEliminarDirectorio;
//	private Toolbarbutton btnCrearSistemaArchivo;
	
	
	private DocSistArch seleccion = null;
	
	
	
	private Window winTablaDatos;
	
	private class ItemDir {
		public DocSistArch dir;
		public Treeitem item;
		public Treecell celda;
	}
	
	@Override
	public void doAfterCompose(Window winEscritorio) throws Exception{
		
		super.doAfterCompose(winEscritorio);
		
		logger = Logger.getLogger(this.getClass());
		
		
		java.io.InputStream zulInput = this.getClass().getClassLoader().getResourceAsStream("com/data3000/admin/zul/tablaDatos.zul") ;
		java.io.Reader zulReader = new java.io.InputStreamReader(zulInput) ;
		
		Map<String,Object> parametros = new HashMap<String, Object>();
		parametros.put(ConstantesAdmin.ARG_USUARIO,usuario);
		parametros.put(ConstantesAdmin.ARG_FORMULARIO,formulario);
		parametros.put(ConstantesAdmin.ARG_CLASE,DocArchivo.class);
		
		
		winTablaDatos = (Window) Executions.createComponentsDirectly(zulReader,"zul",divTrabajo,parametros) ;
		winTablaDatos.setTitle(null);
		winTablaDatos.setBorder("none");
		winTablaDatos.doEmbedded();
		
		
		List<FormularioHijo> hijos = formulario.getHijos();
		if(hijos != null && ! hijos.isEmpty()){
			for(FormularioHijo hijo : hijos){
				if(hijo.getTipo().equals(ConstantesAdmin.HIJO_BOTON)){
					
					crearMenuItem(hijo.getHijo());
					
				}
			}
		}
		
		
		
		String nombreFormularioCrearEntidad = (String) btnCrearEntidad.getAttribute(ConstantesAdmin.ATRIBUTO_FORMULARIO);
		final Formulario frmCrearEntidad = getFormulario(nombreFormularioCrearEntidad);
		if(frmCrearEntidad == null){
			btnCrearEntidad.setDisabled(true);
			btnCrearEntidad.setVisible(false);
		} else {
			
			btnCrearEntidad.setImage(frmCrearEntidad.getUrlIcono());
			btnCrearEntidad.setTooltip(frmCrearEntidad.getTooltip());
			btnCrearEntidad.setTooltiptext("Crear entidad");
			
			btnCrearEntidad.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				@Override
				public void onEvent(Event arg0) throws Exception {
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {
						 @Override
							public void onEvent(Event arg0) throws Exception {
							 String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
								 	Treechildren raiz = trFileSystem.getTreechildren();
								 	cargarArbol(null,raiz);
								}
						 	}
					 
					 };
					 
					 Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
						
					 DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
						
					abrirFormulario(frmCrearEntidad, directorioPadre, eventoCerrar);
				}
				
				
				
			});
		}
		
		// funcionalidad para crear dependencia
		String nombreFormularioCrearDependencia = (String) btnCrearDependencia.getAttribute(ConstantesAdmin.ATRIBUTO_FORMULARIO);
		final Formulario frmCrearDependencia = getFormulario(nombreFormularioCrearDependencia);
		if(frmCrearEntidad == null){
			btnCrearDependencia.setDisabled(true);
			btnCrearDependencia.setVisible(false);
		} else {
			
			btnCrearDependencia.setImage(frmCrearDependencia.getUrlIcono());
			btnCrearDependencia.setTooltip(frmCrearDependencia.getTooltip());
			btnCrearDependencia.setTooltiptext("Crear dependencia");
			
			btnCrearDependencia.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				@Override
				public void onEvent(Event arg0) throws Exception {
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {
						 @Override
							public void onEvent(Event arg0) throws Exception {
							 	String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									onSelect$trFileSystem(arg0);
								}						 
						 	}
					 
					 };
					 
					 Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
						
					 DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
						
					abrirFormulario(frmCrearDependencia, directorioPadre, eventoCerrar);
				}
				
				
				
			});
		}
		
		// funcionalidad para crear directorio
		String nombreFormulario = (String) btnNuevoDirectorio.getAttribute(ConstantesAdmin.ATRIBUTO_FORMULARIO);
		final Formulario frmNuevoDirectorio = getFormulario(nombreFormulario);
		if(frmNuevoDirectorio == null){
			btnNuevoDirectorio.setDisabled(true);
			btnNuevoDirectorio.setVisible(false);
		} else {
			
			btnNuevoDirectorio.setImage(frmNuevoDirectorio.getUrlIcono());
			btnNuevoDirectorio.setTooltip(frmNuevoDirectorio.getTooltip());
			btnNuevoDirectorio.setTooltiptext("Crear directorio");
			
			btnNuevoDirectorio.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {
							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									onSelect$trFileSystem(arg0);
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmNuevoDirectorio, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		
		
		
		
//		String nombreFormularioEditar = (String) btnEditarDirectorio.getAttribute(ConstantesAdmin.ATRIBUTO_FORMULARIO);
//		final Formulario frmEditarDirectorio = getFormulario(nombreFormularioEditar);
//		if(frmEditarDirectorio == null){
//			btnEditarDirectorio.setDisabled(true);
//			btnEditarDirectorio.setVisible(false);
//		} else {
//			
//			btnEditarDirectorio.setImage(frmEditarDirectorio.getUrlIcono());
//			btnEditarDirectorio.setTooltip(frmEditarDirectorio.getTooltip());
//			btnEditarDirectorio.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//				@Override
//				public void onEvent(Event arg0) throws Exception {
//					
//					 EventListener<Event> eventoCerrar = new EventListener<Event>() {
//
//							@Override
//							public void onEvent(Event arg0) throws Exception {
//								String res = (String) arg0.getData();
//								if(res != null && res.equals(ConstantesAdmin.EXITO)){
//									
//									
//									
//									Treeitem li = trFileSystem.getSelectedItem();
//									if(li != null){
//										
//										DocSistArch directorioTmp = li.getValue();
//										
//										DocSistArch directorio = sistemaArchivoNgc.getDirectorio(directorioTmp.getSistArchIdn());
//										
//										Treerow fila = (Treerow) li.getFirstChild();
//										Treecell celda = (Treecell) fila.getFirstChild();
//										li.setValue(directorio);
//										celda.setLabel(directorio.getSistArchNombre());
//										celda.setTooltiptext(directorio.getSistArchDescripcion());
//									}
//									
//									
//									//onSelect$trFileSystem(arg0);
//								}
//								
//							}
//						};
//					
//					
//					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
//					
//					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
//					
//					
//					abrirFormulario(frmEditarDirectorio, directorioPadre, eventoCerrar);
//					
//					
//				}
//				
//			});
//			
//		}
		
//		String nombreFormularioEliminar = (String) btnEliminarDirectorio.getAttribute(ConstantesAdmin.ATRIBUTO_FORMULARIO);
//		final Formulario frmEliminarDirectorio = getFormulario(nombreFormularioEliminar);
//		if(frmEliminarDirectorio == null){
//			btnEliminarDirectorio.setDisabled(true);
//			btnEliminarDirectorio.setVisible(false);
//		} else {
//			
//			btnEliminarDirectorio.setImage(frmEliminarDirectorio.getUrlIcono());
//			btnEliminarDirectorio.setTooltip(frmEliminarDirectorio.getTooltip());
//			btnEliminarDirectorio.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//				@Override
//				public void onEvent(Event arg0) throws Exception {
//					
//					 EventListener<Event> eventoCerrar = new EventListener<Event>() {
//
//							@Override
//							public void onEvent(Event arg0) throws Exception {
//								String res = (String) arg0.getData();
//								if(res != null && res.equals(ConstantesAdmin.EXITO)){
//									Treeitem li = trFileSystem.getSelectedItem();
//									if(li != null){										
//										Component padre = li.getParent();										
//										padre.removeChild(li);
//									}
//								}
//								
//							}
//						};
//					
//					
//					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
//					
//					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
//					
//					
//					abrirFormulario(frmEliminarDirectorio, directorioPadre, eventoCerrar);
//					
//					
//				}
//				
//			});
//			
//		}
		
		cargarArbol();
	}
	
	
	
	private void crearMenuItem(final Formulario formularioHijo){		
		Menuitem item = new Menuitem();					
		
		String nombre = formularioHijo.getNombre();
		String leyenda = Labels.getLabel(nombre);
		if(leyenda == null){
			leyenda = nombre;
		}
		
		item.setLabel(leyenda);
		item.setImage(formularioHijo.getUrlIcono());
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				
				Map<String,Object> parametros = new HashMap<String, Object>();
				
				parametros.putAll(argumentos);
				parametros.put(ConstantesAdmin.ARG_FORMULARIO,formularioHijo);
				parametros.put(ConstantesAdmin.OBJETO_PADRE,seleccion);							
				
				java.io.InputStream zulInput = this.getClass().getClassLoader().getResourceAsStream(formularioHijo.getUrl()) ;
				java.io.Reader zulReader = new java.io.InputStreamReader(zulInput) ;
				
				
				Window winCargar = (Window) Executions.createComponentsDirectly(zulReader,"zul",divTrabajo,parametros) ;
				
				String nombre = formularioHijo.getNombre();
				
				String titulo = Labels.getLabel(nombre);
				if(titulo == null){
					titulo = formularioHijo.getNombre();
				}
				
				winCargar.setTitle(titulo);
				
				winCargar.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						String res = (String) arg0.getData();
						if(res != null && res.equals(ConstantesAdmin.EXITO)){
							
							Treeitem tiSeleccion = trFileSystem.getSelectedItem();							
							DocSistArch directorio = tiSeleccion != null ? (DocSistArch) tiSeleccion.getValue() : null;
							if(directorio != null){							
								actualizarTablaDatos(directorio);
							}
						}
						
					}
				});
				
				winCargar.doModal();							
			}
		});
		
		menuArchivo.appendChild(item);
		
		
		
	}
	
	private void cargarArbol() throws Exception {
		if(logger.isDebugEnabled()) logger.debug("Consultando arbol ...");
		
		Treechildren raiz = new Treechildren();		
		trFileSystem.appendChild(raiz);
		
		List<DocSistArch> lista = new ArrayList<DocSistArch>();
		lista = sistemaArchivoNgc.getEntidades();
		
		Map<Long,ItemDir> mapaDir = new HashMap<>();
		raiz.setAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR, mapaDir);
		
		
		for (DocSistArch docSistArch : lista) {
			
			Treeitem tiEntidad = new Treeitem();
			tiEntidad.setValue(docSistArch);
			Treerow fila = new Treerow();
			Treecell celda = new Treecell(docSistArch.getSistArchNombre());
			celda.setTooltiptext(docSistArch.getSistArchDescripcion());
			fila.appendChild(celda);
			
			fila.setContext(menuArchivo);
			fila.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					seleccion = docSistArch;
				}
			});
			
			
			tiEntidad.appendChild(fila);
			tiEntidad.setImage("img/iconos/entidadIconp.png");
			
			
			raiz.appendChild(tiEntidad);
			
			Treechildren hijosRaiz = new Treechildren();
			tiEntidad.appendChild(hijosRaiz);
			
			ItemDir itemDir = new ItemDir();
			itemDir.dir = docSistArch;
			itemDir.item = tiEntidad;
			itemDir.celda = celda;
			
			mapaDir.put(docSistArch.getSistArchIdn(), itemDir);
			
			
			cargarArbol(docSistArch, hijosRaiz);
		}
		
		
	}
	
	private void cargarArbol(DocSistArch padre, Treechildren arbolPadre) throws Exception{
		if(logger.isDebugEnabled()) logger.debug(new StringBuilder("Cargando hijos para ...").append(padre != null ? padre.getSistArchNombre() : "Raiz").toString());
		
		List<DocSistArch> listaHijos = sistemaArchivoNgc.getHijos(padre, (PltUsuario) usuario);
		
		Map<Long,ItemDir> mapaDir = (Map<Long, ItemDir>) arbolPadre.getAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR);
		//List<ItemDir> listaDir = (List<ItemDir>) arbolPadre.getAttribute(ConstantesData3000.ATRIBUTO_LISTA_DIR);
		
		
		if(mapaDir == null){
			mapaDir = new HashMap<Long, ItemDir>();
			arbolPadre.setAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR,mapaDir);
		}
		
		Map<Long,ItemDir> mapaDirAux = new HashMap<Long, ItemDir>();
		mapaDirAux.putAll(mapaDir);
		
		//int idx = 0;
		
		for(final DocSistArch hijo : listaHijos){
			
			
			ItemDir dirAux = mapaDirAux.remove(hijo.getSistArchIdn());
			
			if(dirAux == null){	
				Treeitem item = new Treeitem();
				item.setValue(hijo);
				Treerow fila = new Treerow();
				Treecell celda = new Treecell(hijo.getSistArchNombre());
				celda.setTooltiptext(hijo.getSistArchDescripcion());
				fila.appendChild(celda);
				item.appendChild(fila);
				if(hijo.getSistArchTipo().equals(ConstantesData3000.SISTEMA_ARCHIVO_DEPENDENCIA)){
					item.setImage("img/iconos/usuarioPequeno.png");
				}else if(hijo.getSistArchTipo().equals(ConstantesData3000.SISTEMA_ARCHIVO_DIRECTORIO)){
					item.setImage("img/iconos/folderPequeno.png");
				}else if(hijo.getSistArchTipo().equals(ConstantesData3000.SISTEMA_ARCHIVO_ENTIDAD)){
					item.setImage("img/iconos/entidadIconp.png");
				}
				fila.setContext(menuArchivo);
				fila.addEventListener(Events.ON_RIGHT_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						seleccion = hijo;
					}
				});
				
				
				dirAux = new ItemDir();
				dirAux.dir = hijo;
				dirAux.celda = celda;
				dirAux.item = item;
				
				mapaDir.put(hijo.getSistArchIdn(), dirAux);
				
				//arbolPadre.appendChild(item);
				//buscando insertar en arbol de forma ordenada por el nombre del directorio
				boolean inserto = false;
				for(Treeitem itemHijo : arbolPadre.getItems()){
					DocSistArch hijoComparar = itemHijo.getValue();
					if(logger.isDebugEnabled()) logger.debug(new StringBuilder("hijoComparar: ").append(hijoComparar.getSistArchNombre()).append(" vs. hijo: ").append(hijo.getSistArchNombre()).append(" resultado: ").append(hijoComparar.getSistArchNombre().compareTo(hijo.getSistArchNombre())).toString());					
					if(hijoComparar.getSistArchNombre().toUpperCase().compareTo(hijo.getSistArchNombre().toUpperCase()) > 0){
						if(logger.isDebugEnabled()) logger.debug(new StringBuilder("Insertando antes de: ").append(hijoComparar.getSistArchNombre()).toString());
						arbolPadre.insertBefore(item, itemHijo);
						inserto = true;
						break;
					}
				}
				
				if(! inserto){
					arbolPadre.appendChild(item);
				}
			} else {
				if(! dirAux.dir.getSistArchNombre().equals(hijo.getSistArchNombre())){
					//cambiar nombre
					dirAux.celda.setLabel(hijo.getSistArchNombre());
					dirAux.item.setValue(hijo);
					dirAux.dir = hijo;				
				}
				
				if(! dirAux.dir.getSistArchDescripcion().equals(hijo.getSistArchDescripcion())){
					//cambiar tooltip
					dirAux.celda.setTooltiptext(hijo.getSistArchDescripcion());
					dirAux.item.setValue(hijo);
					dirAux.dir = hijo;
				}
			}
		}
		
		//Eliminando hijos que han sido borrados de la BD
		for(ItemDir dir : mapaDirAux.values()){
			Component hijo = dir.item.getParent();
			hijo.removeChild(dir.item);
			mapaDir.remove(dir.dir.getSistArchIdn());
		}
		
	}
	
	
	public void onSelect$trFileSystem(Event event) throws Exception {
		
		
		
		Treeitem tiSeleccion = trFileSystem.getSelectedItem();
		
		DocSistArch directorio = tiSeleccion != null ? (DocSistArch) tiSeleccion.getValue() : null; 
		
		
			
		if(directorio == null){
			directorio = new DocSistArch();
			directorio.setSistArchIdn(0L);
		}
		
		actualizarTablaDatos(directorio);
		
		
		if(tiSeleccion != null){
			
			Treechildren hijos = tiSeleccion.getTreechildren();
			if(hijos == null){
				hijos = new Treechildren();
				tiSeleccion.appendChild(hijos);
			}			
			DocSistArch dir = tiSeleccion.getValue();
			if(dir != null){
			cargarArbol(dir, hijos);
			}
		} else {
			Treechildren hijos = trFileSystem.getTreechildren();
			cargarArbol(null, hijos);
		}
		
	}




	private void actualizarTablaDatos(DocSistArch dir) {
		
		Map<String,Object> datos = new HashMap<String, Object>();
		datos.put(ConstantesAdmin.ACCION, ConstantesAdmin.EVENTO_REFRESCAR);
		datos.put(ConstantesAdmin.OBJETO_PADRE, dir);
		
		Events.sendEvent(Events.ON_USER, winTablaDatos, datos);
		
	}



	





	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}




	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}

	
	
	
	
}
