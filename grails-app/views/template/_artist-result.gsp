<div class="callout result-item result-artist">
	<h4>
		<g:if test="${artist.artistName.equalsIgnoreCase('others')}">
			${artist.artistName}
		</g:if>
		<g:if test="${!artist.artistName.equalsIgnoreCase('others')}">
			<a entity-id="${artist.documentID}" class="load-document-id" href="javascript:void(0)">${artist.artistName}</a>
		</g:if>
	</h4>
	<div>Country: ${artist.artistCountry}</div>
	<div>Type: ${artist.artistType}</div>
	<div>Gender: ${artist.artistGender}</div>
	<p>
		<g:if test="${artist.iartist != null}">
			${artist.iartist.description}
		</g:if>
	</p>
	
</div>