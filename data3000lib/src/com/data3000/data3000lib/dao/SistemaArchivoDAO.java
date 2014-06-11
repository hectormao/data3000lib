package com.data3000.data3000lib.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.data3000.admin.dao.PltDAO;
import com.data3000.data3000lib.bd.DocSistArch;

public class SistemaArchivoDAO extends PltDAO{
	
	
	
	public List<DocSistArch> getHijosRaiz() throws Exception{
		
		
		
		Session sesion = super.getSessionFactory().getCurrentSession();
		
		Transaction tx = sesion.getTransaction();
		try{
			if(! tx.isActive()){
				tx.begin();
			}
			
			Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.isNull("docSistArch"));
			criteria.addOrder(Order.asc("sistArchNombre"));
			
			return criteria.list();
			
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
			
			Criteria criteria = sesion.createCriteria(DocSistArch.class);
			criteria.add(Restrictions.eq("docSistArch",padre));
			criteria.addOrder(Order.asc("sistArchNombre"));
			
			return criteria.list();
			
		} catch(Exception ex){
			sesion.close();
			throw ex;
		}
		
	}
	

}
