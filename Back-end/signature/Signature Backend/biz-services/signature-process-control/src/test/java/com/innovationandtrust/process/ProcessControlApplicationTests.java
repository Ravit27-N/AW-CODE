package com.innovationandtrust.process;

import com.innovationandtrust.utils.encryption.AESHelper;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ProcessControlApplicationTests {

  @Test
  void testContextLoads() {
    var impor = new AESHelper("signature.keycloak.secret");
    String shortEncrypt = impor.shortEncrypt("Hello");
    log.info("{}", shortEncrypt);
    String shortDecrypt = impor.shortDecrypt(shortEncrypt);
    log.info("{}", shortDecrypt);
    var data =
        impor.decryptToken(
            "Tqhn-XPXWbodGBLdO4lgwWqunVy6SeeFbpnSZmP-a3clLWQ0ZtztRwI3BO48nvXpSgdxRb916F3OBqiw2OEOGMBDo2aIAeSS_dVdb-4_CM8McUfyvEYfzKp9jGF0M_2Q3SKuPykZMWY9w9zZC87egpsgs6D0FrSWxU1FJ0UQb7Q");
    log.info("Personal Data {}", data);
  }

  @Test
  void testRemoveDuplicateSlashesFromURL() {
    String normalizeUrlHttpS =
        URI.create(
                "https://polymermallard.medium.com/regex-fix-duplicate-slashes-without/affecting-protocol//daa1ac34a469")
            .normalize()
            .toString();
    String normalizeUrlHttp =
        URI.create(
                "http://polymermallard.medium.com//regex-fix-duplicate-slashes-without/affecting-protocol//daa1ac34a469")
            .normalize()
            .toString();
    String https = normalizeUrlHttpS.replaceAll("(?<!\\w+:/?)//+", "/");
    log.info("Url {}", https);
    String http = normalizeUrlHttp.replaceAll("(?<!\\w+:/?)//+", "/");
    log.info("Url {}", http);
  }
}
