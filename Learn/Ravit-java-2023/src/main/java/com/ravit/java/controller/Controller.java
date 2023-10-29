package com.ravit.java.controller;


import com.ravit.java.model.Candidate;
import com.ravit.java.repository.CandidateRepository;
import com.ravit.java.restclient.RavitTwoFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  private final CandidateRepository candidateRepository;
  private final RavitTwoFeignClient ravitTwoFeignClient;

  public Controller(CandidateRepository candidateRepository,
      RavitTwoFeignClient ravitTwoFeignClient) {
    this.candidateRepository = candidateRepository;
    this.ravitTwoFeignClient = ravitTwoFeignClient;
  }

  @GetMapping
  public String helloRavit(){
    return "Hello Ravit";
  }

  @GetMapping("/user")
  public String helloUser(){
    return "Hello USER";
  }

  @GetMapping("/ravit-two")
  public String helloRavitTwo(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
    return ravitTwoFeignClient.getRavit(authorization);
  }

  @GetMapping("/create")
  public ResponseEntity<String> createCandidate() {

    Candidate can = new Candidate();
    can.setAge(1);
    can.setName("Test1");

    candidateRepository.save(can);

    return new ResponseEntity<>("Success",HttpStatus.OK);
  }

}
