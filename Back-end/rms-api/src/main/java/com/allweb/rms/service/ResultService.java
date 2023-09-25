package com.allweb.rms.service;

import com.allweb.rms.entity.dto.ResultDTO;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Result;
import com.allweb.rms.exception.InterviewNotFoundException;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ResultRepository;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.allweb.rms.service.elastic.request.InterviewElasticUpdateRequest;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResultService {

  private final ResultRepository resultRepository;
  private final ModelMapper modelMapper;
  private final InterviewRepository interviewRepository;
  private final ElasticIndexingService elasticIndexingService;

  @Autowired
  public ResultService(
      ResultRepository resultRepository,
      ModelMapper modelMapper,
      InterviewRepository interviewRepository,
      ElasticIndexingService elasticIndexingService) {
    this.resultRepository = resultRepository;
    this.modelMapper = modelMapper;
    this.interviewRepository = interviewRepository;
    this.elasticIndexingService = elasticIndexingService;
  }

  // update or create result
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ResultDTO updateResult(ResultDTO resultDTO, int interviewId) {
    ResultDTO result =
        mapperForList().map(resultRepository.findResultByInterviewId(interviewId), ResultDTO.class);
    if (result.getId() != 0) {
      resultDTO.setId(result.getId());
    }
    resultDTO.setInterviewId(interviewId);
    Optional<Interview> resultInterviewObject = this.interviewRepository.findById(interviewId);
    Interview interview =
        resultInterviewObject.orElseThrow(() -> new InterviewNotFoundException(interviewId));
    Result savingResult = convertToEntity(resultDTO);
    savingResult.setInterview(interview);
    Result saveResult = resultRepository.save(savingResult);
    elasticIndexingService.execute(new InterviewElasticUpdateRequest(interview));
    return convertToDTO(saveResult);
  }

  // get result by interview id
  @Transactional(readOnly = true)
  public ResponseEntity<ResultDTO> getResultByInterviewId(int interviewId) {
    ResultDTO result =
        mapperForList().map(resultRepository.findResultByInterviewId(interviewId), ResultDTO.class);
    if (result.getId() == 0) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  public Result convertToEntity(ResultDTO resultDTO) {
    return modelMapper.map(resultDTO, Result.class);
  }

  public ResultDTO convertToDTO(Result result) {
    return modelMapper.map(result, ResultDTO.class);
  }

  public ModelMapper mapperForList() {
    ModelMapper mapper = new ModelMapper();
    mapper
        .getConfiguration()
        .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
        .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);
    return mapper;
  }
}
