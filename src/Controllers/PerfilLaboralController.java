package Controllers;

import Models.CentrarColumnas;
import Models.ColorearLabels;
import Models.PerfilLaboral;
import Models.PerfilLaboralDAO;
import Models.Trabajador;
import Models.TrabajadorDAO;
import Models.Validaciones;

import Views.FrmMenu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//import java.sql.Date;// primero
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PerfilLaboralController implements ActionListener, KeyListener, MouseListener {

    Trabajador trabajador = null;
    Object estado = null;

    //  Instancias de clases
    private PerfilLaboral plab;
    private PerfilLaboralDAO plabDAO;
    private FrmMenu frmMenu;

    private String[] categoriaCargos = {"seleccionar", "Operaciones Mina", "Seguridad", "Administración", "Mantenimiento", "Transporte"};  //  Array de areas
    private String[] filtros = {"Nombre", "DNI", "Área", "Cargo", "Estado"};  //  Array de filtros

    public PerfilLaboralController(PerfilLaboral plab, PerfilLaboralDAO plabDAO, FrmMenu frmMenu) {
        this.plab = plab;
        this.plabDAO = plabDAO;
        this.frmMenu = frmMenu;
        diseñarIntefaz();
        interfaces();
//        llenarCombo();
        cargarTabla();
        limpiarInputs();
        limpiarMensajesError();
        enableButtons();
        cargarAreas();
        cargarFiltros();
    }

    //  Metodo para diseñar el panel de perfil laboral de trabajadores
    private void diseñarIntefaz() {
        frmMenu.txtIdTrabajadorPerfil.setBackground(Color.white);
    }

    //  Metodo para importar las interfaces utilizadas
    private void interfaces() {
        //  Eventos ActionListener
        frmMenu.btnRegistrarPerfilLaboral.addActionListener(this);
        frmMenu.btnActualizarPerfil.addActionListener(this);
        frmMenu.cboAreaPerfil.addActionListener(this);
        frmMenu.btnEstadoPerfil.addActionListener(this);
        frmMenu.cboFiltrarContratoPor.addActionListener(this);

        //  Eventos MouseListener
        frmMenu.txtFechaIngreso.addMouseListener(this);
        frmMenu.txtFechaCese.addMouseListener(this);
        frmMenu.cboAreaPerfil.addMouseListener(this);
        frmMenu.tblPerfilLaboral.addMouseListener(this);
        frmMenu.btnSeleccionarTrabajadorPerfil.addMouseListener(this);
        frmMenu.pnlTrabajador.addMouseListener(this);

        //  Eventos KeyListener
        frmMenu.txtSueldo.addKeyListener(this);
        frmMenu.tblPerfilLaboral.addKeyListener(this);
        frmMenu.txtFiltrarTrabajadorPerfil.addKeyListener(this);
        frmMenu.txtFiltroContratoLista.addKeyListener(this);
    }

    //  Metodo para llenar comboBox de areas
    private void cargarAreas() {
        for (String categoriaCargo : categoriaCargos) {
            frmMenu.cboAreaPerfil.addItem(categoriaCargo);
        }
    }

    //  Metodo para cargar filtros del combo box
    private void cargarFiltros() {
        for (String filtro : filtros) {
            frmMenu.cboFiltrarContratoPor.addItem(filtro);
        }
    }

    //  Metodo para listar perfiles laborales de los trabajadores
    private void cargarTabla() {
        //  Diseño de la tabla Perfil Laboral - Vista Usuario
        int anchos[] = {10, 200, 30, 80, 30, 150}; //anchos de las columnas
        DefaultTableModel model = (DefaultTableModel) frmMenu.tblPerfilLaboral.getModel(); // Obtencion del modelo de la tabla
        model.setRowCount(0);
        for (int i = 0; i < frmMenu.tblPerfilLaboral.getColumnCount(); i++) {
            frmMenu.tblPerfilLaboral.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]); // establecer los anchos
        }
        frmMenu.tblPerfilLaboral.setDefaultRenderer(Object.class, new CentrarColumnas()); // centrado de datos
        frmMenu.tblPerfilLaboral.getColumnModel().getColumn(4).setCellRenderer(new ColorearLabels(4));
        frmMenu.tblPerfilLaboral.getColumnModel().getColumn(5).setCellRenderer(new ColorearLabels(5));
        frmMenu.tblPerfilLaboral.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        frmMenu.tblPerfilLaboral.getTableHeader().setOpaque(false);
        frmMenu.tblPerfilLaboral.getTableHeader().setBackground(Color.decode("#243b55"));
        frmMenu.tblPerfilLaboral.getTableHeader().setForeground(Color.decode("#FFFFFF"));
        plabDAO.listarPerfilLaboral(model); // llamada del metodo dao listar

        //  Diseño de la tabla listar Contratos - Vista Administrador
        int anchosLista[] = {10, 200, 50, 100, 200, 50, 150}; // anchos para la tabla listar
        DefaultTableModel modelLista = (DefaultTableModel) frmMenu.tblListaContratos.getModel(); // Obtencion del modelo de la tabla
        modelLista.setRowCount(0);
        for (int i = 0; i < frmMenu.tblListaContratos.getColumnCount(); i++) {
            frmMenu.tblListaContratos.getColumnModel().getColumn(i).setPreferredWidth(anchosLista[i]); // establecer los anchos
        }
        frmMenu.tblListaContratos.setDefaultRenderer(Object.class, new CentrarColumnas()); //centrado de datos
        frmMenu.tblListaContratos.getColumnModel().getColumn(5).setCellRenderer(new ColorearLabels(5));
        frmMenu.tblListaContratos.getColumnModel().getColumn(6).setCellRenderer(new ColorearLabels(6));
        frmMenu.tblListaContratos.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        frmMenu.tblListaContratos.getTableHeader().setOpaque(false);
        frmMenu.tblListaContratos.getTableHeader().setBackground(Color.decode("#243b55"));
        frmMenu.tblListaContratos.getTableHeader().setForeground(Color.decode("#FFFFFF"));
        plabDAO.mostrarContratos(modelLista); // llamada del metodo dao listar
    }

    //  Metodo para activar botones
    private void enableButtons() {
        frmMenu.btnRegistrarPerfilLaboral.setEnabled(true);
        frmMenu.btnActualizarPerfil.setEnabled(false);
    }

    //  Metodo para desactivar botones
    private void disableButtons() {
        frmMenu.btnRegistrarPerfilLaboral.setEnabled(false);
        frmMenu.btnActualizarPerfil.setEnabled(true);
    }

    //  Metodo para llenar combo
    private void llenarCombo() {
//        try {
//            traDAO.llenarComboTrabajador(frmMenu.cboTrabajadorPerfil);
//        } catch (Exception ex) {
//            System.out.println("Error llenarCombo");
//        }
    }

    //  Metodo para limpiar inputs
    private void limpiarInputs() {
        frmMenu.txtCodPerfilLaboral.setText("");
        frmMenu.txtIdTrabajadorPerfil.setText("");
        frmMenu.txtTrabajadorAsignadoPerfil.setText("");
        frmMenu.txtCargoAsignadoPerfil.setText("");
        frmMenu.txtFechaIngreso.setText("");
        frmMenu.cboAreaPerfil.setSelectedItem("seleccionar");
        frmMenu.txtSueldo.setText("");
        frmMenu.txtFechaCese.setText("");
        frmMenu.txtMotivo.setText("");
        frmMenu.txtFiltrarTrabajadorPerfil.setText("");
        frmMenu.txtFiltroContratoLista.setText("");
        frmMenu.tblPerfilLaboral.clearSelection();
    }

    //  Metodo para limpiar mensajes de error
    private void limpiarMensajesError() {
        frmMenu.mTrabajadorAsignadoPerfil.setText("");
        frmMenu.mFechaIngreso.setText("");
        frmMenu.mArea.setText("");
        frmMenu.mSueldo.setText("");
        frmMenu.mFechaCese.setText("");
        frmMenu.mMotivoCese.setText("");
    }

    //  Metodo para validar campos vacios
    private boolean validarCamposVacios() {
        boolean valor = true;   // Valor inicial verdadero
        if ((frmMenu.txtTrabajadorAsignadoPerfil.getText().equals(""))) {
            frmMenu.mTrabajadorAsignadoPerfil.setText("Seleccione un trabajador");
            frmMenu.mTrabajadorAsignadoPerfil.setForeground(Color.decode("#E94560"));
            frmMenu.txtTrabajadorAsignadoPerfil.requestFocus();
            valor = false;
        } else if (frmMenu.txtFechaIngreso.getText().equals("")) {
            frmMenu.mFechaIngreso.setText("Ingrese o seleccione una fecha");
            frmMenu.mFechaIngreso.setForeground(Color.decode("#E94560"));
            frmMenu.txtFechaIngreso.requestFocus();
            valor = false;
        } else if (frmMenu.cboAreaPerfil.getSelectedItem().equals("seleccionar")) {
            frmMenu.mArea.setText("Seleccione una área");
            frmMenu.mArea.setForeground(Color.decode("#E94560"));
            valor = false;
        } else if (frmMenu.txtSueldo.getText().isEmpty()) {
            frmMenu.mSueldo.setText("Ingrese sueldo");
            frmMenu.mSueldo.setForeground(Color.decode("#E94560"));
            frmMenu.txtSueldo.requestFocus();
            valor = false;
        }
        return valor;
    }

    //  Metodo para validar existencia de contrato
    private boolean validarExistenciaContrato() {
        boolean valor = true;
        if (frmMenu.txtIdTrabajadorPerfil.getText().isEmpty()) {
            frmMenu.mTrabajadorAsignadoPerfil.setText("Seleccione un trabajador");
        } else if (plabDAO.existeContrato(Integer.parseInt(frmMenu.txtIdTrabajadorPerfil.getText())) != 0) {
            frmMenu.mTrabajadorAsignadoPerfil.setText("Ya existe perfil para este trabajador");
            frmMenu.mTrabajadorAsignadoPerfil.setForeground(Color.decode("#E94560"));
            frmMenu.txtIdTrabajadorPerfil.setText("");
            frmMenu.txtTrabajadorAsignadoPerfil.setText("");
            frmMenu.txtCargoAsignadoPerfil.setText("");
            valor = false;
        }
        return valor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //  Evento boton registrarPerfilLaboral
        if (e.getSource().equals(frmMenu.btnRegistrarPerfilLaboral)) {
            boolean validarVacios = validarCamposVacios();
            boolean validarContrato = validarExistenciaContrato();

            if (validarVacios == false) {
                validarCamposVacios();
            } else {
                if (validarContrato == false) {
                    validarExistenciaContrato();
                } else {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                Date fechaIngreso = Date.valueOf(frmMenu.txtFechaIngreso.getText());//primero
                    Date fechaIngreso = null;
                    try {
                        fechaIngreso = format.parse(frmMenu.txtFechaIngreso.getText());
                    } catch (ParseException ex) {
                    }

                    String area = frmMenu.cboAreaPerfil.getSelectedItem().toString();
                    double sueldo = Double.parseDouble(frmMenu.txtSueldo.getText());

//                Date fechaCese = (Date) frmMenu.txtFechaCese.getDate(); // date chooser jcalendar
                    Date fechaCese = null;
                    try {
                        fechaCese = format.parse(frmMenu.txtFechaCese.getText());
                    } catch (ParseException ex) {
                    }
//                Date fechaCese = Date.valueOf(frmMenu.txtFechaCese1.getText());   // dateChooser custom      
                    String motivo = frmMenu.txtMotivo.getText();
                    int idTrabajador = Integer.parseInt(frmMenu.txtIdTrabajadorPerfil.getText());
                    plab = new PerfilLaboral(fechaIngreso, area, sueldo, fechaCese, motivo, idTrabajador);
                    if (plabDAO.registrarPerfil(plab)) {
                        cargarTabla();
                        limpiarInputs();
                        JOptionPane.showMessageDialog(frmMenu, "Perfil registrado");
                    }
                }
            }
        }
        //  Evento ActionListener para el boton actualizar perfil
        if (e.getSource().equals(frmMenu.btnActualizarPerfil)) {
            int codPerfil = Integer.parseInt(frmMenu.txtCodPerfilLaboral.getText());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaIngreso = null;
            try {
                fechaIngreso = format.parse(frmMenu.txtFechaIngreso.getText());
            } catch (ParseException ex) {
            }
            String area = frmMenu.cboAreaPerfil.getSelectedItem().toString();
            double sueldo = Double.parseDouble(frmMenu.txtSueldo.getText());
            Date fechaCese = null;
            try {
                fechaCese = format.parse(frmMenu.txtFechaCese.getText());
            } catch (ParseException ex) {
            }
            String motivo = frmMenu.txtMotivo.getText();
            int idTrabajador = Integer.parseInt(frmMenu.txtIdTrabajadorPerfil.getText());
            plab = new PerfilLaboral(codPerfil, fechaIngreso, area, sueldo, fechaCese, motivo, idTrabajador);
            if (plabDAO.modificarPerfilLaboral(plab)) {
                limpiarInputs();
                cargarTabla();
                JOptionPane.showMessageDialog(frmMenu, "Contrato actualizado");
                enableButtons();
            } else {
                JOptionPane.showMessageDialog(frmMenu, "No se pudo actulizar contrato");
            }
        }
        //  Evento ActionListener boton estado en el panel Registrar Perfil Laboral
        if (e.getSource().equals(frmMenu.btnEstadoPerfil)) {
            DefaultTableModel model = (DefaultTableModel) frmMenu.tblPerfilLaboral.getModel(); // Capturar el modelo de la tabla
            cargarTabla();
            //  Lista de opciones para el comboBox del JOptionPane
            String listaOpciones[] = {"Activo", "Cesado"};
            //  Agregar las opciones en el combo box
            JComboBox cb = new JComboBox(listaOpciones);
            //  Diseño de comboBox interno
            cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cb.setFont(new Font("Dialog", Font.PLAIN, 14));
            int input;
            input = JOptionPane.showConfirmDialog(frmMenu, cb, "Seleccionar estado", JOptionPane.DEFAULT_OPTION);
            //  Si se acepta una opcion
            if (input == JOptionPane.OK_OPTION) {
                // Escoger opcion
                String opcion = (String) cb.getSelectedItem();
                if ("Activo".equals(opcion)) { // opcion Activo - mostrar contratos activos
                    plabDAO.mostrarContratosActivos(model);
                } else if ("Cesado".equals(opcion)) { // opcion Cesado - mostrar contratos cesados
                    plabDAO.mostrarContratosCesados(model);
                }
            }
        }
        //  Evento ActionListener para el combo box de filtros del panel Listar Contratos
        if (e.getSource().equals(frmMenu.cboFiltrarContratoPor)) {
            if (frmMenu.cboFiltrarContratoPor.getSelectedItem().equals("Nombre")) {
                frmMenu.txtFiltroContratoLista.setLabelText("Nombre del trabajador");
                frmMenu.txtFiltroContratoLista.setText("");
                frmMenu.txtFiltroContratoLista.requestFocus();
            } else if (frmMenu.cboFiltrarContratoPor.getSelectedItem().equals("DNI")) {
                frmMenu.txtFiltroContratoLista.setLabelText("DNI");
                frmMenu.txtFiltroContratoLista.setText("");
                frmMenu.txtFiltroContratoLista.requestFocus();
            } else if (frmMenu.cboFiltrarContratoPor.getSelectedItem().equals("Área")) {
                frmMenu.txtFiltroContratoLista.setLabelText("Área");
                frmMenu.txtFiltroContratoLista.setText("");
                frmMenu.txtFiltroContratoLista.requestFocus();
            } else if (frmMenu.cboFiltrarContratoPor.getSelectedItem().equals("Cargo")) {
                frmMenu.txtFiltroContratoLista.setLabelText("Cargo");
                frmMenu.txtFiltroContratoLista.setText("");
                frmMenu.txtFiltroContratoLista.requestFocus();
            } else if (frmMenu.cboFiltrarContratoPor.getSelectedItem().equals("Estado")) {
                frmMenu.txtFiltroContratoLista.setLabelText("Estado (activo - inactivo)");
                frmMenu.txtFiltroContratoLista.setText("");
                frmMenu.txtFiltroContratoLista.requestFocus();
            }
        }
        // Evento ActionListener para los itemsPopUp
        if (e.getSource().equals(frmMenu.JReingresarTrabajador)) {
            if (!frmMenu.txtIdTrabajador.getText().isEmpty()) {
                cargarTabla();
                limpiarInputs();
            }
            if (!frmMenu.txtIdTrabajador.getText().isEmpty()) {
                cargarTabla();
                limpiarInputs();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //  Evento KeyTyped para validar
        if (e.getSource().equals(frmMenu.txtSueldo)) {
            Validaciones.soloDigitos(e); // validar el tipeo de solo letras
        } else if (e.getSource().equals(frmMenu.txtFiltroContratoLista)) {
            switch (frmMenu.txtFiltroContratoLista.getLabelText()) {
                case "Nombre del trabajador":
                    Validaciones.soloLetras(e);
                    break;
                case "DNI":
                    Validaciones.soloDigitos(e);
                    int limiteDNI = 8;
                    if (frmMenu.txtFiltroContratoLista.getText().length() == limiteDNI) {
                        e.consume();
                    }
                    break;
                case "Área":
                    Validaciones.soloLetras(e);
                    break;
                case "Cargo":
                    Validaciones.soloLetras(e);
                    break;
                default:
                    Validaciones.soloLetras(e);
                    break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke
    ) {

    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
        //  Eventos que al escribir contenido en cajas de texto, los mensajes de error se ocultan
        if (e.getSource().equals(frmMenu.txtSueldo)) {
            frmMenu.mSueldo.setText("");
        }
        //  Evento Limpiar seleccion con Escape despues de clickear tabla
        if (e.getSource().equals(frmMenu.tblPerfilLaboral)) {
            //  seteo de datos con las flechas arriba y abajo sobre la tabla
            if ((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_UP)) {
                disableButtons();// desabilitar boton registrar
                limpiarMensajesError(); //  limpiar mensajes de error
                // seleccionar fila de tabla
                int fila = frmMenu.tblPerfilLaboral.getSelectedRow();
                // extraer la primera columna de la tabla
                int codPerfil = Integer.parseInt(frmMenu.tblPerfilLaboral.getValueAt(fila, 0).toString());
                //  setear el valor extraido 
                frmMenu.txtCodPerfilLaboral.setText(String.valueOf(codPerfil));

                if (!frmMenu.txtCodPerfilLaboral.getText().isEmpty()) { // cuando se setee el codigo de guardia
                    // Obtener el valor de la caja de texto del codigo de perfil
                    int cod = Integer.parseInt(frmMenu.txtCodPerfilLaboral.getText());
                    //  Ejecutar el metodo consultar perfil por codigo
                    plab = plabDAO.consultarPerfil(cod);
                    // Seteo de datos
                    frmMenu.txtFechaIngreso.setText(String.valueOf(plab.getFechaIngreso()));
                    frmMenu.cboAreaPerfil.setSelectedItem(String.valueOf(plab.getArea()));
                    frmMenu.txtSueldo.setText(String.valueOf(plab.getSueldo()));
                    frmMenu.txtFechaCese.setText(String.valueOf(plab.getFechaCese()));
                    if (frmMenu.txtFechaCese.getText().equals("null")) {    // si se setea null
                        frmMenu.txtFechaCese.setText("");   // eliminar contenido
                    } else {
                        frmMenu.txtFechaCese.setForeground(Color.decode("#E94560"));
                    }
                    frmMenu.txtMotivo.setText(String.valueOf(plab.getMotivoCese()));
                    frmMenu.txtIdTrabajadorPerfil.setText(String.valueOf(plab.getTrabajador().getIdTrabajador()));
                    frmMenu.txtTrabajadorAsignadoPerfil.setText(String.valueOf(plab.getTrabajador()));
                    int id = Integer.parseInt(frmMenu.txtIdTrabajadorPerfil.getText());
                    trabajador = TrabajadorDAO.getInstancia().consultarTrabajador(id);
                    frmMenu.txtCargoAsignadoPerfil.setText(String.valueOf(trabajador.getCargo()));
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // Evento tecla escape
                limpiarInputs(); // limpiar entradas
                limpiarMensajesError(); // limpiar mensajes de error
                enableButtons(); // habilitar botones
                cargarTabla(); // cargar tabla 
            }
        }
        //  Evento KeyReleased para el filtro de busqueda en el panel Registrar Perfil Laboral 
        if (e.getSource().equals(frmMenu.txtFiltrarTrabajadorPerfil)) {
            //  Capturar el modelo establecido para la tabla de perfil laboral
            DefaultTableModel model = (DefaultTableModel) frmMenu.tblPerfilLaboral.getModel();
            String nombreFiltro = frmMenu.txtFiltrarTrabajadorPerfil.getText();
            plabDAO.filtrarBusqueda(nombreFiltro, model);
            // Evento KeyReleased para la tecla Escape
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                limpiarInputs();
                limpiarMensajesError();
                enableButtons();
                cargarTabla();
            }
        }

        //  Evento de filtrado de busqueda en el listado de contratos
        if (e.getSource().equals(frmMenu.txtFiltroContratoLista)) {
            //  Capturar el modelo de la tabla ListarContratos - Vista administrador
            DefaultTableModel model = (DefaultTableModel) frmMenu.tblListaContratos.getModel();
            // Establecer metodos por cada opcion
            switch (frmMenu.txtFiltroContratoLista.getLabelText()) {
                case "Nombre del trabajador":
                    String nombreTrabajador = frmMenu.txtFiltroContratoLista.getText();
                    plabDAO.filtrarBusquedaNombre(nombreTrabajador, model);
                    break;
                case "DNI":
                    String dniTrabajador = frmMenu.txtFiltroContratoLista.getText();
                    plabDAO.filtrarBusquedaDni(dniTrabajador, model);
                    break;
                case "Área":
                    String areaTrabajador = frmMenu.txtFiltroContratoLista.getText();
                    plabDAO.filtrarBusquedaArea(areaTrabajador, model);
                    break;
                case "Cargo":
                    String cargoTrabajador = frmMenu.txtFiltroContratoLista.getText();
                    plabDAO.filtrarBusquedaCargo(cargoTrabajador, model);
                    break;
                default:
                    String estadoContrato = frmMenu.txtFiltroContratoLista.getText();
                    plabDAO.filtrarBusquedaEstado(estadoContrato, model);
                    break;
            }

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                limpiarInputs();
                limpiarMensajesError();
                enableButtons();
                cargarTabla();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e
    ) {
        //  Evento de clickeo en la caja de fechas
        if (e.getSource().equals(frmMenu.cboAreaPerfil)) {
            frmMenu.mArea.setText("");
        }
        if (e.getSource().equals(frmMenu.txtFechaIngreso)) {
            frmMenu.mFechaIngreso.setText("");
        }
        if (e.getSource().equals(frmMenu.txtFechaCese)) {
            frmMenu.mFechaCese.setText("");
        }
        //  Evento de clickeo para la tabla de perfilLaboral
        if (e.getSource().equals(frmMenu.tblPerfilLaboral)) {
            disableButtons();// desabilitar boton registrar
            limpiarMensajesError(); //  limpiar mensajes de error
            // seleccionar fila de tabla
            int fila = frmMenu.tblPerfilLaboral.getSelectedRow();
            // extraer la primera columna de la tabla
            int codPerfil = Integer.parseInt(frmMenu.tblPerfilLaboral.getValueAt(fila, 0).toString());
            //  setear el valor extraido 
            frmMenu.txtCodPerfilLaboral.setText(String.valueOf(codPerfil));

            if (!frmMenu.txtCodPerfilLaboral.getText().isEmpty()) { // cuando se setee el codigo de guardia
                // Obtener el valor de la caja de texto del codigo de perfil
                int cod = Integer.parseInt(frmMenu.txtCodPerfilLaboral.getText());
                //  Ejecutar el metodo consultar perfil por codigo
                plab = plabDAO.consultarPerfil(cod);
                // Seteo de datos
                frmMenu.txtFechaIngreso.setText(String.valueOf(plab.getFechaIngreso()));
                frmMenu.cboAreaPerfil.setSelectedItem(String.valueOf(plab.getArea()));
                frmMenu.txtSueldo.setText(String.valueOf(plab.getSueldo()));
                frmMenu.txtFechaCese.setText(String.valueOf(plab.getFechaCese()));
                if (frmMenu.txtFechaCese.getText().equals("null")) {    // si se setea null
                    frmMenu.txtFechaCese.setText("");   // eliminar contenido
                } else {
                    frmMenu.txtFechaCese.setForeground(Color.decode("#E94560"));
                }
                frmMenu.txtMotivo.setText(String.valueOf(plab.getMotivoCese()));
                if (frmMenu.txtMotivo.getText().equals("null")) {    // si se setea null
                    frmMenu.txtMotivo.setText("");   // eliminar contenido
                } else {
                    frmMenu.txtMotivo.setForeground(Color.decode("#E94560"));
                }
                frmMenu.txtIdTrabajadorPerfil.setText(String.valueOf(plab.getTrabajador().getIdTrabajador()));
                frmMenu.txtTrabajadorAsignadoPerfil.setText(String.valueOf(plab.getTrabajador()));
                int id = Integer.parseInt(frmMenu.txtIdTrabajadorPerfil.getText());
                trabajador = TrabajadorDAO.getInstancia().consultarTrabajador(id);
                frmMenu.txtCargoAsignadoPerfil.setText(String.valueOf(trabajador.getCargo()));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e
    ) {
        if (e.getSource().equals(frmMenu.btnSeleccionarTrabajadorPerfil)) {
            frmMenu.mTrabajadorAsignadoPerfil.setText("");
        }
        if (e.getSource().equals(frmMenu.pnlTrabajador)) {
            cargarTabla();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me
    ) {

    }

    @Override
    public void mouseEntered(MouseEvent me
    ) {

    }

    @Override
    public void mouseExited(MouseEvent me
    ) {

    }

}
