package jotatec.stockManager.SQLconnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQLConnection {
    public static Map<String,String> query(String IdComponente) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        HashMap<String,String> result = new HashMap<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://yourDatabaseManager/Database";
            String user = "Your User";
            String password = "Your Password";
            conn = DriverManager.getConnection(url, user, password);
            String IdDigikey = "Item Id Selected";
            String IdMouser = "Item Id Selected";

            String sql = "SELECT Comp.Componente, Prov.Codigo " +
                    "FROM Componentes AS Comp " +
                    "JOIN CompProvCodigos AS Prov ON Comp.Componente_id = Prov.Componente_id " +
                    "WHERE Comp.Componente = ? AND Prov.Proveedor_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, IdComponente);
            stmt.setString(2, IdDigikey);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String CodigoDigikey = rs.getString("Codigo");
                result.put("IdComponente", IdComponente);
                result.put("CodigoDigikey", CodigoDigikey.trim());
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, IdComponente);
            stmt.setString(2, IdMouser);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String CodigoMouser= rs.getString("Codigo");
                result.put("CodigoMouser", CodigoMouser.trim());
            }
            System.out.println(result);

            return result;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
