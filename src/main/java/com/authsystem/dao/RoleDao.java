package com.authsystem.dao;

import com.authsystem.entity.Role;
import com.authsystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RoleDao {

    public Role save(Role role) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(role);
            tx.commit();
            return role;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Role findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Role.class, id);
        }
    }

    public Role findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Role> q = session.createQuery("from Role where name=:n", Role.class);
            q.setParameter("n", name);
            return q.uniqueResult();
        }
    }

    public List<Role> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Role", Role.class).list();
        }
    }
}
