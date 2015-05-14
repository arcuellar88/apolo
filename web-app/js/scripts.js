var delay = (function(){
  	var timer = 0;
  	return function(callback, ms){
    clearTimeout (timer);
    timer = setTimeout(callback, ms);
  };
})();

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