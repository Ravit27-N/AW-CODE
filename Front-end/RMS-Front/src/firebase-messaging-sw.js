 importScripts("https://www.gstatic.com/firebasejs/9.1.3/firebase-app-compat.js");
 importScripts("https://www.gstatic.com/firebasejs/9.1.3/firebase-messaging-compat.js");

firebase.initializeApp({
  apiKey: "AIzaSyAVTuakdZiCWQPUEb0G4i7wD4wKE0FQ4AE",
  authDomain: 'allweb-hrm-mobile.firebaseapp.com',
  projectId: 'allweb-hrm-mobile',
  storageBucket: 'allweb-hrm-mobile.appspot.com',
  messagingSenderId: '872486263189',
  appId: '1:872486263189:web:68a4bd3b6af575eb6f7e9c',
  measurementId: 'G-YPVZFD75V3',
});
const messaging = firebase.messaging();
