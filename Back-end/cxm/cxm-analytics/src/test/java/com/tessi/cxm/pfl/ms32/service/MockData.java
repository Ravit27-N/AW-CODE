package com.tessi.cxm.pfl.ms32.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import com.tessi.cxm.pfl.ms32.entity.projection.FlowDocumentReportProjection;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public final class MockData {
	
	public static List<String> PostalParamter = FlowDocumentSubChannel.postalSubChannels();
	public static List<String> EmailParamter = List.of(FlowDocumentSubChannel.EMAIL.getValue());
	public static List<String> SMSParamter = List.of(FlowDocumentSubChannel.SMS.getValue());

	public static UserInfoResponse mockUserInfoResponse;
	public static UserPrivilegeDetails mockUserPrivilegeDetails;
	public static UserFilterPreferenceDto mockUserFilterPreferenceDto;
	public static UserInfoResponse mockAdminInfoResponse;
	public static List<FlowDocumentReportProjection> mockListFlowDocumentReport;
	public static double firstReportPercentageValue = 18.2927;

	static {
		mockUserInfoResponse = UserInfoResponse.builder()
		.platformAdmin(true).superAdmin(false).build();

		mockUserPrivilegeDetails = UserPrivilegeDetails.builder()
		.relatedOwners(List.of(1L, 2L)).build();

		mockUserFilterPreferenceDto = UserFilterPreferenceDto.builder()
		.selectDateType("4")
		.customEndDate(new Date())
		.customStartDate(new Date())
		.build();

		mockAdminInfoResponse = UserInfoResponse.builder()
		.platformAdmin(false).superAdmin(true).build();
		

		mockListFlowDocumentReport = new ArrayList<FlowDocumentReportProjection>();

		mockListFlowDocumentReport.add(
		FlowDocumentReportChannel.builder()
		.status(FlowDocumentStatus.TO_VALIDATE.getValue())
		.total(5)
		.build());

		mockListFlowDocumentReport.add(
		FlowDocumentReportChannel.builder()
		.status(FlowDocumentStatus.IN_PROGRESS.getValue())
		.total(30)
		.build());

		mockListFlowDocumentReport.add(
		FlowDocumentReportChannel.builder()
		.status(FlowDocumentStatus.SCHEDULED.getValue())
		.total(10)
		.build());

		mockListFlowDocumentReport.add(
		FlowDocumentReportChannel.builder()
		.status(FlowDocumentStatus.COMPLETED.getValue())
		.total(20)
		.build());

		mockListFlowDocumentReport.add(
		FlowDocumentReportChannel.builder()
		.status(FlowDocumentStatus.COMPLETED.getValue())
		.total(15)
		.build());

		mockListFlowDocumentReport.add(
			FlowDocumentReportChannel.builder()
			.status(FlowDocumentStatus.IN_ERROR.getValue())
			.total(2)
			.build());

	}


	@Builder
	@Getter
	@Setter
	static class FlowDocumentReportChannel implements FlowDocumentReportProjection {

		private String status;
		private long total;

		@Override
		public String getStatus() {
			return this.status;
		}

		@Override
		public Long getTotal() {
			return this.total;
		}

	}

}
