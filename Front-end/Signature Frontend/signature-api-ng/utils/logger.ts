import pino from "pino";
import * as FileStreamRotator from 'file-stream-rotator';
import {Config} from "../server-config";

const logRotation = (config: Config) => {
    return FileStreamRotator.getStream({
        filename: `${config.logsPath}/api-ng-%DATE%`,
        frequency: "daily",
        date_format: "DD-MM-YYYY",
        // size: `${config.logFileSizeMax}k`,
        max_logs: `${config.keepLogsMax}d`, // keep days
        audit_file: `${config.logsPath}/../audit.json`,
        extension: ".log",
    })
}
const logger = (apiName: string, config: Config) => pino({
    redact: ['req.headers.certignahash'],
    name: apiName,
    level: config.logLevel || 'info',
    timestamp: true,
    serializers: {
        req(request) {
            return {
                requestMethod: request.method,
                requestUrl: request.url,
                ip: request.ip,
            }
        },
        res(response) {
            return {
                statusCode: response.statusCode,
            }
        },
        err() {
            return;
        }
    },
}, logRotation(config));
export default logger;

export type logRequest = {
    error?: string;
    apiRole: number;
    user: string;
    role: number;
    date: string;
    request: object | undefined;
    reply: object | undefined;
}