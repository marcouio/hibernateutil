package com.molinari.hibernate.dao;

import org.hibernate.Session;

public interface TransactionalTask {

	public void execute(Session session);
}
