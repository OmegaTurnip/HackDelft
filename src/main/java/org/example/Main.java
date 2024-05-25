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
        String fileNameTest = "src/main/resources/consumption-1y.csv";
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
        for (int i = 0; i < 1; i++) {
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
                        volumes = mapper.readValue(jsonString, new TypeReference<>() {
                        });
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


        averageHourlyConsumptionPerDay(data);
        averageDailyConsumptionPerMonth(data);
        derivativeInfoPerDay(data);
//        derivativeInfoPerMonth(data);

        // test
        for (List<Energy> file : data) {
            for (Energy energy : file) {
//                System.out.println(energy.getId() + " " + energy.getDailyAvg());
//                System.out.println(energy.getId() + " " + energy.getMonthlyAvg());
//                System.out.println(energy + " " + energy.getDerivativeInfoDaily());
            }
        }

    }

//    private static void derivativeInfoPerMonth(List<List<Energy>> data) {
//        double sum = 0;
//        for (List<Energy> file : data) {
//            for (Energy energy : file) {
//                double[] values = new double[31];
//                int count = 1;
//                for (Energy.Volume vol : energy.getVolumes()) {
//                    String date = vol.getKey().split(":")[0];
//                    YearMonth ym = YearMonth.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]));
//                    int lengthOfMonth = ym.lengthOfMonth();
//                    if (Integer.parseInt(date.split("-")[2]) == lengthOfMonth && Integer.parseInt(vol.getKey().split(":")[1]) == 23) {
//                        values[count - 1] = vol.getValue();
//                        energy.
//                        energy.getMonthlyAvg().put(ym, sum / lengthOfMonth);
//                        sum = 0;
//                    }
//                }
//            }
//        }
//    }

    private static void derivativeInfoPerDay(List<List<Energy>> data) {
        for (List<Energy> file : data) {
            for (Energy energy : file) {
                double[] values = new double[24];
                int count = 1;
                for (Energy.Volume vol : energy.getVolumes()) {
                    values[count - 1] = vol.getValue();
                    if (count != 23) {
                        count++;
                    } else {
                        double[] derivatives = new double[23];
                        double h = 1; // x values increment by 1

                        // Calculate the derivative for interior points using central difference
                        for (int i = 1; i < 22; i++) {
                            derivatives[i] = (values[i + 1] - values[i - 1]) / (2 * h);
                        }

                        // Handle boundaries using forward and backward difference
                        derivatives[0] = (values[1] - values[0]) / h;
                        derivatives[22] = (values[22] - values[21]) / h;

                        int maxHour = -1;
                        double maxVal = -1;
                        int minHour = -1;
                        double minVal = Double.MAX_VALUE;
                        int zeroHour = -1;
                        double zeroVal = Double.MAX_VALUE;
                        for (int i=0; i<23; i++) {
                            double curr = derivatives[i];
                            if (curr > maxVal) {
                                maxHour = i+1;
                                maxVal = curr;
                            } else if (curr < minVal) {
                                minHour = i+1;
                                minVal = curr;
                            } else if (Math.abs(curr) < zeroVal) {
                                zeroHour = i+1;
                                zeroVal = curr;
                            }
                        }

                        Map<String, HourValue> info = new HashMap<>();
                        info.put("max deriv",  new HourValue(maxHour, maxVal));
                        info.put("min deriv", new HourValue(minHour, minVal));
                        info.put("zero deriv", new HourValue(zeroHour, 0));

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date = LocalDate.parse(vol.getKey().split(":")[0], formatter);
                        energy.getDerivativeInfoDaily().put(date, info);
                        count = 1;
                    }
                }
            }
        }
    }

private static void averageDailyConsumptionPerMonth(List<List<Energy>> data) {
    double sum = 0;
    for (List<Energy> file : data) {
        for (Energy energy : file) {
            for (Energy.Volume vol : energy.getVolumes()) {
                String date = vol.getKey().split(":")[0];
                YearMonth ym = YearMonth.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]));
                int lengthOfMonth = ym.lengthOfMonth();
                sum += vol.getValue();
                if (Integer.parseInt(date.split("-")[2]) == lengthOfMonth && Integer.parseInt(vol.getKey().split(":")[1]) == 23) {
                    energy.getMonthlyAvg().put(ym, sum / lengthOfMonth);
                    sum = 0;
                }
            }
        }
    }
}

// put date & average on that date as map
private static void averageHourlyConsumptionPerDay(List<List<Energy>> data) {
    int count = 1;
    double sum = 0;
    for (List<Energy> file : data) {
        for (Energy energy : file) {
            for (Energy.Volume vol : energy.getVolumes()) {
                sum += vol.getValue();
                if (count != 23) {
                    count++;
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    try {
                        LocalDate date = LocalDate.parse(vol.getKey().split(":")[0], formatter);
                        energy.getDailyAvg().put(date, sum / 24.0);
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