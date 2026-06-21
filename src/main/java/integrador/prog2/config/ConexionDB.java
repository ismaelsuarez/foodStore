package integrador.prog2.config;

import integrador.prog2.exception.ErrorBaseDatos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {

    private static final String RUTA_CONFIG = "META-INF/persistence.xml";
    private static DatosConexion datosConexion;

    private ConexionDB() {
    }

    public static Connection getConexion() {
        DatosConexion datos = obtenerDatosConexion();
        try {
            Class.forName(datos.driver);
            return DriverManager.getConnection(
                    datos.url,
                    datos.usuario,
                    datos.password
            );
        } catch (ClassNotFoundException e) {
            throw new ErrorBaseDatos("No se encontro el driver JDBC configurado.", e);
        } catch (SQLException e) {
            throw new ErrorBaseDatos("No se pudo conectar a la base de datos.", e);
        }
    }

    private static synchronized DatosConexion obtenerDatosConexion() {
        if (datosConexion == null) {
            datosConexion = leerPersistenceXml();
        }
        return datosConexion;
    }

    private static DatosConexion leerPersistenceXml() {
        try (InputStream inputStream = ConexionDB.class
                .getClassLoader()
                .getResourceAsStream(RUTA_CONFIG)) {

            if (inputStream == null) {
                throw new ErrorBaseDatos("No se encontro el archivo " + RUTA_CONFIG + ".");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(inputStream);

            String driver = leerPropiedad(document, "jakarta.persistence.jdbc.driver");
            String url = leerPropiedad(document, "jakarta.persistence.jdbc.url");
            String usuario = leerPropiedad(document, "jakarta.persistence.jdbc.user");
            String password = leerPropiedad(document, "jakarta.persistence.jdbc.password");

            return new DatosConexion(driver, url, usuario, password);
        } catch (ErrorBaseDatos e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorBaseDatos("No se pudo leer la configuracion de persistencia.", e);
        }
    }

    private static String leerPropiedad(Document document, String nombre) {
        NodeList propiedades = document.getElementsByTagName("property");

        for (int i = 0; i < propiedades.getLength(); i++) {
            Element propiedad = (Element) propiedades.item(i);
            if (nombre.equals(propiedad.getAttribute("name"))) {
                return propiedad.getAttribute("value");
            }
        }

        throw new ErrorBaseDatos("Falta la propiedad " + nombre + " en " + RUTA_CONFIG + ".");
    }

    private static class DatosConexion {

        private final String driver;
        private final String url;
        private final String usuario;
        private final String password;

        private DatosConexion(String driver, String url, String usuario, String password) {
            this.driver = driver;
            this.url = url;
            this.usuario = usuario;
            this.password = password;
        }
    }
}
