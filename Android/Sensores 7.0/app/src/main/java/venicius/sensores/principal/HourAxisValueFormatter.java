package venicius.sensores.principal;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by venicius on 27/10/17.
 */

public class HourAxisValueFormatter implements IAxisValueFormatter {

    protected String[] mMonths = new String[]{
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    private BarLineChartBase<?> chart;

    public HourAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int minutos = (int) value;

        String horaIntera = String.valueOf(minutos / 60);
        String restoHora = String.valueOf(minutos % 60);

        if((minutos%60) < 10) {
            return horaIntera + ":0" + restoHora ;
        } else {
            return horaIntera + ":" + restoHora ;
        }


    }


}

