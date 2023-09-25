// import { sharedEnvironment } from '../app.config';

export const flowDepositEnv = {
  // ...sharedEnvironment,
  production: false,
  name: 'dev',
  flowDepositContext: '/cxm-acquisition/api/v1',
  processControlContext: '/cxm-process-control/api/v1',
  fileManagerContext: '/cxm-file-manager/api/v1',
  resourceContext: '/cxm-setting/api/v1',
  server: 'cxm-acquisition'
}
