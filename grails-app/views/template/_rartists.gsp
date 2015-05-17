<div class="">
	<g:if test="${recommendedArtists.size() > 0}">
	      <h4><strong>Artists</strong></h4>
	   	  <table class="table">
		     	<g:each in="${recommendedArtists}" var="rartist">
		     		<tr>
		     			<td><a entity-id="${rartist.documentID}" class="load-document-id" href="javascript:void(0)">${rartist.artistName}</a></td>
		     		</tr>
		     	</g:each>
	      </table>
      </g:if>
</div>