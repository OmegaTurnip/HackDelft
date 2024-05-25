package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // setup

//        List<Energy> c1y = new ArrayList<>();
//        List<Energy> c3y = new ArrayList<>();
//        List<Energy> p1y = new ArrayList<>();
//        List<Energy> p3y = new ArrayList<>();
        List<Energy> test = new ArrayList<>();
        List<List<Energy>> data = new ArrayList<>();
        data.add(test);
//        data.add(c1y);
//        data.add(c3y);
//        data.add(p1y);
//        data.add(p3y);
        ObjectMapper mapper = new ObjectMapper();
        String fileNameTest = "src/main/resources/sample.csv";
//        String fileNamec1y = "src/main/resources/consumption-1y.csv";
//        String fileNamec3y = "src/main/resources/consumption-3y.csv";
//        String fileNamep1y = "src/main/resources/production-1y.csv";
//        String fileNamep3y = "src/main/resources/production-3y.csv";
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileNameTest);
//        fileNames.add(fileNamec1y);
//        fileNames.add(fileNamec3y);
//        fileNames.add(fileNamep1y);
//        fileNames.add(fileNamep3y);

        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
//        for (int i = 0; i < 4; i++) {
        for (int i=0; i<1; i++) {
            String fileName = fileNames.get(i);
            try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).withSkipLines(1).build()) {
                List<String[]> rows = csvReader.readAll();
                for (String[] row : rows) {
                    Energy energy = new Energy();
                    energy.setId(Integer.parseInt(row[0]));
                    energy.setStart(row[1]);
                    energy.setEnd(row[2]);
                    List<Energy.Volume> volumes = new ArrayList<>();
                    String jsonString = row[3].replace("'", "\"");
                    try {
                        volumes = mapper.readValue(jsonString, new TypeReference<>(){});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    energy.setVolumes(volumes);

                    data.get(i).add(energy);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }



        calculateDailyAvg(data);
        calculateMonthlyAvg(data);

        // test
        for (List<Energy> file : data) {
            for (Energy energy : file) {
//                System.out.println(energy.getId() + " " + energy.getDailyAvg());
                System.out.println(energy.getId() + " " + energy.getMonthlyAvg());
            }
        }

    }

    private static void calculateMonthlyAvg(List<List<Energy>> data) {
        int lastMonth = 1;
        double sum = 0;
        for (List<Energy> file : data) {
            for (Energy energy : file) {
                Map<YearMonth, Double> monthlyAvg = energy.getMonthlyAvg();
                for (Energy.Volume vol :  energy.getVolumes()) {
                    int currentMonth = Integer.parseInt(vol.getKey().split("-")[1]);
                    if (currentMonth==lastMonth) {
                        sum+=vol.getValue();
                    } else {
                        String date = vol.getKey().split(":")[0];
                        YearMonth ym = YearMonth.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]));
                        monthlyAvg.put(ym, sum/24.0);
                        lastMonth=currentMonth;
                        sum=0;
                    }
                }
            }
        }
    }

    // put date & average on that date as map
    private static void calculateDailyAvg(List<List<Energy>> data) {
        int count = 1;
        double sum = 0;
        for (List<Energy> file : data) {
            for (Energy energy : file) {
                Map<LocalDate, Double> dailyAvg = energy.getDailyAvg();
                for (Energy.Volume vol : energy.getVolumes()) {
                    if (count<24) {
                        sum+=vol.getValue();
                        count++;
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        try {
                            LocalDate date = LocalDate.parse(vol.getKey().split(":")[0], formatter);
                            dailyAvg.put(date, sum/24.0);
                        } catch (DateTimeParseException e) {
                            System.out.println("Error: Unable to parse the date from the string.");
                            e.printStackTrace();
                        }
                        count = 1;
                        sum = 0;
                    }
                }
            }
        }
    }
}