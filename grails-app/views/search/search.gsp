<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Apolo Music</title>
<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-63787311-1', 'auto');
	  ga('send', 'pageview');
</script>
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
			                    <span class="input-group-addon"><a id="voice-record-btn" href="javascript:void(0)"><i class="fa fa-headphones"></i></a></span>
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
		
		<g:if test="${isSearching == true}">
			<div class="row">
				<div class="col-lg-9">
					<g:if test="${!spellingCorrectedString.equals("")}">
						<h4>
							Did you mean: 
							<g:link controller="search" params="[keyword: spellingCorrectedString]">
		     					${spellingCorrectedString}
							</g:link>
						</h4>
					</g:if>
				</div>
				
				<div class="col-lg-3"></div>
			</div>
			
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
					
				<!-- RECOMMENDATION -->
				<div class="col-lg-3">
						<div class="box box-default">
			                <div class="box-header with-border">
			                 	<i class="fa fa-thumbs-up	"></i>
			                  	<h3 class="box-title">You might be interested</h3>
			                </div><!-- /.box-header -->
			                
			                <g:if test="${artists.size() > 0}">
				                <div class="box-body artist-recomendation-wrapper" artist-id="${artists.get(0).artistID}">
				                	<div class="clearfix"></div>
				                 	<div class="overlay centered" style="height: 30px; position: relative;"><i class="fa fa-circle-o-notch fa-spin"></i></div>
				                </div><!-- /.box-body -->
				                <script type="text/javascript">
					                function initRecommendation() {
					        			var artistID = $('.artist-recomendation-wrapper').attr("artist-id")
					        			$.ajax({
					        				url : "search/getRecommendationArtist?artistID=" + artistID,
					        				success: function(data) {
					        					$('.artist-recomendation-wrapper').html(data);
					        					initModalEntity();
					        				}
					        			});
					        		}
					                initRecommendation();
				                </script>
			                </g:if>
	             		</div>
	                </div>
			</div>
			
			<!-- MODEL TEMPLATE -->
			<div id="entity-modal" class="modal" style="display: none">
            	<div class="modal-dialog modal-lg">
                	<div class="modal-content">
                  		<div class="modal-header">
                    		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
                    		<h4 class="modal-title">Modal Default</h4>
                  		</div>
                  		
                  		<div class="modal-body">
                    		<p>One fine body…</p>
                  		</div>
                  	
                </div><!-- /.modal-content -->
              </div><!-- /.modal-dialog -->
            </div>
 	 </g:if>
 	 
 	  <!-- AUDIO RECORDING MODEL -->
       <div id="audio-modal" class="modal" style="display: none">
       	<div class="modal-dialog modal-lg">
           	<div class="modal-content">
             		<div class="modal-header">
               		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
               		<h4 class="modal-title">Recording...</h4>
             		</div>
             		
             		<div class="modal-body">
            				<div class="centered" style="font-size: 30px">
            					<i class="fa fa-fw fa-headphones"></i>
            				</div>
               			<div class="progress" style="margin-top: 10px; margin-bottom: 10px">
		                    <div class="progress-bar progress-bar-primary progress-bar-striped" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
		                      	<span style="position: static"class="sr-only">0</span>
		                    </div>
		                  </div>
             		</div>
           </div><!-- /.modal-content -->
         </div><!-- /.modal-dialog -->
       </div>
 	 
	 <script src="${resource(dir: 'js', file: 'plugins/recording/mp3recorder.js')}" type="text/javascript"></script>
	 
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

	  		$('#search').focus();
		  	
			$('ul.typeahead.dropdown-menu').css('width', $('#search').css('width'));
			
			$('#voice-search-btn').click(function(){
				recognition.start();
			});

			initAutocomplete();

			initModalEntity();

			initRecording();
		});

		function initAutocomplete() {
			$('#search').keyup(function(e){
				var code = e.keyCode || e.which;
				//only accept some code: http://www.cambiaresearch.com/articles/15/javascript-char-codes-key-codes
				if ((code >= 48 && code <= 90) || (code >= 186 && code <= 222)) {
					delay(function(){
						getSuggestion();
				    }, 500 );
				}
			});
		}

		function getSuggestion() {
			var query = $('#search').val();
			$.ajax({
				type: "post",
				url: "search/getSuggestion",
				data: {keyword : query},
				success: function(data) {
					$("#search").typeahead("destroy");
					$("#search").typeahead({ source: data.suggestions });
					$("#search").typeahead('lookup');
					initAutocomplete();
				}
			});
		}

		function initModalEntity() {
			$('#entity-modal').modal({show : false})
			$("a.load-document-id").unbind('click').click(function(e){
				var entityID = $(this).attr("entity-id")
				$.ajax({
					url : "search/getEntity?entityID=" +entityID,
					dataType: "json",
					beforeSend: function() {
						$('#entity-modal .modal-body').html('<div class="overlay centered"><i class="fa fa-circle-o-notch fa-spin"></i></div>');
						$('#entity-modal').modal('hide');
						$('#entity-modal').modal('show');
						$('#entity-modal .modal-title').html("Loading...");
					},
					success: function(data) {
						$('#entity-modal .modal-body').html(data.data)
						$('#entity-modal .modal-title').html(data.entityName);
					}
				});
			});
		}
    </script>
</body>
</html>