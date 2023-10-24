package com.innovationandtrust.process;

import com.innovationandtrust.utils.encryption.ImpersonateToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
class ProcessControlApplicationTests {

	@Test
	void contextLoads() {
		var impor = new ImpersonateToken("signature.keycloak.secret");
	var data=	impor.decryptToken("Tqhn-XPXWbodGBLdO4lgwWqunVy6SeeFbpnSZmP-a3clLWQ0ZtztRwI3BO48nvXpSgdxRb916F3OBqiw2OEOGMBDo2aIAeSS_dVdb-4_CM8McUfyvEYfzKp9jGF0M_2Q3SKuPykZMWY9w9zZC87egpsgs6D0FrSWxU1FJ0UQb7Q");
		log.info("Personal Data {}", data);
	}

}
