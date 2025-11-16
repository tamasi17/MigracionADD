package main.java.utils;

import main.java.models.Cliente;
import main.java.models.EstadoMigracion;
import main.java.models.ResultadoMigracion;
import main.java.v1.DaoClienteV1;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

public class JdbcMigrator {

    public void migrarDB() {

        try (Connection conOriginal = ConnectionFactory.getConnectionDmOriginal();
             Connection conMigra = ConnectionFactory.getConnectionDmMigrada()) {

            // Transaccion atomica en prac2migra
           conMigra.setAutoCommit(false);

           int totalErrores=0;

            Savepoint spClientes = conMigra.setSavepoint("spClientes");

            ResultadoMigracion migracionClientes = migrarClientes(conOriginal, conMigra);

            if (migracionClientes.getEstadoMigracion() != EstadoMigracion.COMPLETA){
                conMigra.rollback(spClientes); // Rollback si no ha migrado bien clientes
                getLogger().warn("Error al migrar: rollback "+
                        migracionClientes.getTablaMigrada() +" a "+ spClientes.getSavepointName());
            }

            totalErrores += migracionClientes.getErrores();

            // migrarPedidos();
            // Savepoint?
            // migrarProductos();

            // HAZ QUE DEVUELVA UN MIGRATIONRESULT



            conMigra.setAutoCommit(true);

        } catch (SQLException sqle) {
            getLogger().error("Fallo al migrar base de datos");
        }


    }

    private ResultadoMigracion migrarClientes(Connection conOriginal, Connection conMigra) {

        ResultadoMigracion resultadoMigracion = new ResultadoMigracion(
                "clientes", 0,
                0, 0,
                0, EstadoMigracion.PARCIAL
        );

        try{

            long tiempo = System.currentTimeMillis();
            int errores = 0;

            DaoClienteV1 daoCliente = new DaoClienteV1();

            // Recibimos datos de clientes original
            List<Cliente> clientesOriginal = daoCliente.findAll();
            int recibidos = clientesOriginal.size();

            // Creamos y cargamos la base de datos migrada
            DbSetup.crearTablaClientesMigrada();

            // Solo suma un error si varios fallan en el batch, version con errores dentro de DataLoader?
            try {
                DataLoader.cargarClientesMigrada(daoCliente, clientesOriginal, conMigra);
            } catch (SQLException sqle) {
                errores++;
            }

            int insertados = daoCliente.findAllMigra().size();
            tiempo = (System.currentTimeMillis() - tiempo);


            resultadoMigracion.setErrores(errores);
            resultadoMigracion.setRecibidos(recibidos);
            resultadoMigracion.setInsertados(insertados);
            resultadoMigracion.setTiempoTranscurrido(tiempo);

            if (errores == 0) {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.COMPLETA);
            } else {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.PARCIAL);
            }


            getLogger().info(resultadoMigracion.toString());

        } catch (Exception e) {
            resultadoMigracion.setEstadoMigracion(EstadoMigracion.FALLIDA);
            getLogger().error("Problema al conectarse a las bases de datos durante la migracion");
        }

        return resultadoMigracion;
    }

}
