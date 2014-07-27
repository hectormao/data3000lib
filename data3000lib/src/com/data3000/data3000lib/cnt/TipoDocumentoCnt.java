package com.data3000.data3000lib.cnt;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.data3000.admin.utl.WindowComposer;

public class TipoDocumentoCnt extends WindowComposer {
	
	
	private Textbox txtNombre;
	private Textbox txtDescripcion;
	private Grid grdMetaDatos;
	
	@Override
	public void doAfterCompose(Window winEscritorio) throws Exception{		
		super.doAfterCompose(winEscritorio);
	}
	
	public void onClick$btnAceptar(Event evt) throws Exception{
		alert("Aceptar");
	}
	
	public void onClick$btnCencelar(Event evt) throws Exception{
		Events.sendEvent(new Event(Events.ON_CLOSE,this.self,null));
	}

}
