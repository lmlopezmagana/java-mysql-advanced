package net.openwebinars.java.mysql.advanced.procedures;

import net.openwebinars.java.mysql.advanced.pool.MyDataSource;

import java.sql.*;

public class StoredProcedureExample {

    public void test() {

        try (Connection conn = MyDataSource.getConnection();
             CallableStatement cstm = conn.prepareCall("{ call insertar_programador(?,?,?,?,?,?) }"))
        {

            cstm.setString(1, "Antonio");
            cstm.setString(2, "Mora Ramírez");
            cstm.setString(3, "14/07/1993");
            cstm.setString(4, "antonio@openwebinars.net");
            cstm.setString(5, "Java, Go, Python, Javascript");
            cstm.registerOutParameter(6, Types.INTEGER);

            cstm.execute();

            int emp_id = cstm.getInt(6);

            System.out.println("Se ha insertado un nuevo empleado con ID: " + emp_id);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }




    }

}
