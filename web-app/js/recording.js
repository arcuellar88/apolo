window.AudioContext = window.AudioContext || window.webkitAudioContext;

navigator.getUserMedia = ( navigator.getUserMedia ||
    navigator.webkitGetUserMedia ||
    navigator.mozGetUserMedia ||
    navigator.msGetUserMedia);

window.URL = window.URL || window.webkitURL;

// create an audio context
var audio_context = new AudioContext;