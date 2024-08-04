package com.mobitel.data_management.other.csvService;

import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.repository.PoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PoCSVConverter {
    private final PoRepository poRepository;

    public String generateCsvForPo(String name) throws IOException {
        List<Po> poList = poRepository.findAllByOrderById();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader(
                "PO_NUMBER", "CREATION_DATE", "PO_CREATION_DATE",
                "PO_TYPE", "VENDOR_NAME", "VENDOR_SITE_CODE",
                "PO_DESCRIPTION", "APPROVAL_STATUS", "CURRENCY",
                "AMOUNT", "MATCHED_AMOUNT", "BUYER_NAME",
                "CLOSURE_STATUS", "PR_NUMBER", "PR_CREATION_DATE",
                "REQUISITION_HEADER_ID", "REQUESTER_NAME", "REQUESTER_EMP_NUM",
                "LINE_NUM", "ITEM_CODE", "ITEM_DESCRIPTION",
                "LINE_ITEM_DESCRIPTION", "UNIT", "UNIT_PRICE",
                "QUANTITY", "LINE_AMOUNT", "BUDGET_ACCOUNT",
                "SEGMENT6_DESC", "P_DELIVER_TO_PERSON_ID", "P_PO_DATE",
                "DEPARTMENT", "USER"
        ))) {
            for (Po po : poList) {
                csvPrinter.printRecord(
                        po.getPoNumber(), po.getCreationDate(), po.getPoCreationDate(),
                        po.getPoType(), po.getVendorName(), po.getVendorSiteCode(),
                        po.getPoDescription(), po.getApprovalStatus(), po.getCurrency(),
                        po.getAmount(), po.getMatchedAmount(), po.getBuyerName(),
                        po.getClosureStatus(), po.getPrNumber(), po.getPrCreationDate(),
                        po.getRequisitionHeaderId(), po.getRequesterName(), po.getRequesterEmpNum(),
                        po.getLineNum(), po.getItemCode(), po.getItemDescription(),
                        po.getLineItemDescription(), po.getUnit(), po.getUnitPrice(),
                        po.getQuantity(), po.getLineAmount(), po.getBudgetAccount(),
                        po.getSegment6Desc(), po.getPurchaseDeliverToPersonId(), po.getPurchasePoDate(),
                        po.getDepartment(), po.getUser().getUsername()
                ); // Adjust fields as necessary
            }
        }

        String baseURL = "http://localhost:8090/CSV-Files/po/"+ name +".csv";
        String filePath = "src/main/resources/static/CSV-Files/po/"+ name +".csv";
        Files.write(Paths.get(filePath), outputStream.toByteArray());

        return baseURL;
    }
}
