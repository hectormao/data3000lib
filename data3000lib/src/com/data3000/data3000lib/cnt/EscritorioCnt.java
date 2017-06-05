package com.data3000.data3000lib.cnt;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.North;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.data3000.admin.bd.PltUsuario;
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
	private Menu menuEntidad;
	private Menu menuDependencia;
	private Menu menuDirectorio;
	private Menuitem iteCrearE;
	private Menuitem iteCrearD;
	private Menuitem iteCrearDi;
	private Menuitem iteEditarE;
	private Menuitem iteEliminarE;
	private Menuitem iteEditarD;
	private Menuitem iteEliminarD;
	private Menuitem iteEditarDi;
	private Menuitem iteEliminarDi;
	
	private Bandbox buscarDirectorio;
	private String buscar;
	
	
	
//	private Toolbarbutton btnEditarDirectorio;
//	private Toolbarbutton btnEliminarDirectorio;
//	private Toolbarbutton btnCrearSistemaArchivo;
	
	
	private DocSistArch seleccion = null;
	
	
	
	private Window winTablaDatos;
	
	private Bandbox bndBuscarArchivo;
	
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
		
		
		bndBuscarArchivo = new Bandbox();
		bndBuscarArchivo.setWidth("300px");
		bndBuscarArchivo.setAutodrop(true);
		bndBuscarArchivo.setTooltiptext(Labels.getLabel("data3000.buscar.archivo"));
		
		/************************
		 <bandpopup>
										 	<groupbox>
										 		<caption label="${labels.data3000.buscar.$}"></caption>
										 		<vbox>
										 			<hbox>
										 				<div width="150px">
										 					<label value="${labels.data3000.nombre.$}" ></label>
										 				</div>
										 				<textbox id="txtBscNombre" width="200px"></textbox>
										 			</hbox>
										 			<hbox>
										 				<div width="150px">
										 				<label value="${labels.data3000.sistema.serie}"></label>
										 				</div>
										 				<tree width="300px" id="trBscSerie" multiple="false" checkmark="true" rows="4" >
															<treecols sizable="true">
																<treecol width="100%">
																</treecol >
															</treecols>
											            </tree>
													
										 			</hbox>  
										 			<hbox>
										 				<div width="150px">
										 					<label value="${labels.data3000.descripcion.$}" ></label>
										 				</div>
										 				<textbox id="txtBscDescripcion" width="200px"></textbox>
										 			</hbox>
										 			<hbox>
										 				<div width="150px">
										 					<label value="${labels.data3000.tagversion}" ></label>
										 				</div>
										 				<textbox id="txtBscTagVersion" width="200px"></textbox>
										 			</hbox>
										 		</vbox>
										 	</groupbox>
										 
										 </bandpopup>
		 ************************/
		Bandpopup winBndBuscarArchivo = new Bandpopup();
		
		Groupbox grpBuscarArchivo = new Groupbox();
		grpBuscarArchivo.appendChild(new Caption(Labels.getLabel("data3000.buscar.archivo")));
		
		Vbox vboxBuscarArchivo = new Vbox();
		
		Hbox hboxNombre = new Hbox();
		Div divNombre = new Div();
		divNombre.setWidth("150px");
		divNombre.appendChild(new Label(Labels.getLabel("data3000.nombre")));
		Textbox txtBscNombre = new Textbox();
		txtBscNombre.setWidth("200px");
		hboxNombre.appendChild(divNombre);
		hboxNombre.appendChild(txtBscNombre);		
		vboxBuscarArchivo.appendChild(hboxNombre);
		
		Hbox hboxSerie = new Hbox();
		Div divSerie = new Div();
		divSerie.setWidth("150px");
		divSerie.appendChild(new Label(Labels.getLabel("data3000.sistema.serie")));		
		Tree trBscSerie = new Tree();
		trBscSerie.setWidth("300px");
		trBscSerie.setMultiple(false);
		trBscSerie.setCheckmark(true);
		trBscSerie.setRows(4);
		hboxSerie.appendChild(divSerie);
		hboxSerie.appendChild(trBscSerie);		
		vboxBuscarArchivo.appendChild(hboxSerie);
		
		
		Hbox hboxDescripcion = new Hbox();
		Div divDescripcion = new Div();
		divDescripcion.setWidth("150px");
		divDescripcion.appendChild(new Label(Labels.getLabel("data3000.descripcion")));
		Textbox txtBscDescripcion = new Textbox();
		txtBscDescripcion.setWidth("200px");
		hboxDescripcion.appendChild(divDescripcion);
		hboxDescripcion.appendChild(txtBscDescripcion);		
		vboxBuscarArchivo.appendChild(hboxDescripcion);
		
		Hbox hboxTag = new Hbox();
		Div divTag = new Div();
		divTag.setWidth("150px");
		divTag.appendChild(new Label(Labels.getLabel("data3000.tagversion")));
		Textbox txtBscTag = new Textbox();
		txtBscTag.setWidth("200px");
		hboxTag.appendChild(divTag);
		hboxTag.appendChild(txtBscTag);		
		vboxBuscarArchivo.appendChild(hboxTag);
		
		
		Div dBotones = new Div();
		dBotones.setWidth("100%");
		
		Button btnBuscar = new Button();
		btnBuscar.setLabel(Labels.getLabel("data3000.buscar"));
		dBotones.appendChild(btnBuscar);
		vboxBuscarArchivo.appendChild(dBotones);
		dBotones.setStyle("text-align: center;");
		
		btnBuscar.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				alert("en desarrollo !!!");
				
			}
		});
		
		
		grpBuscarArchivo.appendChild(vboxBuscarArchivo);
		winBndBuscarArchivo.appendChild(grpBuscarArchivo);
	
		
		
		bndBuscarArchivo.appendChild(winBndBuscarArchivo);
		
		
		Borderlayout contenidoDatos = (Borderlayout) winTablaDatos.getFirstChild();
		North norte = contenidoDatos.getNorth();
		Div divNorte = (Div) norte.getFirstChild();
		Hbox hboxNorte = (Hbox) divNorte.getFirstChild();
		hboxNorte.setStyle("vertical-align: middle;");
		
		
		hboxNorte.getChildren().add(0, bndBuscarArchivo);
		
		
		
		List<FormularioHijo> hijos = formulario.getHijos();
		if(hijos != null && ! hijos.isEmpty()){
			for(FormularioHijo hijo : hijos){
				if(hijo.getTipo().equals(ConstantesAdmin.HIJO_BOTON)){
					
					crearMenuItem(hijo.getHijo());
					
				}
			}
		}
		
		
		/**
		 * ===================== Entidad ===================
		 */
		
		// crear entidad
		String nombreFormularioCrearEntidad = (String) iteCrearE.getValue();
		final Formulario frmCrearEntidad = getFormulario(nombreFormularioCrearEntidad);
		if(frmCrearEntidad == null){
			iteCrearE.setDisabled(true);
			iteCrearE.setVisible(false);
//			menuEntidad.setVisible(false);
		} else {
			
			menuEntidad.setImage(frmCrearEntidad.getUrlIcono());
			menuEntidad.setTooltiptext("Entidad");
			iteCrearE.setTooltip(frmCrearEntidad.getTooltip());
			iteCrearE.setLabel("Crear");
			iteCrearE.setImage(frmCrearEntidad.getUrlIcono());
			
			iteCrearE.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
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
		
		// Editar Entidad
		String nombreFormularioEditar = (String) iteEditarE.getValue();
		final Formulario frmEditarEntidad = getFormulario(nombreFormularioEditar);
		if(frmEditarEntidad == null){
			iteEditarE.setDisabled(true);
			iteEditarE.setVisible(false);
		} else {
			iteEditarE.setImage(frmEditarEntidad.getUrlIcono());
			iteEditarE.setImage(frmEditarEntidad.getUrlIcono());
			iteEditarE.setTooltip(frmEditarEntidad.getTooltip());
			iteEditarE.setLabel("Editar");
			iteEditarE.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									
									
									
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){
										
										DocSistArch directorioTmp = li.getValue();
										
										DocSistArch directorio = sistemaArchivoNgc.getDirectorio(directorioTmp.getSistArchIdn());
										
										Treerow fila = (Treerow) li.getFirstChild();
										Treecell celda = (Treecell) fila.getFirstChild();
										li.setValue(directorio);
										celda.setLabel(directorio.getSistArchNombre());
										celda.setTooltiptext(directorio.getSistArchDescripcion());
									}
									
									
									onSelect$trFileSystem(arg0);
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEditarEntidad, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		String nombreFormularioEliminarEntidad = (String) iteEliminarE.getValue();
		final Formulario frmEliminarEntidad = getFormulario(nombreFormularioEliminarEntidad);
		if(frmEliminarEntidad == null){
			iteEliminarE.setDisabled(true);
			iteEliminarE.setVisible(false);
		} else {
			
			iteEliminarE.setImage(frmEliminarEntidad.getUrlIcono());
			iteEliminarE.setTooltip(frmEliminarEntidad.getTooltip());
			iteEliminarE.setLabel("Eliminar");
			iteEliminarE.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){										
										Component padre = li.getParent();										
										padre.removeChild(li);
									}
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEliminarEntidad, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		/**
		 * =======================================================================================
		 */
		
		/**
		 * ============================Dependencia==================================
		 */
		
		// funcionalidad para crear dependencia
		String nombreFormularioCrearDependencia = (String) iteCrearD.getValue();
		final Formulario frmCrearDependencia = getFormulario(nombreFormularioCrearDependencia);
		if(frmCrearEntidad == null){
			iteCrearD.setDisabled(true);
			iteCrearD.setVisible(false);
//			menuDependencia.setVisible(false);
		} else {
			
			menuDependencia.setImage(frmCrearDependencia.getUrlIcono());
			menuDependencia.setTooltiptext("Dependencia");
			iteCrearD.setTooltip(frmCrearDependencia.getTooltip());
			iteCrearD.setLabel("Crear");
			iteCrearD.setImage(frmCrearDependencia.getUrlIcono());
			
			iteCrearD.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
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
		
		// formulario para editar dependencia
		String nombreFormularioEditarD = (String) iteEditarD.getValue();
		final Formulario frmEditarDependencia = getFormulario(nombreFormularioEditarD);
		if(frmEditarDependencia == null){
			iteEditarD.setDisabled(true);
			iteEditarD.setVisible(false);
		} else {
			
			iteEditarD.setImage(frmEditarDependencia.getUrlIcono());
			iteEditarD.setTooltip(frmEditarDependencia.getTooltip());
			iteEditarD.setLabel("Editar");
			iteEditarD.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									
									
									
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){
										
										DocSistArch directorioTmp = li.getValue();
										
										DocSistArch directorio = sistemaArchivoNgc.getDirectorio(directorioTmp.getSistArchIdn());
										
										Treerow fila = (Treerow) li.getFirstChild();
										Treecell celda = (Treecell) fila.getFirstChild();
										li.setValue(directorio);
										celda.setLabel(directorio.getSistArchNombre());
										celda.setTooltiptext(directorio.getSistArchDescripcion());
									}
									
									
									onSelect$trFileSystem(arg0);
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEditarDependencia, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		//eliminar dependencia
		String nombreFormularioEliminarDependencia = (String) iteEliminarD.getValue();
		final Formulario frmEliminarDependencia = getFormulario(nombreFormularioEliminarDependencia);
		if(frmEliminarDependencia == null){
			iteEliminarD.setDisabled(true);
			iteEliminarD.setVisible(false);
		} else {
			
			iteEliminarD.setImage(frmEliminarDependencia.getUrlIcono());
			iteEliminarD.setTooltip(frmEliminarDependencia.getTooltip());
			iteEliminarD.setLabel("Eliminar");
			iteEliminarD.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){										
										Component padre = li.getParent();										
										padre.removeChild(li);
									}
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEliminarDependencia, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		
		/**
		 * =================================================================================================
		 */
		
		/**
		 * ================================Directorio===========================
		 */
		
		// funcionalidad para crear directorio
		String nombreFormulario = (String) iteCrearDi.getValue();
		final Formulario frmNuevoDirectorio = getFormulario(nombreFormulario);
		if(frmNuevoDirectorio == null){
			iteCrearDi.setDisabled(true);
			iteCrearDi.setVisible(false);
		} else {
			
			menuDirectorio.setImage(frmNuevoDirectorio.getUrlIcono());
			menuDirectorio.setTooltiptext("Directorio");
			iteCrearDi.setTooltip(frmNuevoDirectorio.getTooltip());
			iteCrearDi.setLabel("Crear");
			iteCrearDi.setImage(frmNuevoDirectorio.getUrlIcono());
			iteCrearDi.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

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
		
		String nombreFormularioEditarDi = (String) iteEditarDi.getValue();
		final Formulario frmEditarDirectorio = getFormulario(nombreFormularioEditarDi);
		if(frmEditarDirectorio == null){
			iteEditarDi.setDisabled(true);
			iteEditarDi.setVisible(false);
		} else {
			
			iteEditarDi.setImage(frmEditarDirectorio.getUrlIcono());
			iteEditarDi.setLabel("Editar");
			
			iteEditarDi.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									
									
									
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){
										
										DocSistArch directorioTmp = li.getValue();
										
										DocSistArch directorio = sistemaArchivoNgc.getDirectorio(directorioTmp.getSistArchIdn());
										
										Treerow fila = (Treerow) li.getFirstChild();
										Treecell celda = (Treecell) fila.getFirstChild();
										li.setValue(directorio);
										celda.setLabel(directorio.getSistArchNombre());
										celda.setTooltiptext(directorio.getSistArchDescripcion());
									}
									
									
									onSelect$trFileSystem(arg0);
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEditarDirectorio, directorioPadre, eventoCerrar);
					
					
				}
				
			});
			
		}
		
		String nombreFormularioEliminarDirectorio = (String) iteEliminarDi.getValue();
		final Formulario frmEliminarDirectorio = getFormulario(nombreFormularioEliminarDirectorio);
		if(frmEliminarDirectorio == null){
			iteEliminarDi.setDisabled(true);
			iteEliminarDi.setVisible(false);
		} else {
			
			iteEliminarDi.setImage(frmEliminarDirectorio.getUrlIcono());
			iteEliminarDi.setTooltip(frmEliminarDirectorio.getTooltip());
			iteEliminarDi.setLabel("Eliminar");
			iteEliminarDi.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					 EventListener<Event> eventoCerrar = new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								String res = (String) arg0.getData();
								if(res != null && res.equals(ConstantesAdmin.EXITO)){
									Treeitem li = trFileSystem.getSelectedItem();
									if(li != null){										
										Component padre = li.getParent();										
										padre.removeChild(li);
									}
								}
								
							}
						};
					
					
					Treeitem itemSeleccionado = trFileSystem.getSelectedItem();
					
					DocSistArch directorioPadre = (DocSistArch) (itemSeleccionado != null ? itemSeleccionado.getValue() : null);
					
					
					abrirFormulario(frmEliminarDirectorio, directorioPadre, eventoCerrar);
					
				}
				
			});
			
		}
		
		/**
		 * =================================================================================================
		 */
		
		
		

		
		cargarArbol();
		
		buscarDirectorio.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				trFileSystem.setSelectedItem(null);
				buscar = (String) buscarDirectorio.getValue();
				aplicarFiltro(arg0);
				
			}
		});
		
		buscarDirectorio.addEventListener(Events.ON_OPEN, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				trFileSystem.setSelectedItem(null);
				OpenEvent oevt = (OpenEvent) arg0;				
				buscar = (String) oevt.getValue();
				aplicarFiltro(arg0);
				
			}
		});
		
		
	}
	
	
	private void aplicarFiltro(Event arg0) throws Exception{
		
		if(buscar != null && buscar.length() == 0){
			buscar = null;
		}
		
		onSelect$trFileSystem(arg0);
		
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
		
		List<DocSistArch> lista = sistemaArchivoNgc.getHijos(null, (PltUsuario) usuario, buscar);
		
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
		cargarArbol(padre, arbolPadre, null);
	}
	
	private void cargarArbol(DocSistArch padre, Treechildren arbolPadre, String buscar) throws Exception{
		if(logger.isDebugEnabled()) logger.debug(new StringBuilder("Cargando hijos para ...").append(padre != null ? padre.getSistArchNombre() : "Raiz").toString());
		
		List<DocSistArch> listaHijos = sistemaArchivoNgc.getHijos(padre, (PltUsuario) usuario, buscar);
		
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
			if(dir.item != null && hijo != null){
			hijo.removeChild(dir.item);
		}
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
			cargarArbol(dir, hijos,buscar);
			}
		} else {
			Treechildren hijos = trFileSystem.getTreechildren();
			cargarArbol(null, hijos,buscar);
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
