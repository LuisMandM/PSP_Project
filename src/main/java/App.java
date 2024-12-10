import GUI.V_Login;

import javax.swing.*;
import java.awt.*;

import static Server.Docker.DockerLauncher;

public class App {
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
        frame.setContentPane(new V_Login(frame).getPanelPrincipal());
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
