package edu.gestionacademica.config;

import edu.gestionacademica.model.Estudiante;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = construirSessionFactory();

    private HibernateUtil() { }

    private static SessionFactory construirSessionFactory() {
        Properties propiedades = new Properties();
        try (InputStream entrada = HibernateUtil.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {
            if (entrada == null) {
                throw new IllegalStateException("Falta configuración");
            }
            propiedades.load(entrada);
            return new Configuration().addProperties(propiedades)
                    .addAnnotatedClass(Estudiante.class).buildSessionFactory();
        } catch (IOException | RuntimeException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
