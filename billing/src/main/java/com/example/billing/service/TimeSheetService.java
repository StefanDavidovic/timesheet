package com.example.billing.service;

import com.example.Empty;
import com.example.TeamMemberServiceGrpc;
import com.example.TimeSheetResponsee;
import com.example.billing.config.BillingConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class TimeSheetService{

    private final BillingConfig billingConfig;

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
            .usePlaintext()
            .build();
    @GrpcClient("grpcClient")
    TeamMemberServiceGrpc.TeamMemberServiceBlockingStub synchronousClient = TeamMemberServiceGrpc.newBlockingStub(channel);

    public Iterator<TimeSheetResponsee> getTimeSheets(){
        var empty = Empty.newBuilder().build();
        var timeSheets = synchronousClient.getTimeSheets(empty);

        return timeSheets;
    }
}
