package com.example.internship.serverGrpc;


import com.example.*;
import com.example.internship.model.TimeSheet;
import com.example.internship.repository.TimeSheetRepo;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;
import java.util.List;

@GRpcService
@RequiredArgsConstructor
public class TimeSheetsImpl extends TeamMemberServiceGrpc.TeamMemberServiceImplBase {

    private final TimeSheetRepo timeSheetRepo;

    @Override
    public void getTimeSheets(Empty request, StreamObserver<TimeSheetResponse> responseObserver) {
        List<TimeSheet> timeSheets = timeSheetRepo.findAll();
        timeSheets.stream().forEach(ts -> {

            Client client = Client.newBuilder().setId(ts.getClient().getId())
                    .setName(ts.getClient().getName())
                    .setAddress(ts.getClient().getAddress())
                    .setCity(ts.getClient().getCity())
                    .setZip(ts.getClient().getZip())
                    .setCountry(ts.getClient().getCountry())
                    .setVersion(ts.getClient().getVersion())
                    .build();

            TeamMember teamMember = TeamMember.newBuilder()
                    .setId(ts.getProject().getTeamMember().getId())
                    .setUsername(ts.getProject().getTeamMember().getUsername())
                    .setHoursPerWeek(ts.getProject().getTeamMember().getHoursPerWeek())
                    .setEmail(ts.getProject().getTeamMember().getEmail())
                    .setPassword(ts.getProject().getTeamMember().getPassword())
                    .setStatus(ts.getProject().getTeamMember().isStatus())
                    .setArchive(ts.getProject().getTeamMember().isArchive())
                    .setRole(ts.getProject().getTeamMember().getRole().toString())
                    .setVersion(ts.getProject().getTeamMember().getVersion())
                    .build();

            Project project = Project.newBuilder()
                    .setId(ts.getProject().getId())
                    .setName(ts.getProject().getName())
                    .setDescription(ts.getProject().getDescription())
                    .setStatus(ts.getProject().isStatus())
                    .setArchive(ts.getProject().isArchive())
                    .setCustomer(client)
                    .setTeamMember(teamMember)
                    .setVersion(ts.getProject().getVersion())
                    .build();

            Category category = Category.newBuilder()
                    .setId(ts.getCategory().getId())
                    .setName(ts.getCategory().getName())
                    .setVersion(ts.getCategory().getVersion())
                    .build();

            TimeSheetResponse timeSheetResponse = TimeSheetResponse.newBuilder()
                    .setId(ts.getId())
                    .setDate(ts.getDate().toString())
                    .setDescription(ts.getDescription())
                    .setTime(ts.getTime())
                    .setOvertime(ts.getOvertime())
                    .setClient(client)
                    .setProject(project)
                    .setVersion(ts.getVersion())
                    .setCategory(category)
                    .build();

            responseObserver.onNext(timeSheetResponse);
        });
        responseObserver.onCompleted();
    }
}
