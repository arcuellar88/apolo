<%@page import="java.text.DecimalFormat"%>
<div class="box box-success">
	<div class="box-header with-border">
		<h3 class="box-title"><strong>${artist.artistName}</strong></h3>
       	<div class="box-tools pull-right">
           	<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
        </div><!-- /.box-tools -->
	</div><!-- /.box-header -->
    <div class="box-body">
    	<div class="row">
	       	<div class="col-lg-6">
	       		<table class="table">
	       			
	       			<tr>
	       				<td><b>Name:</b></td>
	       				<td>${artist.artistName}</td>
	       			</tr>
	       		
	       			<tr>
	       				<td><b>Gender:</b></td>
	       				<td>${artist.artistGender}</td>
	       			</tr>
	       			
	       			<tr>
	       				<td><b>Country:</b></td>
	       				<td>${artist.artistCountry}</td>
	       			</tr>
	       			
	       			<tr>
	       				<td><b>Rating:</b></td>
	       				<td>
	       					<g:if test="${artist.artistRating != Integer.MAX_VALUE}">
	       						<% 
								   DecimalFormat df = new DecimalFormat();
								   df.setMaximumFractionDigits(0);
	       						%>
       							${df.format(artist.getArtistRating())}
       						</g:if>
	       				</td>
	       			</tr>
	       			
	       			<tr>
	       				<td><b>#Rating:</b></td>
	       				<td>
	       					<g:if test="${artist.artistNumberOfRating != Integer.MAX_VALUE}">
       							${artist.artistNumberOfRating}
       						</g:if>
	       				</td>
	       			</tr>
	       		</table>
	       	</div>
       	
	       	<div class="col-lg-6" style="text-align: center;">
	       		<g:if test="${artist.iartist != null && artist.iartist.thumbnail != null}">
	       			<img style="max-height: 200px; border: 4px solid #DDD;" src="${artist.iartist.thumbnail}"/>
	       		</g:if>
	       		<g:else>
	       			<img style="max-height: 200px; border: 4px solid #DDD;" src="http://placehold.it/150x200&text=Artist Image from DBPedia if available"/>
	       		</g:else>
	        </div>
       </div>
       
       <div class="clearfix"></div>
       
       <div class="row">
      		<div class="col-lg-12">
      			<p>
     				<g:if test="${artist.iartist != null}">
     					${artist.iartist.description}
     				</g:if>
      			</p>
      		</div>
       </div>
      	<div class="row">
      		<div class="col-lg-6">
      			<g:if test="${artist.artistNumberOfRating != Integer.MAX_VALUE}">
      				<script type="text/javascript">
      					$(document).ready(function(){
      						drawRatingPieChart(${artist.artistNumberOfPositiveRating}, ${artist.artistNumberOfNeutralRating}, ${artist.artistNumberOfNegativeRating}, "rating-chart-${artist.documentID}")
          				});
      				</script>
      				<div id="rating-chart-${artist.documentID}"></div>
      			</g:if>
      			<g:else>
      				<img style="width: 100%;" src="http://placehold.it/600x300&text=Artist rating is not available"/>
      			</g:else>
      		</div>
       
         <div  class="col-lg-6 all-artist-song" style="max-height: 200px; overflow: auto;">
        		<g:render template="/template/first-artist-songs" model="[artist: artist, firstArtistSongs: firstArtistSongs]"/>
      	 </div>
       </div>
       	</div><!-- /.box-body -->
   </div>