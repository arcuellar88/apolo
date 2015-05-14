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
					<g:form name="search-form" method="get" role="form" controller="search" action="index">
						<div class="form-group">
							<div class="input-group">
			                    <input required value="${params.keyword}" name="keyword" autocomplete="off" id="search" type="text" class="form-control input-lg" placeholder="What are you looking for?">
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
			
			<g:if test="${isSearching == true}">
				<g:if test="${!spellingCorrectedString.equals("")}">
					<h4>
						Did you mean: 
						<g:link controller="search" params="[keyword: spellingCorrectedString]">
	     					${spellingCorrectedString}
						</g:link>
					</h4>
				</g:if>
				<div class="row">
					<div class="col-lg-9">
						<div class="nav-tabs-custom">
			                <ul class="nav nav-tabs">
			                  	<li class="active"><a href="#tab-song" data-toggle="tab" aria-expanded="false">Songs</a></li>
			                  	<li class=""><a href="#tab-artist" data-toggle="tab" aria-expanded="false">Artists</a></li>
			                 	<li><a href="#tab-release" data-toggle="tab" aria-expanded="true">Releases</a></li>
			                </ul>
			                <div class="tab-content">
			                  	<div class="tab-pane active" id="tab-song">
			                  		<g:each in="${songs}" var="song" status="songCounter">
			                  			
			                  			<g:if test="${songCounter==0}">
			                  				<g:render template="/template/first-song" model="[song: song]"/>
			                  			</g:if>
			                  			<g:else>
			                  				<g:render template="/template/song-result" model="[song: song]"/>
										</g:else>
			                  		</g:each>
			                  			
			                  	</div><!-- /.tab-pane -->
			                  <div class="tab-pane" id="tab-artist">
			                    	<g:each in="${artists}" var="artist" status="artistCounter">
			                  			
			                  			<g:if test="${artistCounter==0}">
			                  				<g:render template="/template/first-artist" model="[artist: artist, firstArtistSongs: firstArtistSongs]"/>
			                  			</g:if>
			                  			<g:else>
			                  				<g:render template="/template/artist-result" model="[artist: artist]"/>
										</g:else>
			                  		</g:each>
			                  </div><!-- /.tab-pane -->
			                  <div class="tab-pane" id="tab-release">
			                    	<g:each in="${releases}" var="release" status="releaseCounter">
			                  			<% 
									   		ArrayList<String> releaseSongs = release.getSplittedFields(release.releaseSongs);
									   		ArrayList<String> releaseSongIDs = release.getSplittedFields(release.releaseSongIDs);
										 %>
			                  			<g:if test="${releaseCounter==0}">
			                  				<g:render template="/template/first-release" model="[release: release, releaseSongs: releaseSongs, releaseSongIDs : releaseSongIDs]"/>
			                  			</g:if>
			                  			<g:else>
			                  				<g:render template="/template/release-result" model="[release: release, releaseSongs: releaseSongs, releaseSongIDs : releaseSongIDs]"/>
										</g:else>
			                  		</g:each>
			                  </div><!-- /.tab-pane -->
			                </div><!-- /.tab-content -->
			              </div>
					</div>
				</div>
			</g:if>
 	 </div>
 	 
 	 <script type="text/javascript">
	   //Voive recognition
	  	var recognition = new webkitSpeechRecognition();
	  	recognition.onresult = function(event) { 
	    	  if (event.results.length > 0) {
	    		 var string = event.results[0][0].transcript;
	    		 $('#search').val(string);
	    		 $('#search-form').submit();
	    	  } 
	    	}

    	$('#search-form').on("submit", function(){
        	$(this).find("button").prop("disabled", true);
        });
			
	  	$(document).ready(function(){
			//$("#search").typeahead({ source: ["Shakira" , "songs of Shakira" , "Maroon 5"]});
			$('ul.typeahead.dropdown-menu').css('width', $('#search').css('width'));
			
			$('#voice-search-btn').click(function(){
				recognition.start();
			});

			initAutocomplete();
		});

		function initAutocomplete() {
			$('#search').keyup(function(e){
				console.log("keyup");
				delay(function(){
					getSuggestion();
			    }, 500 );
			});
		}

		function getSuggestion() {
			var query = $('#search').val();
			$.ajax({
				type: "post",
				url: "search/getSuggestion",
				data: {keyword : query},
				success: function(data) {
					console.log(data)
					$("#search").typeahead("destroy");
					$("#search").typeahead({ source: data.suggestions });
					$("#search").typeahead('lookup');
					initAutocomplete();
				}
			});
		}
    </script>
</body>
</html>