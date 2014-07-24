package com.data3000.data3000lib.cnt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zhtml.Tbody;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.data3000.admin.cmp.TablaDatos;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class EscritorioCnt extends WindowComposer {
	
	private Tree trFileSystem;
	
	private Logger logger;
	
	
	private SistemaArchivoNgc sistemaArchivoNgc;
	
	private Div divTrabajo;
	
	
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
		parametros.put(ConstantesAdmin.ARG_CAMPOS_TABLA, DocArchivo.getCamposTabla());
		
		winTablaDatos = (Window) Executions.createComponentsDirectly(zulReader,"zul",divTrabajo,parametros) ;
		winTablaDatos.setTitle(null);
		winTablaDatos.setBorder("none");
		winTablaDatos.doEmbedded();
		
		cargarArbol();
	}
	
	private void cargarArbol() throws Exception {
		if(logger.isDebugEnabled()) logger.debug("Consultando arbol ...");
		
		Treechildren hijosRaiz = new Treechildren();		
		trFileSystem.appendChild(hijosRaiz);
		cargarArbol(null, hijosRaiz);
	}
	
	private void cargarArbol(DocSistArch padre, Treechildren arbolPadre) throws Exception{
		if(logger.isDebugEnabled()) logger.debug(new StringBuilder("Cargando hijos para ...").append(padre != null ? padre.getSistArchNombre() : "Raiz").toString());
		
		List<DocSistArch> listaHijos = sistemaArchivoNgc.getHijos(padre);
		
		Map<Long,ItemDir> mapaDir = (Map<Long, ItemDir>) arbolPadre.getAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR);
		List<ItemDir> listaDir = (List<ItemDir>) arbolPadre.getAttribute(ConstantesData3000.ATRIBUTO_LISTA_DIR);
		
		
		if(mapaDir == null){
			mapaDir = new HashMap<Long, ItemDir>();
			arbolPadre.setAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR,mapaDir);
		}
		
		Map<Long,ItemDir> mapaDirAux = new HashMap<Long, ItemDir>();
		mapaDirAux.putAll(mapaDir);
		
		//int idx = 0;
		
		for(DocSistArch hijo : listaHijos){
			
			
			ItemDir dirAux = mapaDirAux.remove(hijo.getSistArchIdn());
			
			if(dirAux == null){	
				Treeitem item = new Treeitem();
				item.setValue(hijo);
				Treerow fila = new Treerow();
				Treecell celda = new Treecell(hijo.getSistArchNombre());
				celda.setTooltiptext(hijo.getSistArchDescripcion());
				fila.appendChild(celda);
				item.appendChild(fila);
				
				
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
					if(hijoComparar.getSistArchNombre().compareTo(hijo.getSistArchNombre()) > 0){
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
		
		Treeitem seleccion = trFileSystem.getSelectedItem();
		
		actualizarTablaDatos((DocSistArch) seleccion.getValue());
		
		if(seleccion != null){
			
			Treechildren hijos = seleccion.getTreechildren();
			if(hijos == null){
				hijos = new Treechildren();
				seleccion.appendChild(hijos);
			}			
			DocSistArch dir = seleccion.getValue();			
			cargarArbol(dir, hijos);
						
			
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
