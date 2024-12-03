package GUI;

import dao.UsuarioDaoImpl;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class V_Register {
    private JPanel panelPrincipal;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton infoUser;
    private JButton infoPassword;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton infoDate;

    private UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();

    //regex usuario [a-zA-Z\d_$@ñÑ]{8}
    //regex password [a-zA-Z\d_$@ñÑ]{12}
    //regex email public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
    //    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //
    //public static boolean validate(String emailStr) {
    //        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    //        return matcher.matches();
    //}


    public V_Register(JFrame frame) {


        infoPassword.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                    La contraseña debe contener minimo 12 caracteres
                    puede incluir letras, números y los caracteres especiales
                    '_' , '$' , '@'
                    """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        infoUser.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                    El usuario debe contener minimo 12 caracteres
                    puede incluir letras, números y los caracteres especiales
                    '_' , '$' , '@'
                    """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        infoDate.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                            La fecha de nacimiento debe seguir
                            el formato dd/mm/yyyy
                            """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelarButton.addActionListener(e -> frame.dispose());

    }

    private Date CrearFecha(String fecha) {
        SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return fechaFormat.parse(fecha);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean ValidarTiempo(String tiempo) {
        String regex = "^\\d{2}:[0-5]\\d\\.\\d{3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tiempo);
        return matcher.matches();
    }
}
