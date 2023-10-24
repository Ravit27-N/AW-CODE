package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.model.entity.Role;
import com.innovationandtrust.profile.repository.RoleRepository;
import com.innovationandtrust.profile.service.spefication.RoleSpec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class RoleServiceTests {
    @Mock
    RoleRepository roleRepository;
    @Mock
    RoleDto roleDTO;
    @Mock Role role;
    @Mock
    Set<String> names;
    @Mock
    List<RoleDto> roleDTOS;
    private RoleService roleService;

    @BeforeEach
    void setup() {
        roleDTO = new RoleDto();
        roleDTO.setId(1L);
        roleDTO.setName("Admin");
    }
    @Test
    @DisplayName("Save role test")
    void save_role_test() {
        //when
        when(roleService.save(roleDTO)).thenReturn(roleDTO);

        var result = roleService.save(roleDTO);

        //then
        assertThat(result).isNotNull();
        assertEquals(result.getId(), roleDTO.getId());
        verify(roleService, times(1)).save(roleDTO);
    }
    @Test
    @DisplayName("Find by keycloak role id test")
    void find_by_keycloak_role_id_test() {
        var specification = Specification.where(RoleSpec.findByKeycloakRole("1"));

        //when
        when(roleRepository.findOne(specification)).thenReturn(Optional.of(role));

        Optional<Role> result = roleRepository.findOne(specification);

        //then
        assertThat(result).isNotNull();
        verify(roleRepository, times(1)).findOne(specification);
    }
    @Test
    @DisplayName("Find by names test")
    void find_by_names_test() {
        //when
        when(roleService.findByNames(names)).thenReturn(roleDTOS);
        when(roleService.findByNames(names).size()).thenReturn(3);

        List<RoleDto> result = roleService.findByNames(names);

        //then
        assertEquals(3, result.size());
        verify(roleService, times(2)).findByNames(names);
    }
}
