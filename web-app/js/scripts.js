var delay = (function(){
  	var timer = 0;
  	return function(callback, ms){
    clearTimeout (timer);
    timer = setTimeout(callback, ms);
  };
})();

var recordingProgressId = null;
var secondRecorded = 0;
var recorderObject = null;
var audio_context = null;

function drawRatingPieChart(positiveRating, neutralRating, negativeRating, chartHolderId) {
	var data = google.visualization.arrayToDataTable([
	                                                 ['Rating', 'Number'],
	                                                 ['Positive',     positiveRating],
	                                                 ['Negative',      negativeRating],
	                                                 ['Neutral',  neutralRating]
	                                               ]);

	var chart = new google.visualization.PieChart(document.getElementById(chartHolderId));
	chart.draw(data);
}

function initRecording() {
	
	window.AudioContext = window.AudioContext || window.webkitAudioContext;

	navigator.getUserMedia = ( navigator.getUserMedia ||
        navigator.webkitGetUserMedia ||
        navigator.mozGetUserMedia ||
        navigator.msGetUserMedia);

	window.URL = window.URL || window.webkitURL;
	
	// create an audio context
	audio_context = new AudioContext;
	
	$('#audio-modal').modal({show : false});
	$('a#voice-record-btn').click(function(){
		navigator.getUserMedia({audio: true}, function(stream) {
				
			$('#audio-modal').modal("show");
			
			  // create an MP3Recorder object supplying the audio context and the stream
			  recorderObject = new MP3Recorder(audio_context, stream);
			  recorderObject.start();
			  secondRecorded = 0;
			  $('#audio-modal .modal-title').html("Recording...");
			  
			  recordingProgressId = window.setInterval(function(){
				  
				  secondRecorded += 0.1;
				  $('#audio-modal .progress-bar span').html(secondRecorded.toFixed(1) + " seconds");
				  var percent = secondRecorded * 1.0 * 100 / 30;
				  $('#audio-modal .progress-bar').css("width" , percent + "%");
				  
				  if (secondRecorded > 30) {
					  clearInterval(recordingProgressId);
					  
					  recorderObject.stop();
					  recorderObject.stop();
					  recorderObject.stop();
					  
					  $('#audio-modal .progress').hide();
					  $('#audio-modal .modal-title').html("Uploading...");
					  $('#audio-modal .progress-bar span').html("0%");
					  $('#audio-modal .progress').show();
					  
					  recorderObject.exportWAV(function(wavData) {
						  data = 'data:audio/wav;base64,' + wavData;
						  $.ajax({
							  type: "post",
							  url: "/apolo/search/upload",
							  timeout: 300000,
							  data : {data : data, secondRecorded : secondRecorded.toFixed(0)},
							  xhr: function() {
							        var xhr = new window.XMLHttpRequest();
							        xhr.upload.addEventListener("progress", function(evt) {
							            if (evt.lengthComputable) {
							                var percentComplete = evt.loaded / evt.total;
							                var percent = percentComplete * 100;
							                $('#audio-modal .progress-bar span').html(percent.toFixed(0) + "%");
							                $('#audio-modal .progress-bar').css("width" , percent.toFixed(0) + "%");
							            }
							       }, false);
							        
							        return xhr;
							  },
							  success: function(data) {
								  if (data.songName.length > 0) {
									  $('#search-form #search').val(data.songName);
									  $('#audio-modal').modal("hide");
									  $('#search-form').submit();
								  }
								  else {
									  $('#audio-modal').modal("hide");
									  alert("Sorry we could not regconize the song!");
								  }
								
							  }
						  });
					  });
				  }
			  }, 100)
				  
			}, function(e) {
				// some error occured
			})
	})
	
	$('#audio-modal').on('hide.bs.modal', function (e) {
		
		if (recordingProgressId != null) {
			clearInterval(recordingProgressId);
		}
		
		if (recorderObject != null) {
			recorderObject.stop();
		}
	});
}