package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

/**
 * The specification for filtering and matching FlowDocument by {@link FlowDocument} properties.
 *
 * @author Piseth Khon
 * @since 10/28/21
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowDocumentSpecification {

  private static final String STRING_CONCAT_FORMAT = "%s%s%s";

  /**
   * Handle the specification for match {@link FlowDocument} by {@link FlowTraceability} id.
   *
   * @param id refer to id of flow.
   */
  public static Specification<FlowDocument> equalToFlowId(long id) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(FlowDocument_.flowTraceability).get(FlowTraceability_.id), id);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param status refer to status of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> equalStatus(String status) {
    return SpecificationUtils.getEqualString(FlowDocument_.status, status);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param channel refer to channel of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> equalChannel(String channel) {
    return SpecificationUtils.getEqualString(FlowDocument_.channel, channel);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param subChannel refer to sub-channel of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> equalSubChannel(String subChannel) {
    return SpecificationUtils.getEqualString(FlowDocument_.subChannel, subChannel);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param start refer to createdAt of {@link FlowDocument}.
   * @param end refer to createdAt of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> betweenOrEqualCreatedDate(String start, String end) {
    return SpecificationUtils.betweenOrEqual(BaseEntity_.createdAt, start, end);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param start refer to status date of {@link FlowDocument}.
   * @param end refer to status date of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> betweenOrEqualStatusDate(String start, String end) {
    return SpecificationUtils.betweenOrEqual(FlowDocument_.dateStatus, start, end);
  }

  /**
   * Handle the specification for match {@link FlowDocument}.
   *
   * @param document refer to document of {@link FlowDocument}.
   */
  public static Specification<FlowDocument> containDocumentName(String document) {
    return SpecificationUtils.getFilterString(FlowDocument_.document, document);
  }

  /**
   * Handle the specification for match {@link FlowDocument} by flow name of {@link
   * FlowTraceability}.
   *
   * @param flowName refer to name of {@link FlowTraceability}.
   * @return the match condition of {@link FlowDocument}
   */
  public static Specification<FlowDocument> containFlowName(String flowName) {
    return (root, query, cb) -> {
      final Path<FlowTraceability> flowTraceability = root.get(FlowDocument_.FLOW_TRACEABILITY);
      return cb.like(
          flowTraceability.get(FlowTraceability_.FLOW_NAME),
          String.format(STRING_CONCAT_FORMAT, "%", flowName, "%"));
    };
  }

  public static Specification<FlowDocument> containFlowNameOrDocumentName(String filter) {
    return (root, query, cb) -> {
      final Path<FlowTraceability> flowTraceability = root.get(FlowDocument_.FLOW_TRACEABILITY);
      return cb.or(
          cb.like(
              flowTraceability.get(FlowTraceability_.FLOW_NAME),
              String.format(STRING_CONCAT_FORMAT, "%", filter, "%")),
          cb.like(
              root.get(FlowDocument_.DOCUMENT),
              String.format(STRING_CONCAT_FORMAT, "%", filter, "%")));
    };
  }

  /**
   * Handle the specification for flow document by collection of userIds or ownerIds {@link List} of
   * {@link Long}
   *
   * @param ownerIds refer to id of the user that are stored into table flow.
   */
  public static Specification<FlowDocument> ownerIdIn(List<Long> ownerIds) {
    return (root, query, cb) -> {
      final Path<FlowTraceability> flowTraceability = root.get(FlowDocument_.FLOW_TRACEABILITY);
      return cb.in(flowTraceability.get(FlowTraceability_.OWNER_ID)).value(ownerIds);
    };
  }

  /**
   * Handle the specification for select email campaign by list of status.
   *
   * @param status refer to list of {@link FlowDocumentStatus}.
   */
  public static Specification<FlowDocument> statusIn(List<String> status) {
    return SpecificationUtils.in(FlowDocument_.status, status);
  }

  /**
   * Handle the specification for select document of flow by list of sub-channel.
   *
   * @param subChannels refer to list of {@link com.tessi.cxm.pfl.ms3.util.SubChannel}.
   */
  public static Specification<FlowDocument> subChannelIn(List<String> subChannels) {
    return SpecificationUtils.in(FlowDocument_.subChannel, subChannels);
  }

  /**
   * Handle the specification for select document of flow by list of channel.
   *
   * @param channel refer to list of {@link com.tessi.cxm.pfl.ms3.util.Channel}.
   */
  public static Specification<FlowDocument> channelIn(List<String> channel) {
    return SpecificationUtils.in(FlowDocument_.channel, channel);
  }

  public static Specification<FlowDocument> fileIdIn(List<String> fileIds) {
    return SpecificationUtils.in(FlowDocument_.fileId, fileIds);
  }

  public static Specification<FlowDocument> isFlowNotDelete() {
    return (root, query, cb) -> {
      final Subquery<FlowDeposit> flowSubQuery = query.subquery(FlowDeposit.class);
      final Root<FlowDeposit> flow = flowSubQuery.from(FlowDeposit.class);
      flowSubQuery.select(flow.get(FlowDeposit_.ID));

      // find the flow traceability that are deleted.
      flowSubQuery.where(
          cb.and(
              cb.equal(
                  flow.get(FlowDeposit_.ID),
                  root.get(FlowDocument_.flowTraceability).get(FlowTraceability_.ID)),
              cb.isFalse(flow.get(FlowDeposit_.IS_ACTIVE))));

      return cb.and(
          List.of(
                  cb.not(
                      root.get(FlowDocument_.flowTraceability)
                          .get(FlowTraceability_.ID)
                          .in(flowSubQuery)))
              .toArray(new Predicate[0]));
    };
  }

  public static Specification<FlowDocument> statusIsNotBlank() {
    return (root, query, cb) ->
        cb.and(
            root.get(FlowDocument_.status).isNotNull(),
            cb.notEqual(cb.trim(root.get(FlowDocument_.status.getName())), ""));
  }

  public static Specification<FlowDocument> containsFlowIds(List<Long> flowIds) {
    return (root, query, cb) -> {
      final Path<FlowDocument> flowDocument = root.join(FlowDocument_.FLOW_TRACEABILITY);
      return cb.in(flowDocument.get(FlowTraceability_.ID)).value(flowIds);
    };
  }

  public static Specification<FlowDocument> containFillers(
      List<String> fillers, String searchByFiller, List<String> fillersConfigured) {
    return (root, query, cb) -> {
      // protect data when the client not configured fillers
      if (CollectionUtils.isEmpty(fillersConfigured)) {
        return null;
      }

      if (StringUtils.isEmpty(searchByFiller)) {
        return null;
      }

      Join<FlowDocument, FlowDocumentDetails> join = root.join(FlowDocument_.DETAIL);
      Expression<String> delimiter = cb.literal(",");

      List<Predicate> likes = new ArrayList<>();

      if (CollectionUtils.isEmpty(fillers)) {
        fillersConfigured.forEach(
            filler -> filterByFillers(searchByFiller, cb, join, delimiter, likes, filler));
      } else {
        fillers.forEach(
            filler -> {
              if (fillersConfigured.contains(filler)) {
                filterByFillers(searchByFiller, cb, join, delimiter, likes, filler);
              }
            });
      }
      return cb.or(likes.toArray(new Predicate[0]));
    };
  }

  private static void filterByFillers(
      String searchByFiller,
      CriteriaBuilder cb,
      Join<FlowDocument, FlowDocumentDetails> join,
      Expression<String> delimiter,
      List<Predicate> likes,
      String filler) {
    var idxFiller = Integer.parseInt(StringUtils.getDigits(filler)) - 1;
    String[] fillerValues = List.of("%%", "%%", "%%", "%%", "%%").toArray(new String[0]);
    fillerValues[idxFiller] = "%" + searchByFiller.toLowerCase() + "%";
    var filter = String.join(",", fillerValues);
    likes.add(
        cb.like(
            cb.lower(
                cb.function(
                    "array_to_string",
                    String.class,
                    join.get(FlowDocumentDetails_.FILLERS),
                    delimiter)),
            filter));
  }

  public static Specification<FlowDocument> containsReference(String reference) {
    return (root, query, cb) -> {
      Join<FlowDocument, FlowDocumentDetails> flowDocDetails = root.join(FlowDocument_.DETAIL);
      return cb.like(cb.lower(flowDocDetails.get(FlowDocumentDetails_.REFERENCE)), String.format(STRING_CONCAT_FORMAT, "%", reference.toLowerCase(), "%"));
    };
  }
}
