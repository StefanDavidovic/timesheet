package com.example.employees.serverGrpc;

import com.example.TeamMemberRequest;
import com.example.TeamMemberResponse;
import com.example.TeamMemberServiceGrpc;
import com.example.employees.model.Employee;
import com.example.employees.repository.EmployeeRepo;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class TeamMemberImpl extends TeamMemberServiceGrpc.TeamMemberServiceImplBase {

    private final EmployeeRepo employeeRepo;
    @Override
    public void getTeamMemberById(TeamMemberRequest request, StreamObserver<TeamMemberResponse> responseObserver) {
        Employee employee = employeeRepo.findByEmail(String.valueOf(request.getEmail().strip()));
        TeamMemberResponse teamMember = TeamMemberResponse.newBuilder().setEmail(employee.getEmail())
                .setName(employee.getName())
                .setId(employee.getId())
                .build();

        responseObserver.onNext(teamMember);
        responseObserver.onCompleted();
    }
}
