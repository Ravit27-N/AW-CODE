package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.exception.UserNotFoundException;
import com.innovationandtrust.profile.model.entity.LoginHistory;
import com.innovationandtrust.profile.model.entity.LoginHistory_;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.profile.repository.LoginHistoryRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.share.model.profile.LoginHistoryDto;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginHistoryService extends CommonCrudService<LoginHistoryDto, LoginHistory, Long> {
  private final LoginHistoryRepository loginHistoryRepository;
  private final UserRepository userRepository;

  protected LoginHistoryService(
      final ModelMapper modelMapper,
      LoginHistoryRepository loginHistoryRepository,
      IKeycloakProvider keycloakProvider,
      UserRepository userRepository) {
    super(modelMapper, keycloakProvider);
    this.loginHistoryRepository = loginHistoryRepository;
    this.userRepository = userRepository;
  }

  /**
   * Save history of user login history.
   *
   * @return saved login history
   */
  @Transactional(rollbackFor = Exception.class)
  public LoginHistoryDto save() {
    Long userId = this.getUserId();
    User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    LoginHistory history = new LoginHistory(null, this.getUserEmail(), user);
    return this.mapData(this.loginHistoryRepository.save(history));
  }

  public List<LoginHistoryDto> getLoginHistoriesByUsers(List<Long> userIds) {
    return this.mapAll(
        this.loginHistoryRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(LoginHistory_.USER, User_.ID))
                    .operator(QueryOperator.IN)
                    .values(userIds.stream().map(String::valueOf).toList())
                    .build())),
        LoginHistoryDto.class);
  }

  public List<LoginHistoryDto> getLoginHistoryByUser(Long userId) {
    return this.mapAll(
        this.loginHistoryRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(LoginHistory_.USER, User_.ID))
                    .operator(QueryOperator.IN)
                    .value(String.valueOf(userId))
                    .build())),
        LoginHistoryDto.class);
  }
}
