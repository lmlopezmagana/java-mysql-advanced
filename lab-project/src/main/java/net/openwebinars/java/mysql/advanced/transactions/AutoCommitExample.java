package net.openwebinars.java.mysql.advanced.transactions;

import net.openwebinars.java.mysql.advanced.pool.MyDataSource;

import javax.xml.transform.Result;
import java.sql.*;

public class AutoCommitExample {

    public void test() {

        try (Connection conn = MyDataSource.getConnection()) {

            String sql1 = "SELECT nombre FROM empleado";

            try (Statement stm = conn.createStatement();
                 ResultSet rs = stm.executeQuery(sql1)) {

                while (rs.next()) {
                    System.out.println("Nombre: " + rs.getString(1));
                }

            }


            conn.setAutoCommit(false);

            String sql2 = "INSERT INTO lenguaje (nombre) VALUES (?)";
            String sql3 = "SELECT nombre FROM lenguaje";

            try (PreparedStatement pstm = conn.prepareStatement(sql2)) {

                pstm.setString(1,"LOLCODE");

                pstm.executeUpdate();

                conn.commit();

                // Con esta línea comentada, la inserción no se confirma realmente
                // En apariencia, cuando hagamos la siguiente consulta, nos aparecerá
                // el valor recién insertado, pero si consultamos con phpMyAdmin, podremos
                // ver que dicho valor no está insertado por no confirmar la operación.

                // Esto sucede porque nuestro pool, HirakiCP, realiza un rollback, al no
                // haberse confirmado la operación, al devolver la conexión al pool.

                try (Statement stm = conn.createStatement();
                     ResultSet rs = stm.executeQuery(sql3)) {

                    while (rs.next()) {
                        System.out.println("Lenguaje: " + rs.getString(1));
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }



    }


}
