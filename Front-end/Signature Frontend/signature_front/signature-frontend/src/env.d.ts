/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_SIGNATURE_VERSION: string;
  readonly VITE_SIGNATURE_PUBLISH_DATE: string;

  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
