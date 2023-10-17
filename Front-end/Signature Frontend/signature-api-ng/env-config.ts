import {$config} from "./utils/tsdefaults";
import { $logterm } from "./utils/utils";

if (process.env.NODE_ENV === "production") {
  $config("prod.env", {debug: true})
  $logterm(">>>>>>>>>>-------Production Mode-------<<<<<<<<<<<");

} else {
  $config("dev.env", {debug: true})
  $logterm(">>>>>>>>>>-------Development Mode-------<<<<<<<<<<<");
}

export = {
  APP_SCHEMES: process.env.APP_SCHEMES,
  APP_ADDRESS: process.env.APP_ADDRESS,
  APP_PORT: Number(process.env.APP_PORT),
  NODE_ENV: process.env.NODE_ENV,
  LOG_LEVEL: process.env.LOG_LEVEL,
  REMOTE_URL: process.env.REMOTE_URL,
  SIGN_SERVER_LOGIN: process.env.SIGN_SERVER_LOGIN,
  SIGN_SERVER_PASS: process.env.SIGN_SERVER_PASS,
  CERTIFICATE_BASE: process.env.CERTIFICATE_BASE,
  SIGNATURE_BASE: process.env.SIGNATURE_BASE,
  AES_ALGORITHM: process.env.AES_ALGORITHM,
  AES_ENCRYPT_KEY: process.env.AES_ENCRYPT_KEY,
};
