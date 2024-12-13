package GUI;

import Models.Area;
import Models.Estado;
import Models.Incidencia;
import dao.AreaDaoImpl;
import dao.IncidenciaDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static Server.Docker.DockerLauncher;

public class V_Resolver {
    private JPanel panelPrincipal;
    private JComboBox<Area> AreaCbox;
    private JComboBox<Incidencia> incidenciasCbox;
    private JPanel DetailPanel;
    private JTextField areaView;
    private JTextArea descView;
    private JTextField estadoView;
    private JTextField timeView;
    private JTextArea mensajetextArea;
    private JButton marcarComoResueltaButton;

    private ArrayList<Area> areas = new ArrayList<>();
    private ArrayList<Incidencia> incidencias = new ArrayList<>();
    private AreaDaoImpl areaDao = new AreaDaoImpl();
    private IncidenciaDaoImpl incidenciaDao = new IncidenciaDaoImpl();
    private Incidencia incidenciaEdit = null;

    public V_Resolver() {

        LoadCbox();
        ConfigDetailPanel();

        AreaCbox.addActionListener(e -> {
            ListenerAreaCbox();

        });

        incidenciasCbox.addActionListener(e -> {
            Incidencia incidencia = (Incidencia) incidenciasCbox.getSelectedItem();
            if (incidencia != null) {
                areaView.setText(incidencia.getArea().toString());
                descView.setText(incidencia.getDescripcion());
                timeView.setText(String.format("%d Horas", incidencia.getTiempo() / (60 * 60 * 1000)));

                switch (incidencia.getEstado()) {
                    case USUARIO -> estadoView.setText("Pendiente de acción Empleado");
                    case AREA -> estadoView.setText("Pendiente de acción Area");
                    case CERRADO -> estadoView.setText("Cerrado");
                }
                incidenciaEdit = incidencia;
            }
        });

        marcarComoResueltaButton.addActionListener(e -> {
            String estado = mensajetextArea.getText();
            byte[] estadoRaw = estado.getBytes();
            incidenciaEdit.setMensajes(estadoRaw);
            incidenciaEdit.setEstado(Estado.CERRADO);

            try {
                Incidencia result = incidenciaDao.update(incidenciaEdit);
                if (result != null) {
                    JOptionPane.showMessageDialog(panelPrincipal, "Incidencia Cerrada con exito", "Cerrado", JOptionPane.INFORMATION_MESSAGE);
                    ListenerAreaCbox();
                    areaView.setText("");
                    descView.setText("");
                    timeView.setText("");
                    estadoView.setText("");
                } else
                    JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);

            } catch (SQLException ex) {
                System.out.println("Error: " + ex);
            }


        });


    }

    private void ConfigDetailPanel() {
        areaView.setEditable(false);
        descView.setEditable(false);
        timeView.setEditable(false);
        estadoView.setEditable(false);

        descView.setLineWrap(true);
        descView.setWrapStyleWord(true);
        DetailPanel.setVisible(true);
        mensajetextArea.setLineWrap(true);
        mensajetextArea.setWrapStyleWord(true);
    }

    private void ListenerAreaCbox() {
        Area area = (Area) AreaCbox.getSelectedItem();
        LoadIncidencias(area.getCodigo());
        if (!incidencias.isEmpty()) {
            incidenciasCbox.removeAllItems();
            incidencias.forEach(incidenciasCbox::addItem);
        } else
            JOptionPane.showMessageDialog(panelPrincipal, "Sin incidencias", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void LoadCbox() {
        AreaCbox.removeAllItems();
        try {
            ArrayList<Area> areas = (ArrayList<Area>) areaDao.getAll();
            if (areas != null) {
                for (Area area : areas) {
                    AreaCbox.addItem(area);
                }
                AreaCbox.setSelectedItem(null);
            } else System.out.println("Sin Areas a cargar");
        } catch (Exception e) {
            System.out.println("Error cargando areas: " + e.getMessage());
        }
    }

    private boolean LoadIncidencias(Long id) {
        incidencias.clear();
        ArrayList<Incidencia> incidenciasBD = (ArrayList<Incidencia>) incidenciaDao.getByArea(id);
        if (incidenciasBD != null) {
            incidencias.addAll(incidenciasBD);
        }

        return !incidencias.isEmpty();
    }


    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        JFrame frame = new JFrame("Inicio - Gestion Campeonato");
        frame.setContentPane(new V_Resolver().getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        if (DockerLauncher()) System.out.println("Docker Launcher exitoso");
        else System.out.println("Problemas con el Docker");
    }

}
