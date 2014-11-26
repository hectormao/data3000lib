package com.data3000.data3000lib.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.data3000.admin.bd.PltUsuario;
import com.data3000.admin.dao.PltDAO;
import com.data3000.data3000lib.bd.DocAcl;
import com.data3000.data3000lib.bd.DocArchivo;
import com.data3000.data3000lib.bd.DocArchivoVersion;
import com.data3000.data3000lib.bd.DocCampArch;
import com.data3000.data3000lib.bd.DocCampTipo;
import com.data3000.data3000lib.bd.DocCampo;
import com.data3000.data3000lib.bd.DocSistArch;
import com.data3000.data3000lib.bd.DocTipoArchivo;

public class SistemaArchivoDAO extends PltDAO{
	
	
	
	public List<DocSistArch> getHijosRaiz() throws Exception{
		
		
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			String hql = "from com.data3000.data3000lib.bd.DocSistArch dir where dir.docSistArch is null order by dir.sistArchNombre";
			
			Query query = sesion.createQuery(hql);
			
			/*Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.isNull("docSistArch"));
			criteria.addOrder(Order.asc("sistArchNombre"));*/
			
			return query.list();
			
		} catch(Exception ex){
			sesion.close();
			throw ex;
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
			
			String hql = "from com.data3000.data3000lib.bd.DocSistArch dir where dir.docSistArch = :padre order by dir.sistArchNombre";
			
			Query query = sesion.createQuery(hql);
			query.setEntity("padre", padre);
			
			return query.list();
			
		} catch(Exception ex){
			sesion.close();
			throw ex;
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
			sesion.close();
			throw ex;
		}
	}


	public List<DocCampTipo> getCamposTipo(DocTipoArchivo docTipoArchivo) throws Exception {
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocCampTipo.class);
			criteria.add(Restrictions.eq("docTipoArchivo", docTipoArchivo));
			criteria.addOrder(Order.asc("campTipoOrden"));
			
			return criteria.list();
			
		} catch(Exception ex){
			sesion.close();
			throw ex;
		}
	}


	public void registrarTipoDocumentos(DocTipoArchivo docTipoArchivo,List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar,List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
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
			
			for(DocCampTipo campoTipo : listaEliminar){
				sesion.delete(campoTipo);
			}
			
			
			for(DocCampTipo campoTipo : listaActualizar){
				sesion.update(campoTipo);
			}
			
			for(DocCampTipo campoTipo : listaCrear){
				sesion.save(campoTipo);
			}
			
			
			
			
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}
		
	}
	public void anularTipoDocumentos(DocTipoArchivo docTipoArchivo) throws Exception {
		super.update(docTipoArchivo);
		
	}

	public void actualizarTipoDocumentos(DocTipoArchivo docTipoArchivo,List<DocCampTipo> listaCrear, List<DocCampTipo> listaActualizar,List<DocCampTipo> listaEliminar, List<DocCampo> listaCrearCampo) throws Exception {
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
			
			for(DocCampTipo campoTipo : listaEliminar){
				sesion.delete(campoTipo);
			}
			
			
			for(DocCampTipo campoTipo : listaActualizar){
				sesion.merge(campoTipo);
			}
			
			for(DocCampTipo campoTipo : listaCrear){
				sesion.save(campoTipo);
			}
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
		}
		
	}


	public List<DocTipoArchivo> getTipos() throws Exception{
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocTipoArchivo.class);
			
			criteria.addOrder(Order.asc("tipoArchNombre"));
			
			return criteria.list();
			
		} catch(Exception ex){
			sesion.close();
			throw ex;
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
			sesion.close();
			throw ex;
		}
		
	}


	public void registrarArchivo(DocArchivo docArchivo, DocArchivoVersion version, List<DocCampArch> listaMeta) throws Exception{
		
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
			
			tx.commit();
		} catch(Exception ex){
			tx.rollback();
			throw ex;
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
		}
		
	}
	

}
