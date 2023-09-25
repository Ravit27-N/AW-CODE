package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.CandidateElasticsearchRequest;
import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.exception.AdvancedSearchBadRequestException;
import com.allweb.rms.repository.elastic.CandidateCustomElasticsearchRepository;
import com.allweb.rms.service.CandidateService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/candidate")
@Slf4j
public class CandidateController {
    CandidateCustomElasticsearchRepository candidateCustomElasticsearchRepository;
    private static final String DEFAULT_SORTING_FIELD = "createdAt";
    private static final String CANDIDATE_FULL_NAME = "firstname";
    private static final String CANDIDATE_GENDER = "gender";
    private static final String CANDIDATE_GPA = "gpa";
    private static final String CANDIDATE_PRIORITY = "priority";
    private static final String CANDIDATE_PHONE = "telephone";
    private static final String[] SUPPORTED_SORT_FIELDS =
            new String[]{
                    CandidateController.CANDIDATE_FULL_NAME,
                    CandidateController.CANDIDATE_GENDER,
                    CandidateController.CANDIDATE_GPA,
                    CandidateController.CANDIDATE_PRIORITY,
                    CandidateController.CANDIDATE_PHONE,
                    CandidateController.DEFAULT_SORTING_FIELD
            };
    private final CandidateService candidateService;
    private final SimpleDateFormat dateFormatter;
    private final String dateFormat;

    public CandidateController(
            CandidateService candidateService,
            @Value("${pattern.date.format}") String applicationDateFormat) {
        this.candidateService = candidateService;
        this.dateFormat = applicationDateFormat;
        this.dateFormatter = new SimpleDateFormat(applicationDateFormat);
    }

    /**
     * Get All Candidates where deleted is false
     *
     * @return List of candidates.
     */
    @Operation(
            operationId = "findAllCandidates",
            description = "Get all candidates",
            tags = {"Candidate"},
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "status",
                            description = "this param is used for search by special status"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "isDeleted",
                            description = "this param is used for get candidates by deleted true or false"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "page size or limit"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortDirection",
                            description = "Direction sort",
                            schema = @Schema(allowableValues = {"desc", "asc"})),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortByField",
                            description = "field name that wanna sort",
                            schema =
                            @Schema(
                                    allowableValues = {
                                            CANDIDATE_FULL_NAME,
                                            CANDIDATE_GENDER,
                                            CANDIDATE_GPA,
                                            CANDIDATE_PRIORITY,
                                            CANDIDATE_PHONE,
                                            DEFAULT_SORTING_FIELD
                                    })),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "filter",
                            description = "filter data on the table"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "filterBy",
                            description = "this param used for filter interview or reminder",
                            schema = @Schema(allowableValues = {"interview", "reminder"})),
            })
    @GetMapping
    public ResponseEntity<EntityResponseHandler<EntityModel<CandidateDTO>>> findAllCandidates(
            @RequestParam(defaultValue = "") String[] filterReminderOrInterview,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(defaultValue = "false") Boolean isDeleted,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = DEFAULT_SORTING_FIELD) String sortByField,
            @RequestParam(required = false) String filter) {
        if (!ArrayUtils.contains(SUPPORTED_SORT_FIELDS, sortByField)) {
            throw new AdvancedSearchBadRequestException(
                    String.format("Sort by \"%s\" is not supported.", sortByField));
        }
        CandidateElasticsearchRequest request =
                CandidateElasticsearchRequest.builder()
                        .isDeleted(isDeleted)
                        .filterBy(filterReminderOrInterview)
                        .filter(filter)
                        .candidateStatus(status)
                        .pageable(
                                PageRequest.of(
                                        page - 1,
                                        pageSize,
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)))
                        .build();
        return ResponseEntity.ok(candidateService.findAllCandidates(request));
    }

    /**
     * Get One Candidate By Id
     *
     * @param id Candidate's id.
     * @return Candidate.
     */
    @Operation(
            operationId = "getCandidateById",
            description = "Get candidate by id this function use for response when update candidate",
            tags = {"Candidate"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Candidate ID")})
    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable("id") int id) {
        return new ResponseEntity<>(candidateService.getCandidateById(id), HttpStatus.OK);
    }

    /**
     * View candidate by id
     *
     * @param id Candidate's id.
     * @return Candidate.
     */
    @Operation(
            operationId = "viewCandidateById",
            description = "View candidate by id this function use for response when update candidate",
            tags = {"Candidate"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Candidate ID")})
    @GetMapping("/{id}/view")
    public ResponseEntity<EntityModel<CandidateDTO>> viewCandidateById(@PathVariable("id") int id) {
        return new ResponseEntity<>(candidateService.viewCandidateById(id), HttpStatus.OK);
    }

    /**
     * Create new Candidate
     *
     * @param candidate Candidate's detail.
     * @return A successful created candidate.
     */
    @Operation(
            operationId = "createCandidates",
            description = "create new candidate",
            tags = {"Candidate"})
    @PostMapping
    public ResponseEntity<CandidateDTO> createCandidate(@RequestBody @Valid CandidateDTO candidate) {
        return new ResponseEntity<>(candidateService.createCandidate(candidate), HttpStatus.OK);
    }

    /**
     * Update a Candidate
     *
     * @param candidate Updating candidate's details.
     * @return The successful updated candidate.
     */
    @Operation(
            operationId = "updateCandidates",
            description = "update a candidate",
            tags = {"Candidate"})
    @PutMapping
    public ResponseEntity<CandidateDTO> updateCandidate(@RequestBody @Valid CandidateDTO candidate) {
        return new ResponseEntity<>(candidateService.updateCandidate(candidate), HttpStatus.OK);
    }

    @Operation(
            operationId = "deleteCandidate",
            description = "delete a candidate just update the field isDelete",
            tags = {"Candidate"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Candidate ID")})
    @PatchMapping("/{id}/delete/{isDelete}")
    public ResponseEntity<CandidateDTO> deleteCandidate(
            @PathVariable("id") int id, @PathVariable("isDelete") boolean isDelete) {
        candidateService.deleteCandidate(id, isDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            operationId = "updateStatusCandidate",
            description = "update statusId candidate",
            tags = {"Candidate"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Candidate ID")})
    @PatchMapping("{id}/status/{statusId}")
    public ResponseEntity<CandidateDTO> updateStatusCandidate(
            @PathVariable("id") int id, @PathVariable("statusId") int statusId) {
        return new ResponseEntity<>(
                candidateService.updateStatusCandidate(id, statusId), HttpStatus.OK);
    }

    @Operation(
            operationId = "reportCandidates",
            description = "report all candidate search by last interview",
            tags = {"Candidate"},
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "from",
                            description = "start date",
                            example = "dd-MM-yyyy"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "to",
                            description = "end date",
                            example = "dd-MM-yyyy"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "pageSize",
                            description = "page size or limit record"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortDirection",
                            description = "Direction sort or type of order",
                            schema = @Schema(allowableValues = {"desc", "asc"})),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortByField",
                            description = "field name that wanna sort",
                            schema =
                            @Schema(
                                    allowableValues = {
                                            CANDIDATE_FULL_NAME,
                                            CANDIDATE_GENDER,
                                            CANDIDATE_GPA,
                                            CANDIDATE_PRIORITY,
                                            CANDIDATE_PHONE,
                                            DEFAULT_SORTING_FIELD
                                    }))
            })
    @GetMapping("/report")
    public EntityResponseHandler<EntityModel<CandidateDTO>> reportCandidates(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = DEFAULT_SORTING_FIELD) String sortByField,
            @RequestParam(required = false) String filter) {
        if (!ArrayUtils.contains(SUPPORTED_SORT_FIELDS, sortByField)) {
            throw new AdvancedSearchBadRequestException(
                    String.format("Sort by \"%s\" is not supported.", sortByField));
        }
        Date dateFrom;
        Date dateTo;
        try {
            dateFrom = this.dateFormatter.parse(from);
            dateTo = this.dateFormatter.parse(to);
        } catch (ParseException parseException) {
            throw new AdvancedSearchBadRequestException(
                    String.format("Accepted date format, \"%s\".", this.dateFormat));
        }
        if (dateFrom.after(dateTo)) {
            throw new AdvancedSearchBadRequestException("Start date must be before the end date.");
        }
        Pageable pageable =
                PageRequest.of(
                        page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
        CandidateElasticsearchRequest request =
                CandidateElasticsearchRequest.builder()
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .filter(filter)
                        .pageable(pageable)
                        .build();
        return candidateService.reportCandidates(request);
    }

    @Operation(
            operationId = "findAllByAdvancedSearch",
            description = "report all candidate search by last interview",
            tags = {"Candidate"},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "name", description = "name of candidate"),
                    @Parameter(in = ParameterIn.QUERY, name = "from", description = "university's name"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "gender",
                            description = "gender",
                            schema = @Schema(allowableValues = {"Male", "Female"})),
                    @Parameter(in = ParameterIn.QUERY, name = "gpa", description = "gpa of candidates"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "position",
                            description = "position that candidate apply or title of interview"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "pageSize",
                            description = "page size or limit record"),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortDirection",
                            description = "Direction sort or type of order",
                            schema = @Schema(allowableValues = {"desc", "asc"})),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sortByField",
                            description = "field name that wanna sort",
                            schema =
                            @Schema(
                                    allowableValues = {
                                            CANDIDATE_FULL_NAME,
                                            CANDIDATE_GENDER,
                                            CANDIDATE_GPA,
                                            CANDIDATE_PRIORITY,
                                            CANDIDATE_PHONE,
                                            DEFAULT_SORTING_FIELD
                                    }))
            })
    @GetMapping("/advancedSearch")
    public ResponseEntity<EntityResponseHandler<EntityModel<CandidateDTO>>> findAllByAdvancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") float gpa,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = DEFAULT_SORTING_FIELD) String sortByField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        CandidateElasticsearchRequest request =
                CandidateElasticsearchRequest.builder()
                        .candidateName(name)
                        .university(from)
                        .gender(gender)
                        .gpa(gpa)
                        .position(position)
                        .pageable(
                                PageRequest.of(
                                        page - 1,
                                        pageSize,
                                        Sort.by(Sort.Direction.fromString(sortDirection), sortByField)))
                        .build();
        return ResponseEntity.ok(candidateService.findAllCandidates(request));
    }

    @Operation(
            operationId = "validateEmail",
            description = "validate email by email",
            tags = {"Candidate"},
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "email", description = "Candidate email")
            })
    @GetMapping("/validateEmail/{email}")
    public Long validateEmail(@PathVariable String email) {
        return candidateService.validateEmailCandidate(email);
    }

    @Operation(
            operationId = "findAllCandidatesOnSelectBox",
            description = "used for show data on the select box",
            tags = {"Candidate"})
    @GetMapping("/selectBox")
    public EntityResponseHandler<Map<String, Object>> findAllCandidatesOnSelectBox(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return candidateService.findAllCandidatesOnSelectBox(filter, page, pageSize);
    }

    @Operation(
            operationId = "uploadProfileCandidate",
            description = "upload profile candidate",
            tags = {"Candidate Upload"})
    @PostMapping(value = "/profile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadProfileCandidate(
            @RequestPart MultipartFile filename) {
        return new ResponseEntity<>(candidateService.uploadProfile(filename), HttpStatus.OK);
    }

    @Operation(
            operationId = "uploadAttachment",
            description = "upload attachment file candidate or file cv",
            tags = {"Candidate Upload"})
    @PostMapping(value = "/attach/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @RequestPart MultipartFile[] filenames) {
        return new ResponseEntity<>(candidateService.uploadAttachment(filenames), HttpStatus.OK);
    }

    @Operation(
            operationId = "uploadAttachmentOnUpdate",
            description = "upload attachment file candidate or file cv, when update candidate",
            tags = {"Candidate Upload"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "candidate id")})
    @PutMapping(value = "/{id}/attach/onUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAttachmentOnUpdate(
            @PathVariable int id, @RequestPart MultipartFile[] filenames) {
        return new ResponseEntity<>(
                candidateService.uploadAttachmentOnUpdate(id, filenames), HttpStatus.OK);
    }

    @Operation(
            operationId = "getFilesAttachment",
            description = "get all file attach by id candidate",
            tags = {"Candidate Upload"},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "candidate id")})
    @GetMapping("/{id}/attach")
    public Map<String, Object> getFilesAttachment(@PathVariable int id) {
        return candidateService.getFilesAttachment(id);
    }

    @Operation(
            operationId = "loadFile",
            description = "load or view content of file",
            tags = {"Candidate Upload"},
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "candidate id"),
                    @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
            })
    @SneakyThrows
    @GetMapping("/{id}/view/{filename}")
    public ResponseEntity<Resource> loadFile(
            @PathVariable String id, @PathVariable String filename, HttpServletRequest request) {
        Resource resource = candidateService.loadFile(id, filename);
        String contentType =
                request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream"; // unknown binary file
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    @Operation(
            operationId = "remove",
            description = "remove file from candidate by id",
            tags = {"Candidate Upload"},
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "candidate id"),
                    @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
            })
    @DeleteMapping("/{id}/remove/{filename}")
    public ResponseEntity<Void> removeAttachFile(
            @PathVariable int id, @PathVariable String filename) {
        candidateService.removeFile(id, filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            operationId = "downloadFile",
            description = "download or view content of file",
            tags = {"Candidate Upload"},
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "candidate id"),
                    @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
            })
    @SneakyThrows
    @GetMapping("/{id}/download/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String id, @PathVariable String filename, HttpServletRequest request) {
        Resource resource = candidateService.loadFile(id, filename);
        String contentType =
                request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream"; // unknown binary file
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(
            operationId = "hardDeleteById",
            description = "Hard delete candidate by id",
            tags = {"Candidate"},
            parameters = @Parameter(name = "id", in = ParameterIn.PATH))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable("id") int id) {
        this.candidateService.hardDelete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/candidateId")
    public EntityResponseHandler<Map<String, Object>> findAllCandidateByIdShowInDemand(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return candidateService.findAllCandidateByIdShowInDemand(page, pageSize);
    }

    @GetMapping("/advance-report")
    public ResponseEntity<EntityResponseHandler<Map<String, Object>>> findAllCandidateAdvanceReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "desc")
            String sortDirection,
            @RequestParam(value = "sortByField", required = false, defaultValue = "created_at")
            String sortByField) {
        if (from.after(to)) {
            throw new AdvancedSearchBadRequestException("Start date must be before the end date.");
        }
        return new ResponseEntity<>(
                candidateService.getAllCandidateAdvanceReport(
                        from, to, position, page, pageSize, sortDirection, sortByField),
                HttpStatus.OK);
    }
}
