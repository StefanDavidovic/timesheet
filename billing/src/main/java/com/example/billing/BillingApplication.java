package com.example.billing;

import com.example.billing.config.BillingConfig;
import com.example.billing.service.BillingExport;
import com.example.billing.service.TimeSheetService;
import com.fasterxml.jackson.databind.JsonNode;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

@SpringBootApplication
public class BillingApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext ctx = SpringApplication.run(BillingApplication.class, args);
		BillingConfig configuration = ctx.getBean(BillingConfig.class);
		TimeSheetService service = new TimeSheetService(configuration);
		BillingExport billingExport1 = new BillingExport(service,configuration);
		billingExport1.exportBill();
		System.exit(1);
	}
}
