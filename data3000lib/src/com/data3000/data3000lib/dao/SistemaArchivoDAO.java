package com.data3000.data3000lib.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.data3000.admin.dao.PltDAO;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSerieDoc;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoAlma;

public class SistemaArchivoDAO extends PltDAO{
	
	
	
	public List<DocSistArch> getHijosRaiz() throws Exception{
		
		
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			String hql = "from com.data3000.data3000lib.bd.DocSistArch dir where dir.audiSiAnul = false and dir.docSistArch is null order by dir.sistArchNombre";
			
			Query query = sesion.createQuery(hql);
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.isNull("docSistArch"));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			return query.list();
			
		} catch(Exception ex){			
			throw ex;
		} finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}
	
	
	public List<DocSistArch> getHijos(DocSistArch padre) throws Exception{
		
		
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.eq("docSistArch",padre));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			String hql = "from com.data3000.data3000lib.bd.DocSistArch dir where dir.audiSiAnul = false and dir.docSistArch = :padre order by dir.sistArchNombre";
			
			Query query = sesion.createQuery(hql);
			query.setEntity("padre", padre);
			
			return query.list();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public List<DocCampo> getCampos() throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocCampo.class);
			criteria.addOrder(Order.asc("campoNombre"));
			
			return criteria.list();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public List<DocCampTipo> getCamposTipo(DocSerieDoc docTipoArchivo) throws Exception {
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocCampTipo.class);
			criteria.add(Restrictions.eq("docSerieDoc", docTipoArchivo));
			criteria.addOrder(Order.asc("campTipoOrden"));
			
			return criteria.list();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public void registrarTipoDocumentos(DocSerieDoc docTipoArchivo,List<Object> listaCrear, List<Object> listaActualizar,List<Object> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			for(DocCampo campo : listaCrearCampo){
				sesion.save(campo);
			}
			
			
			sesion.save(docTipoArchivo);
			
			for(Object campoTipo : listaEliminar){
				sesion.delete(campoTipo);
			}
			
			
			for(Object campoTipo : listaActualizar){
				sesion.update(campoTipo);
			}
			
			for(Object campoTipo : listaCrear){
				sesion.save(campoTipo);
			}
			
			
			
			
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}
	public void anularTipoDocumentos(DocSerieDoc docTipoArchivo) throws Exception {
		super.update(docTipoArchivo);
		
	}

	public void actualizarTipoDocumentos(DocSerieDoc docTipoArchivo,List<Object> listaCrear, List<Object> listaActualizar,List<Object> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			for(DocCampo campo : listaCrearCampo){
				sesion.save(campo);
			}
			
			
			sesion.merge(docTipoArchivo);
			
			for(Object campoTipo : listaEliminar){
				sesion.delete(campoTipo);
			}
			
			
			for(Object campoTipo : listaActualizar){
				sesion.merge(campoTipo);
			}
			
			for(Object campoTipo : listaCrear){
				sesion.save(campoTipo);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public List<DocSerieDoc> getTipos() throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocSerieDoc.class);
			
			criteria.addOrder(Order.asc("tipoArchNombre"));
			
			return criteria.list();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public DocArchivo getArchivo(DocSistArch directorio, String nombreArchivo) throws Exception {
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocArchivo.class);
			criteria.add(Restrictions.and(Restrictions.eq("docSistArch", directorio),Restrictions.eq("archNombre", nombreArchivo)));
			
			return (DocArchivo) criteria.uniqueResult();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public void registrarArchivo(DocArchivo docArchivo, DocArchivoVersion version, List<DocCampArch> listaMeta, List<DocAcl> permisos) throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			if(docArchivo.getArchIdn() == 0){
				sesion.save(docArchivo);
			}
			
			sesion.save(version);
			for(DocCampArch meta : listaMeta){
				sesion.save(meta);
			}
			
			for(DocAcl acl : permisos){
				sesion.save(acl);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public void registrarDirectorio(DocSistArch directorio, List<DocAcl> permisos) throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			sesion.save(directorio);
			
			for(DocAcl acl : permisos){
				sesion.save(acl);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public void modificarDirectorio(DocSistArch directorio, List<DocAcl> permisosNuevos, List<DocAcl> permisosEdicion, List<DocAcl> permisosEliminacion) throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();		
		Transaction tx = sesion.getTransaction();
		try{
			
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			sesion.update(directorio);
			
			
			for(DocAcl acl : permisosNuevos){
				sesion.save(acl);
			}
			
			for(DocAcl acl : permisosEdicion){				
				
				sesion.update(acl);				
			}
			
			for(DocAcl acl : permisosEliminacion){
				
				sesion.delete(acl);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		} finally {
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public DocSistArch getDirectorio(long sistArchIdn) throws Exception {
		
		Session sesion = super.getSessionFactory().getCurrentSession();		
		Transaction tx = sesion.getTransaction();
		try{
			
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			return (DocSistArch) sesion.get(DocSistArch.class, sistArchIdn);
		} catch(Exception ex){
			
			throw ex;
		} finally {
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
		
	}


	public void anularDirectorio(DocSistArch directorio) throws Exception{
		super.update(directorio);
		
	}


	public List<DocArchivoVersion> getVersionesArchivo(DocArchivo archivo) throws Exception {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocArchivoVersion.class);
			criteria.add(Restrictions.eq("docArchivo", archivo));
			
			return criteria.list();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public List<DocCampArch> getCamposVersion(DocArchivoVersion version) throws Exception {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocCampArch.class);
			criteria.add(Restrictions.eq("docArchivoVersion", version));
			
			return criteria.list();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public DocSistArch getDirectorio(DocSistArch destino, String nombre) throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.and(
					Restrictions.eq("docSistArch", destino),
					Restrictions.eq("sistArchNombre", nombre)
					));
			
			return (DocSistArch) criteria.uniqueResult();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public void copiarArchivo(DocArchivo archivo, List<DocArchivoVersion> listaVersiones, List<DocCampArch> listaCampos, List<DocAcl> listaPermisos) throws Exception{ 
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			sesion.save(archivo);
			for(DocArchivoVersion version : listaVersiones){
				sesion.save(version);
			}
			
			for(DocCampArch campo : listaCampos){
				sesion.save(campo);
			}
			
			for(DocAcl permiso : listaPermisos){
				sesion.save(permiso);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
		
	}


	public List<DocAcl> getPermisosArchivo(DocArchivo archivo) throws Exception {
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocAcl.class);
			criteria.add(Restrictions.eq("docArchivo", archivo));
			
			return criteria.list();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public DocArchivoVersion getVersionArchivo(Long idVersion) throws Exception {
		 
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocArchivoVersion.class);
			criteria.add(Restrictions.eq("archVersIdn", idVersion));
			
			return (DocArchivoVersion) criteria.uniqueResult();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public DocSerieDoc getSerieDoc(long idSerie) {
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocSerieDoc.class);
			criteria.add(Restrictions.eq("serieDocIdn", idSerie));
			
			return (DocSerieDoc) criteria.uniqueResult();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public List<DocSerieDoc> getHijosRaizSerie() {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			String hql = "from " + DocSerieDoc.class.getName() + " ser where ser.audiSiAnul = false and ser.serieDocPadre is null order by ser.serieDocNombre";
			
			Query query = sesion.createQuery(hql);
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.isNull("docSistArch"));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			return query.list();
			
		} catch(Exception ex){			
			throw ex;
		} finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}
	
	public List<DocSerieDoc> getHijosSerie() {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			
			String hql = "from " + DocSerieDoc.class.getName() + " ser where ser.audiSiAnul = false and ser.serieDocPadre is null order by ser.serieDocNombre";
			
			Query query = sesion.createQuery(hql);
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.isNull("docSistArch"));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			return query.list();
			
		} catch(Exception ex){			
			throw ex;
		} finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}
	
	public List<DocSerieDoc> getHijosSerie(DocSerieDoc padre) {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.eq("docSistArch",padre));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			String hql = "from " + DocSerieDoc.class.getName() + " ser where ser.audiSiAnul = false and ser.serieDocPadre = :padre order by ser.serieDocNombre";
			
			Query query = sesion.createQuery(hql);
			query.setEntity("padre", padre);
			
			return query.list();
			
		} catch(Exception ex){
			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	public List<DocTipoAlma> getTiposAlmacenamientoSerie(DocSerieDoc docSerieDoc) {
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocTipoAlma.class);
			criteria.add(Restrictions.eq("docSerieDoc", docSerieDoc));
			criteria.addOrder(Order.asc("tipoAlmaIdn"));
			
			return criteria.list();
			
		} catch(Exception ex){			
			throw ex;
		}finally{
			if(sesion.isOpen()){
				sesion.close();
			}
		}
	}


	

}
