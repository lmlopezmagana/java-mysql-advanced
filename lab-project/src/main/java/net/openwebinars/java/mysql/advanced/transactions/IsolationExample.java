package net.openwebinars.java.mysql.advanced.transactions;

import net.openwebinars.java.mysql.advanced.pool.MyDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class IsolationExample {

    public void test() {

        try (Connection conn = MyDataSource.getConnection()) {

            // Este nivel viene establecido por el driver JDBC de Mysql
            // Se trata de un valor hardcodeado en el código fuente del driver.
            int defaultIsolationLevel = conn.getMetaData().getDefaultTransactionIsolation();
            printIsolationLevel(defaultIsolationLevel);

            // Este nivel viene establecido por la configuración de la instancia
            // de Mysql que tenemos instalado. En este caso, no coincide con
            // el anterior.

            // Más información en https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html

            int isolationLevel = conn.getTransactionIsolation();
            printIsolationLevel(isolationLevel);


            // Cambio del nivel de aislamiento
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            isolationLevel = conn.getTransactionIsolation();
            printIsolationLevel(isolationLevel);




        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }

    public void printIsolationLevel(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE -> System.out.println("Sin soporte para transacciones");
            case Connection.TRANSACTION_READ_UNCOMMITTED -> System.out.println("Lecturas no confirmadas");
            case Connection.TRANSACTION_READ_COMMITTED -> System.out.println("Lecturas confirmadas (previene lecturas sucias)");
            case Connection.TRANSACTION_REPEATABLE_READ -> System.out.println("Lecturas repetibles (previene lecturas sucias y no repetibles)");
            case Connection.TRANSACTION_SERIALIZABLE -> System.out.println("Serializable (previene todas las anomalías)");
        }
    }

}
