package com.innovationandtrust.utils.encryption;

import com.innovationandtrust.utils.encryption.exception.EncryptionException;
import com.innovationandtrust.utils.encryption.exception.InvalidUserTokenException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImpersonateTokenService {
    private static final String PREFIX = "sig$";

    private final ImpersonateToken impersonateToken;

    public ImpersonateTokenService(String secretKey) {
        this.impersonateToken = new ImpersonateToken(secretKey);
    }

    private String generateImpersonateTicket(
            String companyUuid, String participantUuid, String flowId) {
        return PREFIX + companyUuid + "$" + participantUuid + "$" + flowId;
    }

    private String getFlowIdFromTicket(String impersonateTicket) {
        String[] ticketArray = impersonateTicket.split("\\$");
        return ticketArray[3];
    }

    private String getParticipantUuidFromTicket(String impersonateTicket) {
        String[] ticketArray = impersonateTicket.split("\\$");
        return ticketArray[2];
    }

    public TokenParam validateImpersonateToken(String companyUuid, String token) {
        String impersonateTicket = this.getImpersonateTicket(token);
        // verify impersonate ticket.
        if (!impersonateTicket.startsWith(PREFIX + companyUuid)) {
            throw new InvalidUserTokenException();
        }

        return TokenParam.builder()
                .companyUuid(companyUuid)
                .flowId(getFlowIdFromTicket(impersonateTicket))
                .uuid(getParticipantUuidFromTicket(impersonateTicket))
                .token(token)
                .build();
    }

    private String getImpersonateTicket(String token) {
        try {
            return this.impersonateToken.decryptToken(token);
        } catch (Exception e) {
            log.error("Error ", e);
            throw new EncryptionException("Invalid user token!");
        }
    }

    public String generateToken(String companyUuid, String participantUuid, String flowId) {
        String ticket = this.generateImpersonateTicket(companyUuid, participantUuid, flowId);
        return this.impersonateToken.encrypt(ticket);
    }

    /**
     * Get completed front url for participant.
     *
     * @return string of completed url with encrypted token.
     */
    public String getTokenUrlParam(
            String flowId, String uuid, String frontUrl, String contextPath, String companyUuid) {
        String encryptUrlParam = this.generateToken(companyUuid, uuid, flowId);
        return String.format("%s/%s/%s?token=%s", frontUrl, contextPath, companyUuid, encryptUrlParam);
    }
}
