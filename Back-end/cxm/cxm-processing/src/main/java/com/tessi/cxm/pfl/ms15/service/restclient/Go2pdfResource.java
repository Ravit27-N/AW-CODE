package com.tessi.cxm.pfl.ms15.service.restclient;

import com.tessi.cxm.pfl.ms15.model.AnalyseModelResponse;
import com.tessi.cxm.pfl.ms15.model.AnalyseRequest;
import com.tessi.cxm.pfl.ms15.model.AnalyseResponse;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import java.util.List;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(FeignClientConstants.CXM_GO2PDF)
public interface Go2pdfResource {

  @PostMapping("/api/v1/go2pdf/analyse-model")
  AnalyseModelResponse analyseModel(
      @Valid @RequestBody AnalyseRequest analyseRequest,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
      String authorizationHeader);

  @PostMapping("/api/v1/go2pdf/analyse")
  List<AnalyseResponse> analyse(
      @Valid @RequestBody AnalyseRequest analyseRequest,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
      String authorizationHeader);
}
