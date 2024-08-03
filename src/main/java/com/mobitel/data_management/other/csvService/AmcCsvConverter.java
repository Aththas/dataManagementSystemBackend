package com.mobitel.data_management.other.csvService;

import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.repository.AmcRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.catalina.manager.JspHelper.formatNumber;
import static org.apache.catalina.manager.StatusTransformer.formatTime;

@Component
@RequiredArgsConstructor
public class AmcCsvConverter {
    private final AmcRepository amcRepository;

    public String amcCsvConverterRow(Amc amc) throws IOException {
        // Export data to CSV
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader("ID", "Name", "OtherField"))) {
            csvPrinter.printRecord(amc.getId()); // Adjust fields as necessary
        }

        // Save CSV to file
        String filePath = Paths.get("path/to/exported-record-" + 1 + ".csv").toString();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(outputStream.toString());
        }
        return filePath;
    }

    public String generateCsvForAmc(String name) throws IOException {
        List<Amc> amcList = amcRepository.findAllByOrderById();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader(
                "User division", "AMC Contract Name", "Existing partner",
                "Initial Cost-USD", "Initial Cost-LKR", "Start", "End",
                "AMC Value-USD 2024", "AMC Value-LKR,2024 Reduced",
                "AMC % upon the purchase price", "category (P, L, H)", "User"
        ))) {
            for (Amc amc : amcList) {
                csvPrinter.printRecord(
                        amc.getUserDivision(), amc.getContractName(), amc.getExistingPartner(),
                        amc.getInitialCostUSD(), amc.getInitialCostLKR(), amc.getStartDate(), amc.getEndDate(),
                        amc.getAmcValueUSD(), amc.getAmcValueLKR(),
                        amc.getAmcPercentageUponPurchasePrice(), amc.getCategory(), amc.getUser().getUsername()
                ); // Adjust fields as necessary
            }
        }

        String baseURL = "http://localhost:8090/CSV-Files/amc/"+ name +".csv";
        String filePath = "src/main/resources/static/CSV-Files/amc/"+ name +".csv";
        Files.write(Paths.get(filePath), outputStream.toByteArray());

        return baseURL;
    }
}
