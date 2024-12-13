package GUI;

import Models.Incidencia;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class IncidenciasTableModel extends AbstractTableModel {
    private String[] columns = {"Codigo", "Tipo Petición", "Respuesta(Horas)"};
    private ArrayList<Incidencia> incidencias;

    public IncidenciasTableModel(ArrayList<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public IncidenciasTableModel() {
        this.incidencias = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return incidencias.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Incidencia incidencia = incidencias.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return incidencia.getCodIncidencia();
            case 1:
                return incidencia.getArea();
            case 2:
                return (incidencia.getTiempo() / (60 * 60 * 1000));
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
}
