import {$count, $length, $now, $ok} from "../../utils/commons";
import {$hash, $uuid} from "../../utils/crypto";
import {$path, $removeFile, $writeBuffer, $writeString,} from "../../utils/fs";
import {ConflictError, FileError, ForbiddenError} from "../../utils/errors";
import {APIFileInfos, APIMimeTypes, UserRole} from "../APIConstants";
import {APIAuth, APIGetListQuery, APIHeaders} from "../APIInterfaces";
import {APIServer} from "../../server";
import {Certigna} from "../../classes/CertignaEndPoint";
import {apiHeadersSchema} from "../APISchemas";
import {Nullable} from "../../utils/types";
import {UploadService} from "../../services/upload.service";
import {Prisma} from "@prisma/client";
import {UploadCreateInput, UploadEntity} from "../../entities/uploads";
import { NO_CONTEXT } from "../../classes/interfaces/DBConstants";

export interface UploadHeaders extends APIHeaders {
    "content-type": string;
}
export const UploadHeadersSchema = {
    ...apiHeadersSchema,
    "content-type": { type: "string" },
};

// FIXME: shouldn't we have TSDataLike instead of string && buffer here for buffer param
export const uploadFile = async (auth: APIAuth, mimeType: string, buffer: Nullable<string | Buffer>
): Promise<UploadEntity> => {
    const uploadService = new UploadService();
    // WARNING: there is no specific authorization verification here.
    //       	everybody can upload, so you'd better not publish this
    //		 	api without any previous authent

    if (!$length(buffer) || !Buffer.isBuffer(buffer)) {
        throw new ConflictError(`No valid payload was send.`);
    }

    // here we have a valid raw Buffer payload
    const api = APIServer.api();

    let type = APIMimeTypes[mimeType];
    if (!$ok(type)) {
        throw new ConflictError(`Bad mime type ${mimeType}.`);
    }

    let extension = APIFileInfos[type].extensions[0];
    let fileBase = $uuid();
    const fileName = `${fileBase}.${extension}`;
    const filePath = $path(api.uploadsPathFiles, fileName);
    const sealPath = $path(api.uploadsPathSeals, `${fileBase}.xml`);

    const hash = $hash(buffer) ;
    if (!$ok(hash)) {
        throw new FileError(`Impossible to calculate uploaded file hash.`);
    }
    if (!$writeBuffer(filePath, buffer)) {
        throw new FileError(`Impossible to save uploaded file.`);
    }
    const endPoint = Certigna.endPoint();
    const credentials = api.conf.signServerLogin;


    const uploadDate = $now();

    let seal = await endPoint.seal(credentials.login, credentials.password, {
        name: fileName,
        user: auth.user,
        size: buffer.length,
        hash: hash!,
        date: uploadDate, // date is mandatory here because we want the same date in our uploaded_date field
    });



    if (!$length(seal)) {
        throw new FileError(`Impossible to seal uploaded file.`);
    }
    if (!$writeString(sealPath, seal)) {
        throw new FileError(`Impossible to save uploaded file seal.`);
    }

    let returnValue = undefined;
    try {
        // we open a transaction in order to close the session
        returnValue = await api.transaction(async (trx) => {
            let n = await UploadEntity.nextGlobalPublicID({ trx: trx }); // this method updates NGConfig table
            let newUpload: UploadCreateInput = {
                publicId: n,
                fileType: type,
                hash: hash!,
                path: filePath,
                size: buffer.length,
                ttl: api.conf.uploadTtl,
                uploadedAt: uploadDate,
                user: auth.user,
            };

            if ($length(sealPath)) {
                newUpload.sealPath = sealPath;
            }

            return await uploadService.insert(newUpload, {trx: trx});
        });

        // here we have committed in the database
    } catch (e) {
        // here we have a rollback
        APIServer.api().error(e);
        throw e;
    }
    return returnValue;
};

export interface UploadListNode {
    publicId: number;
}

export const getUploadList = async (
    auth: APIAuth,
    q: APIGetListQuery
): Promise<string[]> => {
    const uploadService = new UploadService();
    // we have no specific authentification verification here because the query does it
    let query = UploadEntity.expirationAwareListQuery<APIGetListQuery, Prisma.UploadWhereInput>(
        auth,
        q,
        NO_CONTEXT
    );
    if (auth.role === UserRole.Action) {
        // as action role, we get only our uploads
        // query.where("user", "=", auth.user);
        query = {
            ...query,
            user: auth.user,
        }
    }
    const data = await uploadService.findMany<UploadListNode>(
      {
          where: query,
          select: {
              publicId: true
          },
          orderBy: {
              publicId: "asc"
          }
      }
    )
    const api = APIServer.api();

    return $count(data)
        ? data.map((n: UploadListNode) => api.url("upload", n.publicId))
        : [];
};

export const purgeUploads = async (auth: APIAuth): Promise<number> => {
    const uploadService = new UploadService();
    const api = APIServer.api() ;
    let n = 0;
    if (auth.role !== UserRole.Maintenance && auth.role !== UserRole.System) {
        throw new ForbiddenError(`Uploads cannot be purged by user ${auth.user}.`);
    }
    try {
        n = await api.transaction(async (trx) => {
            let total = 0;
            const now = $now();
            const uploads = await uploadService.getsWhere({
                expiresAt: {
                    lte: now
                }
            }, {trx});

            let files: string[] = [];
            if ($count(uploads)) {
                uploads!.forEach((u) => {
                    const upEntity = new UploadEntity(u);
                    return upEntity.fillPathsIn(files)
                });
                total = await uploadService.deleteMany({
                    where: {
                        expiresAt: {
                            lte: now
                        }
                    }
                })
                  //.where("expires_at", "<=", now);
                files.forEach((p) => $removeFile(p));
            }
            return total;
        });
    } catch (e) {
        // here we have a rollback
        APIServer.api().error(e);
        throw e;
    }
    return n;
};
