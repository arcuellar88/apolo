<div class="callout result-item result-release">
	<h4><a entity-id="${release.documentID}" class="load-document-id" href="javascript:void(0)">${release.releaseName}</a></h4>
	<div>Type: ${release.releaseType}</div>
	<div>Artist: ${release.releaseArtist}</div>
	<div>
		#Song:
		<g:if test="${release.releaseSongs.equals("")}">
       		N/A
   		</g:if>
   		<g:else>
   			${releaseSongs.size()}
   		</g:else> 
	</div>
	<p>
		<g:if test="${release.irelease != null}">
			${release.irelease.description}
		</g:if>
	</p>
	
</div>