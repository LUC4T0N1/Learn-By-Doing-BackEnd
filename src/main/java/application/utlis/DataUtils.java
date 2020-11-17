package application.utlis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DataUtils {
    private DataUtils() {}

    public static Date converterParaDate(String dataAsStr) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dataAsStr != null ? df.parse(dataAsStr) : null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date dateParaDateFormatada(Date dataAgora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataAgoraString = sdf.format(dataAgora);
        return DataUtils.converterParaDate(dataAgoraString);
    }

    public static String converterParaString(Date data) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return data != null ? df.format(data) : null;
    }
}
