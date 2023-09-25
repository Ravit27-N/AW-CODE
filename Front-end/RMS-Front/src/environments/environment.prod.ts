export const environment = {
  basePath: '/rms/', // frontend base path for production, It must be the same with command --base-href /rms/ ,
  // /rms/ refer to directory of front end deployed.
  production: true,
  rmsContextPath: '/rms-service/api/v1',
  apiUrl: 'https://dev.allweb.com.kh/rms/gateway',
  oauth: {
    // issuer:
    //   // eslint-disable-next-line @typescript-eslint/dot-notation
    //   window['env']['keycloakUrl'] || 'http://10.2.2.129:8020/auth/realms/aw_recruitment',
    issuer: 'https://dev.allweb.com.kh:8070/realms/aw_recruitment',
    clientId: 'angular',
    dummyClientSecret: 'p0LeJ4LzbJnNhyclpvvJw1Y9adJTzbXR'
  },
  firebase: {
    publicKey: 'BDYgp0_ycFfwLphpeLjBl9V6Wdd1wP4oIJxEECOaGLc',
    databaseURL: 'https://allweb-hrm-mobile.firebaseio.com',
    apiKey: 'AIzaSyAVTuakdZiCWQPUEb0G4i7wD4wKE0FQ4AE',
    authDomain: 'allweb-hrm-mobile.firebaseapp.com',
    projectId: 'allweb-hrm-mobile',
    storageBucket: 'allweb-hrm-mobile.appspot.com',
    messagingSenderId: '872486263189',
    appId: '1:872486263189:web:68a4bd3b6af575eb6f7e9c',
    measurementId: 'G-YPVZFD75V3',
    vapidKey: 'BHi69yLTmUQ2KuoYjgVPVJpurlJPrc3xW-bnC08Nw1VVOVfNEKYaLvXJDv6fbv_9sG8lz4ckXRP8vMwenybslOQ'
  },
};
