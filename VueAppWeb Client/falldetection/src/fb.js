import firebase from 'firebase/app';
import 'firebase/database';
import 'firebase/auth';

// Initialize Firebase
let config = {
  apiKey: "AIzaSyC7ngsEHdQb_ythi-lTtooSaYST4JidfW4",
  authDomain: "falldetection-bee2b.firebaseapp.com",
  databaseURL: "https://falldetection-bee2b.firebaseio.com",
  projectId: "falldetection-bee2b",
  storageBucket: "falldetection-bee2b.appspot.com",
  messagingSenderId: "534305138098",
  appId: "1:534305138098:web:4d64b351f7b362dcc830ab",
  measurementId: "G-HK9H83CKTZ"
};

firebase.initializeApp(config);

let mail = "app@fall.com";
let pass = "appfalldetectionIOT";

firebase.auth().signInWithEmailAndPassword(mail, pass)
  .catch(function (error) {
    var errorCode = error.code;
    var errorMessage = error.message;
    console.log("Error: " + errorCode + " -- " + errorMessage);
  });

export default firebase.database();