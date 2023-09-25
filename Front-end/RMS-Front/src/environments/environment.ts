// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  basePath: '/', // frontend base path for development.
  production: false,
  rmsContextPath: '/rms-service/api/v1',
  elfinderConnectorPath: '/elfinder-connector/api/v1',
  apiUrl: 'http://10.2.2.129:8090',
  oauth: {
    // issuer: 'http://localhost:8080/auth/realms/aw_recruitment',
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
  errorMsg: {
    test: 'this is a test'
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
