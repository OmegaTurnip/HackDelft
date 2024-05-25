package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Energy> c1y = new ArrayList<>();
        List<Energy> c3y = new ArrayList<>();
        List<Energy> p1y = new ArrayList<>();
        List<Energy> p3y = new ArrayList<>();
        List<List<Energy>> data = new ArrayList<>();
        data.add(c1y);
        data.add(c3y);
        data.add(p1y);
        data.add(p3y);
        ObjectMapper mapper = new ObjectMapper();
        String fileNamec1y = "src/main/resources/consumption-1y.csv";
        String fileNamec3y = "src/main/resources/consumption-3y.csv";
        String fileNamep1y = "src/main/resources/production-1y.csv";
        String fileNamep3y = "src/main/resources/production-3y.csv";
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileNamec1y);
        fileNames.add(fileNamec3y);
        fileNames.add(fileNamep1y);
        fileNames.add(fileNamep3y);

        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
        for (int i = 0; i < 4; i++) {
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

        System.out.println("testing");


    }
}