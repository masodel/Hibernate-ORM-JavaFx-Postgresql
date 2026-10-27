package edu.gestionacademica.repository;

import edu.gestionacademica.config.HibernateUtil;
import edu.gestionacademica.model.Estudiante;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EstudianteRepository {
    public void guardar(Estudiante estudiante) {
        Transaction transaccion = null;
        try (Session sesion = HibernateUtil.getSessionFactory().openSession()) {
            transaccion = sesion.beginTransaction();
            sesion.persist(estudiante);
            transaccion.commit();
        } catch (RuntimeException error) {
            if (transaccion != null && transaccion.isActive()) {
                transaccion.rollback();
            }
            throw error;
        }
    }

    public List<Estudiante> listar() {
        try (Session sesion = HibernateUtil.getSessionFactory().openSession()) {
            return sesion.createQuery("FROM Estudiante", Estudiante.class).getResultList();
        }
    }

    public List<Estudiante> listarPorCarrera(String carrera) {
        try (Session sesion = HibernateUtil.getSessionFactory().openSession()) {
            return sesion.createQuery("FROM Estudiante e WHERE e.carrera = :carrera", Estudiante.class)
                    .setParameter("carrera", carrera).getResultList();
        }
    }
}
