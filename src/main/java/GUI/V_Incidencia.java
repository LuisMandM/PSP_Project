package GUI;

import Models.Area;
import Models.Estado;
import Models.Incidencia;
import Models.Usuario;
import dao.AreaDaoImpl;
import dao.IncidenciaDaoImpl;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static Server.Utils.*;

public class V_Incidencia {
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;
    private JComboBox<Area> AreaCbox;
    private JTextArea descTxtArea;
    private JButton enviarButton;
    private JButton limpiarButton;
    private JTextField areaView;
    private JTextArea descView;
    private JTextField estadoView;
    private JButton verIncidenciaButton;
    private JScrollPane incidenciasScrollp;
    private JTextField textField1;
    private JButton contestarButton;
    private JScrollPane mensajesScrollP;
    private JTextField timeView;
    private JPanel DetailPanel;
    private JPanel MensajesPanel;
    private JTable incidenciasTable;

    private Usuario usuario;
    private ArrayList<Incidencia> incidencias = new ArrayList();
    private AreaDaoImpl areaDao = new AreaDaoImpl();
    private IncidenciaDaoImpl incidenciaDao = new IncidenciaDaoImpl();

    public V_Incidencia(Usuario usuario, JFrame frame) {
        this.usuario = usuario;
        LoadIncidencias();
        LoadCbox();
        DetailPanel.setVisible(false);
        MensajesPanel.setVisible(false);
        descTxtArea.setLineWrap(true);
        descTxtArea.setWrapStyleWord(true);
        frame.pack();

        tabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane1.getSelectedIndex();

                switch (index) {
                    case 0:
                        System.out.println("Panel del form");
                        LoadCbox();
                        DetailPanel.setVisible(false);
                        MensajesPanel.setVisible(false);
                        descTxtArea.setLineWrap(true);
                        descTxtArea.setWrapStyleWord(true);
                        frame.pack();
                        break;
                    case 1:
                        System.out.println("Panel del listado");
                        // cleanForm();
                        if (LoadIncidencias()) {
                            incidenciasTable = new JTable();
                            incidenciasTable.setModel(new IncidenciasTableModel(incidencias));
                            incidenciasScrollp.setViewportView(incidenciasTable);

                        } else {
                            incidenciasTable = new JTable();
                            incidenciasTable.setModel(new IncidenciasTableModel());
                            incidenciasScrollp.setViewportView(incidenciasTable);
                            JOptionPane.showMessageDialog(panelPrincipal, "Actualmente no hay datos" +
                                    " de proyectos añadidos", "Sin Datos", JOptionPane.INFORMATION_MESSAGE);
                        }
                        //System.out.println(tablePanel.getSize());
                        break;
                }


            }
        });
        enviarButton.addActionListener(e -> {
            Incidencia current = new Incidencia();
            current.setUsuario(usuario);
            current.setDescripcion(descTxtArea.getText());
            Area area = (Area) AreaCbox.getSelectedItem();
            if (area != null) {
                current.setArea(area);
                current.setEstado(Estado.AREA);

                byte[] currentBytes = IncidenciaToBytes(current);
                if (currentBytes != null) {
                    Incidencia gestion = SendPeticion(currentBytes);
                    if (gestion != null) {
                        try {
                            Incidencia created = incidenciaDao.create(gestion);
                            if (created != null) {
                                String mensaje = String.format("Incidencia guardada con exito:\nIdentificador:%d" +
                                                "\nNivel:%s\nTiempo estimado de respuesta: %d Horas", gestion.getCodIncidencia(),
                                        gestion.getNivel().toString(), (gestion.getTiempo() / (60 * 60 * 1000)));
                                JOptionPane.showMessageDialog(panelPrincipal, mensaje, "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
                                LoadIncidencias();
                            }

                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(panelPrincipal, "Error Guardado: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else JOptionPane.showMessageDialog(panelPrincipal, """
                            La incidencia no ha podido ser validada intenta nuevamente
                            """, "Error", JOptionPane.ERROR_MESSAGE);
                }

            }

        });
        verIncidenciaButton.addActionListener(e -> {
            if (incidenciasTable.getRowCount() > 0 && incidenciasTable.getSelectedRow() >= 0) {
                Incidencia current = incidencias.get(incidenciasTable.getSelectedRow());
                areaView.setText(current.getArea().toString());
                descView.setText(current.getDescripcion());
                timeView.setText(String.format("%d Horas", current.getTiempo() / (60 * 60 * 1000)));

                switch (current.getEstado()) {
                    case USUARIO -> estadoView.setText("Pendiente de acción Empleado");
                    case AREA -> estadoView.setText("Pendiente de acción Area");
                    case CERRADO -> estadoView.setText("Cerrado");
                }

                areaView.setEditable(false);
                descView.setEditable(false);
                timeView.setEditable(false);
                estadoView.setEditable(false);

                descView.setLineWrap(true);
                descView.setWrapStyleWord(true);
                DetailPanel.setVisible(true);
                frame.pack();
            }
        });


    }

    private void LoadCbox() {
        AreaCbox.removeAllItems();
        try {
            ArrayList<Area> areas = (ArrayList<Area>) areaDao.getAll();
            if (areas != null) {
                for (Area area : areas) {
                    AreaCbox.addItem(area);
                }
            } else System.out.println("Sin Areas a cargar");
        } catch (Exception e) {
            System.out.println("Error cargando areas: " + e.getMessage());
        }
    }

    private boolean LoadIncidencias() {
        incidencias.clear();
        ArrayList<Incidencia> incidenciasBD = (ArrayList<Incidencia>) incidenciaDao.getByUser(usuario.getCodigoEmple());
        if (incidenciasBD != null) {
            incidencias.addAll(incidenciasBD);
        }

        return !incidencias.isEmpty();
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    private Incidencia SendPeticion(byte[] report) {
        System.setProperty("javax.net.ssl.trustStore", ".\\Certificados\\AlmacenUsuarioSSL.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "1234567");
        int port = 8888;
        String host = "localhost";
        Incidencia result = null;
        PublicKey serverPublicKey = null;
        SecretKey secretKey = null;

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket peticion = (SSLSocket) socketFactory.createSocket(host, port);

            ObjectOutputStream salida = new ObjectOutputStream(peticion.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(peticion.getInputStream());

            salida.writeObject(usuario.getPublicKey());
            serverPublicKey = (PublicKey) entrada.readObject();

            int mensaje = 200;
            byte[] peticionbytes = ByteBuffer.allocate(4).putInt(mensaje).array();
            salida.writeObject(cifrarConClavePublica(peticionbytes, serverPublicKey));

            byte[] confirmacionRaw = (byte[]) entrada.readObject();
            confirmacionRaw = descifrarConClavePrivada(confirmacionRaw, usuario.getPrivateKey());
            int confirmacion = ByteBuffer.wrap(confirmacionRaw).getInt();

            if (confirmacion == 200) {
                byte[] sign = FirmarIncidencia(report);
                if (sign != null) {
                    secretKey = GenerarLLaveSincro();
                    byte[] reportAES = CifrarAES(report, secretKey);
                    byte[] AESCipher = cifrarConClavePublica(secretKey.getEncoded(), serverPublicKey);
                    salida.writeObject(AESCipher);
                    salida.writeObject(reportAES);
                    salida.writeObject(sign);
                }

                boolean firmaCheck = (boolean) entrada.readObject();
                if (firmaCheck) {
                    boolean castCheck = (boolean) entrada.readObject();
                    if (castCheck) {
                        byte[] reportCipher = (byte[]) entrada.readObject();
                        byte[] reportGest = DescifrarAES(reportCipher, secretKey);
                        Incidencia reportReady = BytesToIncidencia(reportGest);
                        if (reportReady != null) {
                            result = reportReady;
                        }
                    } else JOptionPane.showMessageDialog(panelPrincipal, """
                            Error al gestionar la incidencia intente nuevamente.
                            """, "Error", JOptionPane.ERROR_MESSAGE);
                } else JOptionPane.showMessageDialog(panelPrincipal, """
                        Error Con la firma del documento.
                        """, "Error", JOptionPane.ERROR_MESSAGE);

                entrada.close();
                salida.close();
                peticion.close();


            } else JOptionPane.showMessageDialog(panelPrincipal, """
                    Error Inesperado con el servidor
                    """, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            System.out.println("Error en la creación y comunicaciónd el Socket: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al castear: " + e.getMessage());
        }


        return result;
    }

    private byte[] FirmarIncidencia(byte[] report) {
        byte[] sign = null;
        try {
            Signature dsa = Signature.getInstance("SHA1withRSA");
            dsa.initSign(usuario.getPrivateKey());
            dsa.update(report);
            sign = dsa.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println("Error de firma: " + e.getMessage());
        }

        return sign;
    }
}
