<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Apolo Music</title>
</head>
<body>
  <div class="body">
  		<div class="row">
				<div class="col-lg-3"></div>
				<div class="col-lg-6">
					<g:form role="form" controller="search" action="index">
						<div class="form-group">
							<div class="input-group">
			                    <input autocomplete="off" id="search" type="text" class="form-control input-lg" placeholder="What are you looking for?">
			                    <span class="input-group-addon"><a id="voice-search-btn" href="javascript:void(0)"><i class="fa fa-microphone"></i></a></span>
			                    <span class="input-group-addon"><a href="javascript:void(0)"><i class="fa fa-headphones"></i></a></span>
	                  		</div>
	                  	</div>	
                  	
                  		<div class="form-group">
	                  		<div class="input-group" style="margin: auto">
			                    <button class="btn btn-block btn-default">Search with Apolo music</button>
	                  		</div>
                  		</div>
					</g:form>
				</div>
				<div class="col-lg-3"></div>
			</div>
 	 </div>
 	 
 	 <script type="text/javascript">
	   //Voive recognition
	  	var recognition = new webkitSpeechRecognition();
	  	recognition.onresult = function(event) { 
	    	  if (event.results.length > 0) {
	    		 var string = event.results[0][0].transcript;
	    		 $('#search').val(string);
	    	  } 
	    	}
			
	  	$(document).ready(function(){
			$("#search").typeahead({ source: ["Shakira" , "songs of Shakira" , "Maroon 5"]});
			$('ul.typeahead.dropdown-menu').css('width', $('#search').css('width'));
			
			$('#voice-search-btn').click(function(){
				recognition.start();
			});
		});
    </script>
</body>
</html>