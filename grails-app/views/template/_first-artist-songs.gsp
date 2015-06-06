<g:if test="${firstArtistSongs.size() > 0}">
	        	<table class="table">
	        		<tr>
	        			<th>#</th>
	        			<th>Songs of ${artist.artistName}</th>
	        		</tr>
	        		
	        		<g:each in="${firstArtistSongs}" var="artistSong" status="songArtistCounter">
	        			<g:if test="${artistSong != null}">
		        			<tr>
		        				<td>${songArtistCounter + 1}</td>
		        				<td>
		        					<a href="javascript:void(0)" class="load-document-id" entity-id="${artistSong.documentID}">${artistSong.songTitle}</a>
		        				</td>
		        			</tr>
	        			</g:if>
	        		</g:each>
	        	</table>
	        	<g:if test="${artist != null}">
		        	<div style="width: 100%; margin-top: 5px;">
	        			<a artistID="${artist.artistID}" class="view-all-song-btn" href="javascript:void(0)">View all song</a>
	        		</div>
	        	</g:if>
	        </g:if>
	        <g:else>
	        	  <div class="alert alert-info alert-dismissable centered">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    <h4 style="margin:0"><i class="icon fa fa-info"></i> No song found for this artist.</h4>
                  </div>
	        </g:else>