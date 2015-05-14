<div class="callout result-item result-artist">
	<h4><a href="javascript:void(0)">${artist.artistName}</a></h4>
	<div>Country: ${artist.artistCountry}</div>
	<div>Type: ${artist.artistType}</div>
	<div>Gender: ${artist.artistGender}</div>
	<p>
		<g:if test="${artist.iartist != null}">
			${artist.iartist.description}
		</g:if>
	</p>
	
</div>