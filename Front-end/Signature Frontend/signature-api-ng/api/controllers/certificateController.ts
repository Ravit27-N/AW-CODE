import { $count, $isnumber } from "../../utils/commons";

import { ForbiddenError, NotFoundError } from "../../utils/errors";

import { sessionWithPublicID } from "./sessionController";
import { APIServer } from "../../server";
import { GlobalID, LocalID } from "../APIIDs";
import { APIAuth } from "../APIInterfaces";
import { revoqueCertignaCertificate } from "./certificatesCommons";
import { CertificateService } from "../../services/certificate.service";
import { Prisma } from "@prisma/client";
import {
  CertificateEntity,
  ICertificate,
} from "../../entities/certificates";
import { SessionEntity } from "../../entities/sessions";
import { PrismaContext } from "../../classes/interfaces/DBInterfaces";
import {CaTokenEntity} from "../../entities/CATokens";

export const getSessionCertificate = async <I extends object = object>(
  auth: APIAuth,
  sessionOrID: GlobalID | SessionEntity,
  cid: LocalID,
  c: PrismaContext<I>
): Promise<CertificateEntity> => {
  let session = $isnumber(sessionOrID)
    ? await sessionWithPublicID(auth, <GlobalID>sessionOrID, { trx: c.trx })
    : <SessionEntity>sessionOrID;
  const certService = new CertificateService();
  let certificates = await certService.findMany<ICertificate>(
    {
      where: {
        publicId: cid,
        sessionId: session.id,
      },
      include: c.include as any,
    },
    c
  );

  if (!$count(certificates)) {
    throw new NotFoundError(
      `Certificate with IDs (${session.publicId},${cid}) was not found.`
    );
  }
  const data = new CertificateEntity(certificates[0]);
  data.setCaToken = certificates[0].caToken ? new CaTokenEntity(certificates[0].caToken) : undefined;
  return data;
};

export const removeSessionCertificate = async (
  auth: APIAuth,
  sessionPublicID: GlobalID,
  cid: LocalID
): Promise<string> => {
  const api = APIServer.api() ;
  let returnValue = undefined;
  try {
    // since we will cascade destruction, we need to be in a transaction
    returnValue = await api.transaction(async (trx) => {
      const context = { trx: trx };
      const certificate =
        await getSessionCertificate<Prisma.CertificateInclude>(
          auth,
          sessionPublicID,
          cid,
          {
            trx: trx,
            include: {
              certFiles: true,
              caToken: {
                include: {
                  actor: {
                    include: {
                      session: true,
                    },
                  },
                },
              },
            },
          }
        );

      const SN = certificate.getCertificateData.serialnumber;
      const url = certificate.url(sessionPublicID);

      if (certificate?.getCaToken?.Actor?.getSession?.wasCertificateUsed(cid)) {
        throw new ForbiddenError(
          `Certificate with IDs (${sessionPublicID},${cid}) cannot be deleted.`
        );
      }
      // since we will cascade destruction, we need to be in a transaction
      await certificate.delete(context);
      await revoqueCertignaCertificate(auth, SN, context); // if we fail to revoke we ignore it

      return url;
    });
  } catch (e) {
    // here we may have a rollback
    APIServer.api().error(e);
    throw e;
  }

  return returnValue;
};
