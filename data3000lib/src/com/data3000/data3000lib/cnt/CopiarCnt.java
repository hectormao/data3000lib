package com.data3000.data3000lib.cnt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.utl.ConstantesAdmin;
import com.data3000.admin.utl.WindowComposer;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

public class CopiarCnt extends WindowComposer {

	private Logger logger;

	private Tree trFileSystem;
	private Textbox txtNombre;

	private DocArchivo archivo;
	private DocSistArch directorio;

	private class ItemDir {
		public DocSistArch dir;
		public Treeitem item;
		public Treecell celda;
	}

	/**
	 * beans
	 */
	private SistemaArchivoNgc sistemaArchivoNgc;

	@Override
	public void doAfterCompose(Window winCopiar) throws Exception {
		super.doAfterCompose(winCopiar);
		logger = Logger.getLogger(this.getClass());

		Object objeto = argumentos.get(ConstantesAdmin.ARG_SELECCION);

		if (objeto instanceof DocArchivo) {
			archivo = (DocArchivo) objeto;
			txtNombre.setValue(archivo.getArchNombre());
		} else if (objeto instanceof DocSistArch) {
			directorio = (DocSistArch) objeto;
			txtNombre.setValue(directorio.getSistArchNombre());
		}

		cargarArbol();
	}

	public void onCreate$winCopiar(Event evt) throws Exception {

		if (directorio == null && archivo == null) {
			Events.sendEvent(Events.ON_CLOSE, this.self, null);
			throw new Exception(Labels.getLabel("error.0007"));
		}

	}

	public void onClick$btnAceptar(Event evt) throws Exception{
		
		
		Treeitem tiSeleccion = trFileSystem.getSelectedItem();		
		DocSistArch destino = tiSeleccion != null ? (DocSistArch) tiSeleccion.getValue() : null;
		
		if(destino == null){
			throw new WrongValueException(trFileSystem, Labels.getLabel("error.0007"));
		}
		
		String nuevoNombre = txtNombre.getValue();
		
		if(nuevoNombre == null || (nuevoNombre != null && nuevoNombre.trim().length() <= 0)){
			throw new WrongValueException(txtNombre, Labels.getLabel("error.0002"));
		}
		
		
		
			
		if(archivo != null){
			
			DocArchivo existente = sistemaArchivoNgc.getArchivo(destino, nuevoNombre);
			if(existente != null){
				throw new WrongValueException(txtNombre, Labels.getLabel("error.1006"));
			}
			sistemaArchivoNgc.copiarArchivo(archivo, nuevoNombre, destino);
		} else {
			DocSistArch existente = sistemaArchivoNgc.getHijo(destino,nuevoNombre);
			if(existente != null){
				throw new WrongValueException(txtNombre, Labels.getLabel("error.1006"));
			}
			sistemaArchivoNgc.copiarDirectorio(directorio, nuevoNombre, destino);
		}
			
		
		
		
		
		Events.sendEvent(Events.ON_CLOSE, this.self , null);
	}

	public void onClick$btnCancelar(Event evt) {
		Events.sendEvent(Events.ON_CLOSE, this.self, null);
	}

	private void cargarArbol() throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("Consultando arbol ...");

		Treechildren raiz = new Treechildren();
		trFileSystem.appendChild(raiz);

		Treeitem tiRaiz = new Treeitem();
		tiRaiz.setLabel(Labels.getLabel("data3000.raiz"));
		raiz.appendChild(tiRaiz);

		Treechildren hijosRaiz = new Treechildren();
		tiRaiz.appendChild(hijosRaiz);

		cargarArbol(null, hijosRaiz);
	}

	private void cargarArbol(DocSistArch padre, Treechildren arbolPadre)
			throws Exception {
		if (logger.isDebugEnabled())
			logger.debug(new StringBuilder("Cargando hijos para ...").append(
					padre != null ? padre.getSistArchNombre() : "Raiz")
					.toString());

		List<DocSistArch> listaHijos = sistemaArchivoNgc.getHijos(padre,
				(PltUsuario) usuario,null);

		Map<Long, ItemDir> mapaDir = (Map<Long, ItemDir>) arbolPadre
				.getAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR);
		List<ItemDir> listaDir = (List<ItemDir>) arbolPadre
				.getAttribute(ConstantesData3000.ATRIBUTO_LISTA_DIR);

		if (mapaDir == null) {
			mapaDir = new HashMap<Long, ItemDir>();
			arbolPadre.setAttribute(ConstantesData3000.ATRIBUTO_MAPA_DIR,
					mapaDir);
		}

		Map<Long, ItemDir> mapaDirAux = new HashMap<Long, ItemDir>();
		mapaDirAux.putAll(mapaDir);

		// int idx = 0;

		for (final DocSistArch hijo : listaHijos) {

			ItemDir dirAux = mapaDirAux.remove(hijo.getSistArchIdn());

			if (dirAux == null) {
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

				// arbolPadre.appendChild(item);
				// buscando insertar en arbol de forma ordenada por el nombre
				// del directorio
				boolean inserto = false;
				for (Treeitem itemHijo : arbolPadre.getItems()) {
					DocSistArch hijoComparar = itemHijo.getValue();
					if (logger.isDebugEnabled())
						logger.debug(new StringBuilder("hijoComparar: ")
								.append(hijoComparar.getSistArchNombre())
								.append(" vs. hijo: ")
								.append(hijo.getSistArchNombre())
								.append(" resultado: ")
								.append(hijoComparar.getSistArchNombre()
										.compareTo(hijo.getSistArchNombre()))
								.toString());
					if (hijoComparar.getSistArchNombre().compareTo(
							hijo.getSistArchNombre()) > 0) {
						if (logger.isDebugEnabled())
							logger.debug(new StringBuilder(
									"Insertando antes de: ").append(
									hijoComparar.getSistArchNombre())
									.toString());
						arbolPadre.insertBefore(item, itemHijo);
						inserto = true;
						break;
					}
				}

				if (!inserto) {
					arbolPadre.appendChild(item);
				}

			} else {
				if (!dirAux.dir.getSistArchNombre().equals(
						hijo.getSistArchNombre())) {
					// cambiar nombre
					dirAux.celda.setLabel(hijo.getSistArchNombre());
					dirAux.item.setValue(hijo);
					dirAux.dir = hijo;
				}

				if (!dirAux.dir.getSistArchDescripcion().equals(
						hijo.getSistArchDescripcion())) {
					// cambiar tooltip
					dirAux.celda.setTooltiptext(hijo.getSistArchDescripcion());
					dirAux.item.setValue(hijo);
					dirAux.dir = hijo;
				}
			}
		}

		// Eliminando hijos que han sido borrados de la BD
		for (ItemDir dir : mapaDirAux.values()) {
			Component hijo = dir.item.getParent();
			hijo.removeChild(dir.item);
			mapaDir.remove(dir.dir.getSistArchIdn());
		}

	}

	public void onSelect$trFileSystem(Event event) throws Exception {

		Treeitem tiSeleccion = trFileSystem.getSelectedItem();

		DocSistArch directorio = tiSeleccion != null ? (DocSistArch) tiSeleccion
				.getValue() : null;

		if (directorio == null) {
			directorio = new DocSistArch();
			directorio.setSistArchIdn(0L);
		}

		if (tiSeleccion != null) {

			Treechildren hijos = tiSeleccion.getTreechildren();
			if (hijos == null) {
				hijos = new Treechildren();
				tiSeleccion.appendChild(hijos);
			}
			DocSistArch dir = tiSeleccion.getValue();
			cargarArbol(dir, hijos);

		} else {
			Treechildren hijos = trFileSystem.getTreechildren();
			cargarArbol(null, hijos);
		}

	}

	public SistemaArchivoNgc getSistemaArchivoNgc() {
		return sistemaArchivoNgc;
	}

	public void setSistemaArchivoNgc(SistemaArchivoNgc sistemaArchivoNgc) {
		this.sistemaArchivoNgc = sistemaArchivoNgc;
	}

}
