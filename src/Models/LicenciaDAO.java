package Models;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class LicenciaDAO extends Conexion {

//  Atributos de conexion
    private Connection cn = null;
    private ResultSet rs = null;
    private CallableStatement cs = null;
    private PreparedStatement ps = null;
    private ResultSetMetaData rsmd = null;

    //  Establecer formato para el ingreso de la fecha
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    //  Instancia de la clase
    private static LicenciaDAO instancia;

    public static LicenciaDAO getInstancia() {
        if (instancia == null) {
            instancia = new LicenciaDAO();
        }
        return instancia;
    }

    //  Metodo para registrar licencias de trabajador
    public boolean registrarLicencia(Licencia x) {
        cn = getConexion();
        String sql = "{call usp_registrar_licencia(?,?,?,?,?)}";
        try {
            cn.setAutoCommit(true); //cancelar el control de transacciones
            cs = cn.prepareCall(sql);
            cs.setString(1, x.getNumLicencia());
            cs.setString(2, x.getCategoria());
            if (x.getFechaEmision() != null) {
                cs.setDate(3, java.sql.Date.valueOf(df.format(x.getFechaEmision())));
            } else {
                cs.setDate(3, null);
            }
            if (x.getFechaCaducidad() != null) {
                cs.setDate(4, java.sql.Date.valueOf(df.format(x.getFechaCaducidad())));
            } else {
                cs.setDate(4, null);
            }
            cs.setInt(5, x.getIdTrabajador());
            cs.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.println("Error DAO: registrarLicencia... " + ex.getMessage());
            return false;
        } finally {
            try {
                cs.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("Error SQLException: registrarLicencia.... " + ex.getMessage());
            }
        }
    }

    //  Metodo para actualizar licencias de trabajador
    public boolean actualizarLicencia(Licencia x) {
        cn = getConexion();
        String sql = "call usp_actualizar_licencia(?,?,?,?,?,?)";
        try {
            cs = cn.prepareCall(sql);
            cs.setString(1, x.getNumLicencia());
            cs.setString(2, x.getCategoria());
            if (x.getFechaEmision() != null) {   //DateChooser jCalendar
                cs.setDate(3, new java.sql.Date(x.getFechaEmision().getTime()));
            } else {
                cs.setDate(3, null);
            }
            if (x.getFechaCaducidad() != null) {   //DateChooser jCalendar
                cs.setDate(4, new java.sql.Date(x.getFechaCaducidad().getTime()));
            } else {
                cs.setDate(4, null);
            }
            cs.setInt(5, x.getIdTrabajador());
            cs.setInt(6, x.getCodLicencia());
            cs.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.println("Error DAO: actualizarLicencia... " + ex.getMessage());
            return false;
        } finally {
            try {
                cs.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("ERROR SQLException: actualizarLicencia... " + ex.getMessage());
            }
        }
    }

    //  MEtodo para listar licencias de conducir
    public void listarLicencias(DefaultTableModel model) {
        cn = getConexion();
        int columnas;
        String sql = "select * from listar_licencia";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            rsmd = rs.getMetaData();
            columnas = rsmd.getColumnCount();
            while (rs.next()) {
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                model.addRow(fila);
            }
        } catch (Exception e) {
            System.out.println("ERROR DAO: listarLicencias... " + e.getMessage());
        } finally {

            try {
                ps.close();
                rs.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("Error SQLException: listarLicencias... " + ex.getMessage());
            }
        }
    }

    //  MEtodo para listar licencias de conducir
    public void mostrarLicencias(DefaultTableModel model) {
        cn = getConexion();
        int columnas;
        String sql = "select * from listar_licencias";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            rsmd = rs.getMetaData();
            columnas = rsmd.getColumnCount();
            while (rs.next()) {
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                model.addRow(fila);
            }
        } catch (Exception e) {
            System.out.println("ERROR DAO: mostrarLicencias... " + e.getMessage());
        } finally {

            try {
                ps.close();
                rs.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("Error SQLException: mostrarLicencias... " + ex.getMessage());
            }
        }
    }

    //  Metodo para consultar licencias por codigo
    public Licencia consultarLicencia(int cod) {
        cn = getConexion();
        Licencia licencia = null;
        String sql = "select * from licencia where codLicencia = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, cod);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                String numLicencia = rs.getString("numLicencia");
                String categoria = rs.getString("categoria");
                Date fechaEmision = rs.getDate("fechaEmision");
                Date fechaCaducidad = rs.getDate("fechaCaducidad");
                int idTrabajador = rs.getInt("idTrabajador");
                Trabajador trabajador = TrabajadorDAO.getInstancia().consultarTrabajador(idTrabajador);

                licencia = new Licencia(numLicencia, categoria, fechaEmision, fechaCaducidad, trabajador);
            }
        } catch (Exception e) {
            System.out.println("Error DAO: consultarLicencia... " + e.getMessage());
        } finally {
            try {
                ps.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("ERROR SQLException: consultarPerfil... " + ex.getMessage());
            }
        }
        return licencia;
    }

    //  Metodo para validar existencia de numero de licencia
    public int existeNumLicencia(String num) {
        cn = getConexion();
        String sql = "select count(codLicencia) from licencia where numLicencia = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, num);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1;
        } catch (Exception e) {
            System.out.println("Error DAO: existeNumLicencia... " + e.getMessage());
            return 1;
        } finally {
            try {
                ps.close();
                rs.close();
                cn.close();
            } catch (SQLException ex) {
                System.out.println("Error SQLException: existeNumLicencia... " + ex.getMessage());
            }
        }
    }
}
