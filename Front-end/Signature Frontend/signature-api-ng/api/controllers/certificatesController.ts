import {
  $count,
  $length,
  $now,
  $ok,
  $unsigned,
  stringifyPrisma,
} from "../../utils/commons";
import { $removeFile } from "../../utils/fs";
import { $uuid } from "../../utils/crypto";

import {
  BadRequestError,
  ConflictError,
  ForbiddenError,
  NotFoundError,
} from "../../utils/errors";

import { uploadWithURL } from "./uploadController";
import { ActorType, CertificateStatus, UserRole } from "../APIConstants";
import { APIServer } from "../../server";
import {
  APIAuth,
  CertificateFileNode,
  CertificatesQuery,
  CreateCertificateBody,
} from "../APIInterfaces";
import { authorityWithPublicID } from "./caController";
import { getSessionActorByID } from "./actorController";
import { $url2gid, $url2lid, GlobalID, LocalID } from "../APIIDs";
import { sessionWithPublicID } from "./sessionController";
import { generateCertignaCertificate } from "./certificatesCommons";
import { CertificateEntity } from "../../entities/certificates";
import {Certificate, Prisma } from "@prisma/client";
import { UploadEntity } from "../../entities/uploads";
import { CaTokenService } from "../../services/caToken.service";
import { CaTokenEntity, ICaToken } from "../../entities/CATokens";
import { CertificateService } from "../../services/certificate.service";
import { FileService } from "../../services/file.service";
import { FileEntity } from "../../entities/files";
import { CertFileService } from "../../services/certFile.service";
import {
  NO_CONTEXT,
  apiGlobals,
  TokenStatus,
  FileStatus,
} from "../../classes/interfaces/DBConstants";
import {
  SessionContextEventType,
  PrismaContext,
} from "../../classes/interfaces/DBInterfaces";

export interface CertificateListNode {
  publicId: GlobalID;
}

export const getSessionCertificateList = async (
  auth: APIAuth,
  sessionPublicID: LocalID,
  q: CertificatesQuery
): Promise<string[]> => {
  await sessionWithPublicID(auth, sessionPublicID, NO_CONTEXT); // here in order to verify the rights to do what we want to do...
  const certService = new CertificateService();
  let query = CertificateEntity.expirationAwareListQuery<
    CertificatesQuery,
    Prisma.CertificateWhereInput
  >(auth, q, NO_CONTEXT);
  if (auth.role === UserRole.Action) {
    // as action role, we get only our certificates
    query = {
      ...query,
      user: auth.user,
    };
  }
  // we get only valid certificates
  query = {
    ...query,
    status: CertificateStatus.Valid,
  };

  if ($ok(q.caid)) {
    try {
      const appendCaId = CertificateEntity.addGlobalIDsToQuery(q.caid);
      if ($ok(appendCaId)) {
        query = {
          ...query,
          caToken: {
            ca: {
              publicId: {
                [`${appendCaId!.operator}`]: appendCaId!.value,
              },
            },
          },
        };
      }
    } catch (e) {
      throw new ConflictError("Bad certification authority id in request");
    }
  }
  if ($ok(q.actorIds)) {
    try {
      const appendActorId = CertificateEntity.addLocalIDsToQuery(q.actorIds);
      if ($ok(appendActorId)) {
        query = {
          ...query,
          caToken: {
            actor: {
              publicId: {
                [`${appendActorId?.operator}`]: appendActorId?.value,
              },
            },
          },
        };
      }
    } catch (e) {
      throw new ConflictError("Bad actor id or actor ids list in request");
    }
  }

  let list = await certService.findMany<CertificateListNode>(
    {
      where: query,
      select: {
        publicId: true,
      },
      orderBy: {
        publicId: "asc",
      },
    },
    NO_CONTEXT
  );

  if ($count(list)) {
    let api = APIServer.api();
    return list.map((n) =>
      api.url("session", sessionPublicID, "actor", n.publicId)
    );
  }
  return [];
};

export interface UploadNode extends CertificateFileNode {
  upload: UploadEntity;
}

export const generateCertificateForSession = async (
  auth: APIAuth,
  sessionPublicID: LocalID,
  q: CreateCertificateBody
): Promise<CertificateEntity> => {
    
  let returnValue = undefined;
  let paths: string[] = [];
  const api = APIServer.api() ;
  try {
    returnValue = await api.transaction(
      async (trx) => {
        let uploads: UploadNode[] = [];
        const context = { trx: trx };
        const certService = new CertificateService();
        let session = await sessionWithPublicID(auth, sessionPublicID, context);
        const uploadNodes = q["supporting-documents"];

        if ($count(uploadNodes)) {
          for (let node of <CertificateFileNode[]>uploadNodes) {
            if (!$length(node.filename)) {
              throw new BadRequestError("bad filename definition");
            }
            const upload = await uploadWithURL(node.url, context);

            uploads.push({ upload: upload, ...node });
            upload.fillPathsIn(paths);
          }
        }

        const caid = $url2gid(q.authority);
        if (!caid) {
          throw new NotFoundError("AC not found");
        }
        const aid = $url2lid(q.actor);
        if (!aid) {
          throw new NotFoundError("Actor not found");
        }
        if ($length(q.token) !== apiGlobals.uuidLength) {
          throw new NotFoundError("Token not found or malformed");
        }

        const authority = await authorityWithPublicID(auth, caid, context);
        let actor = await getSessionActorByID(auth, session, aid, context);
        const caTokenService = new CaTokenService();
        let tokens = await caTokenService.findMany<ICaToken>(
          {
            where: {
              caId: authority.id,
              sessionId: session.id,
              actorId: actor.id,
              token: q.token,
              status: TokenStatus.Active,
            },
          },
          context
        );

        if ($count(tokens) !== 1) {
          throw new NotFoundError("Token not found");
        }
        let token = new CaTokenEntity(tokens[0]);
        actor.setSession = session;
        token.Authority = authority; // make our graph strait
        token.Actor = actor; // idem

        // TODO: should be check session mutability here ?
        // await checkSessionMutability(actor.session) ;
        if (actor.url() !== q.actor) {
          throw new NotFoundError("Actor not found : wrong session identifier"); // we did put a bad session ID in the actor URL !
        }
        let validCertificates = await findValidCertificatesByToken(
          certService,
          token.id,
          { trx: trx }
        );
        if ($count(validCertificates)) {
          throw new ForbiddenError(
            "Can not generate new certificate while the generated still valid"
          );
        }
        token.Actor = actor;
        let api = APIServer.api();
        let ttl: number = $unsigned(q.ttl);
        if (!ttl) {
          ttl = api.conf.certificateTtl;
        }

        let certificateData = await generateCertignaCertificate(
          auth,
          {
            givenName: $length(actor.firstName)
              ? <string>actor.firstName
              : "John",
            surname: actor.type === ActorType.Person ? actor.name : "Doe",
            organizationName:
              actor.type === ActorType.Entity ? actor.name : "Unknown",
            emailAddress: actor.email,
            countryName: actor.country,
            lifespan: Math.floor(ttl),
          },
          actor.type,
          context
        );
        // we need to create all the filerefs for the certificate
        let files: FileEntity[] = [];
        const fileService = new FileService();
        for (let node of uploads) {
          const upload = node.upload;
          let file = await fileService.insert(
            {
              fileName: node.filename,
              fileType: upload.fileType,
              hash: upload.hash,
              path: upload.path,
              sealPath: upload.sealPath,
              size: upload.size,
              status: FileStatus.Valid,
              timestampedAt: <Date>upload.uploadedAt,
              uploadedAt: upload.uploadedAt,
              user: upload.user,
            },
            context
          );

          files.push(file);
          await upload.cleanAndDelete(context);
        }

        let pid = actor.getSession!.sessionNextPublicID();
        let certificate = await certService.insert(
          {
            caToken: {
              connect: {
                id: token.id,
              },
            },
            publicId: pid,
            sessionId: session.id,
            status: CertificateStatus.Valid,
            ttl: ttl,
            user: auth.user,
            certificateData: stringifyPrisma(certificateData),
          },
          context
        );

        if ($count(files)) {
          const certFileService = new CertFileService();
          //Todo: what if data of rank, status, usage
          await Promise.all(
            files.map(async (file) => {
              await certFileService.insert(
                {
                  certificate: {
                    connect: {
                      id: certificate.id,
                    },
                  },
                  file: {
                    connect: {
                      id: file.id,
                    },
                  },
                },
                context
              );
            })
          );
        }

        let sessionOtherData = { ...actor.getSession!.getOtherData };
        sessionOtherData.sessionContextEvents.push({
          user: auth.user,
          date: certificate.creationDate(),
          "event-type": SessionContextEventType.GenerateCertificate,
          "actor-id": aid,
          "operation-id": $uuid(),
          token: token.token,
          "certificate-id": pid,
        });

        await actor.getSession!.updateSession(
          {
            lastPubObject: actor.getSession!.lastPubObject,
            otherData: stringifyPrisma(sessionOtherData),
          },
          context
        );

        return certificate;
      });
  } catch (e) {
    // we did rollback here
    // we at least will remove all uploaded files...
    paths.forEach((p) => $removeFile(p));
    APIServer.api().error(e);
    throw e;
  }
  return returnValue;
};

export const findValidCertificatesByToken = async (
  certService: CertificateService,
  caTokenId: LocalID,
  c: PrismaContext
): Promise<Certificate[]> => {
  if (!$ok(caTokenId)) {
    return <Certificate[]>[];
  }
  return await certService.findMany<Certificate>(
    {
      where: {
        caTokenId: caTokenId,
        status: CertificateStatus.Valid,
        expiresAt: {
          gt: $now(),
        },
      },
    },
    c
  );
};
