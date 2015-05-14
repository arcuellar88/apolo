											<div class="callout result-item result-song">
			                  					<% 
													ArrayList<String> songArtists = song.getSplittedFields(song.songArtists);
													ArrayList<String> songArtistIDs = song.getSplittedFields(song.songArtistsID);
       											%>
						                    	<h4><a entity-id="${song.songID}" class="load-document-id" href="javascript:void(0)">${song.songTitle}</a></h4>
						                    	<div>
						                    		Artist:
						                    		<g:each in="${songArtists}" var="artist" status="artistCounter">
						                    			<g:if test="${i>0}">,&nbsp;</g:if>
						                    			<a entity-id="${songArtistIDs.get(artistCounter)}" class="load-document-id" href="javascript:void(0)">${artist}</a>
						                    		</g:each> 
						                    	</div>
						                    	<div>Duration:
						                    		<g:if test="${song.songDuration != Integer.MAX_VALUE}">
       													${song.songDuration}
       												</g:if> 
						                    	</div>
						                    	<div>Released: 
						                    		<g:if test="${song.songMonth != Integer.MAX_VALUE}">
       													${song.songMonth}/${song.songYear}
       												</g:if>
						                    	</div>
						                    	<div>Country:
						                    		<% ArrayList<String> songCountries = song.getSplittedFields(song.songCountries); %>
							       					<g:each in="${songCountries}" var="country" status="i">
							       						<g:if test="${i > 0}"><br></g:if>
							       						${country}
							       					</g:each>
						                    	</div>
						                  	</div>