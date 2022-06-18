package com.molinari.hibernate.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;

import com.molinari.hibernate.cfg.DataAccessLayerException;
import com.molinari.hibernate.cfg.HibernateFactory;

public abstract class AbstractDao {
	private Session session;
	private Transaction tx;
	private Criteria criteria;

	public AbstractDao() {
		HibernateFactory.buildIfNeeded();
	}
	
	public void executeOnTransaction(TransactionalTask task) {
		try {
			startOperation();
			task.execute(session);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
	}

	public void saveOrUpdate(final Object obj) {
		try {
			startOperation();
			session.saveOrUpdate(obj);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
	}

	protected void delete(final Object obj) {
		try {
			startOperation();
			session.delete(obj);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
	}

	protected Object find(final Class<?> clazz, final Long id) {
		Object obj = null;
		try {
			startOperation();
			obj = session.get(clazz, id);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
		return obj;
	}
	
	public Object findByProp(Class<?> clazz, String propName, Object propValue) {
		Object obj = null;
		try {
			startOperation();
			addCriteria(clazz, propName, propValue);
			try{
				return criteria.uniqueResult();
			}catch(Exception e){
				return criteria.list();
			}
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
		return obj;

	}
	
	public Object find(final Class<?> clazz, final Object example) {
		Object obj = null;
		try {
			startOperation();
			Example autoreExample = Example.create(example);
			Criteria criteria = session.createCriteria(clazz);
			criteria.add(autoreExample);
			try{
				return criteria.uniqueResult();
			}catch(Exception e){
				return criteria.list();
			}
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
		return obj;
	}

	public <T> List<T> findAll(final Class<T> clazz) {
		List<T> objects = null;
		try {
			startOperation();
			Query query = session.createQuery("from " + clazz.getName());
			objects = query.list();
			tx.commit();
		} catch (HibernateException e) {
			handleException(e);
		} finally {
			HibernateFactory.close(session);
		}
		return objects;
	}
	
	protected Criteria addCriteria(final Class<?> clazz, String propertyName, Object value) {
		if(criteria == null){
			criteria = session.createCriteria(clazz);
		}
		criteria.add(Expression.eq(propertyName, value));
		return criteria;
	}

	protected void handleException(final HibernateException e) throws DataAccessLayerException {
		HibernateFactory.rollback(tx);
		throw new DataAccessLayerException(e);
	}

	protected void startOperation() throws HibernateException {
		session = HibernateFactory.openSession();
		tx = session.beginTransaction();
	}

	protected Session getSession() {
		return session;
	}

	protected Transaction getTx() {
		return tx;
	}
}