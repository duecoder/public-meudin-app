package br.com.due.meudin.util;

import jakarta.validation.constraints.NotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalMethods {

    public static String formatDate(LocalDate date, String format) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return date.format(formatter);
        }
        return "-";
    }

    // Método abaixo é para pegar a competência ao criar a Invoice
    // (Cria a competência sempre sendo no mês atual, mas com o dia sendo o paymentLimitDay do card)
    public static LocalDate getCardCompetenceByPaymentDay(int paymentDay) {
        // Obter a data atual
        Date date = new Date();
        // Criar um objeto Calendar e definir a data atual
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Ajustar para o dia do pagamento do cartão
        calendar.set(Calendar.DAY_OF_MONTH, paymentDay);
        // Obter a data ajustada
        Date monthFirstDay = calendar.getTime();
        // Convertendo Date para Instant
        Instant instant = monthFirstDay.toInstant();
        // Convertendo Instant para LocalDate
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate;
    }

    public static List<Long> convertStringToLongList(String ids) {
        // Divida a string pelos separadores, como vírgula, e converta para uma lista de Long
        return Stream.of(ids.replaceAll("\\[|\\]", "").split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @NotNull
    public static LocalDate getCompetence(LocalDate dateToCompare, int currentDay, int paymentDayInt) {
        LocalDate competence = null;
        // Caso dia atual seja maior ou igual ao dia de pagamento, significa que a current invoice é
        // a do mês subsequente
        if (currentDay >= paymentDayInt) {
            competence = dateToCompare.plusMonths(1).withDayOfMonth(paymentDayInt);
        } else {
            competence = dateToCompare.withDayOfMonth(paymentDayInt);
        }
        return competence;
    }

    @NotNull
    public static LocalDate getClosingDate(LocalDate dateToCompare, int closingDayInt, int paymentDayInt) {
        LocalDate closingDate = null;
        int closingYear = dateToCompare.getYear();
        // Pego a data do dia de fechamento - verifico se o closing é maior que o limit
        // Caso sim, significa que o closing é no mês anterior
        if (closingDayInt > paymentDayInt) {
            closingYear = dateToCompare
                .getMonth()
                .getValue() == 1
                ? dateToCompare.getYear() -1
                : dateToCompare.getYear();
            closingDate = LocalDate.of(closingYear, dateToCompare.getMonth().getValue() - 1, closingDayInt);
        } else {
            int finalMonth = dateToCompare.getMonth().getValue() == 12 ? 1 : dateToCompare.getMonth().getValue() + 1;
            int finalYear = finalMonth == 1 ? closingYear + 1 : closingYear;
            closingDate = LocalDate.of(finalYear, finalMonth, closingDayInt);
        }
        return closingDate;
    }
}
