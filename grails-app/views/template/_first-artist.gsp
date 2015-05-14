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
       							${artist.artistRating}
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
	     		<img style="max-height: 200px; border: 4px solid #DDD;" src="http://placehold.it/150x200&text=Artist Image from DBPedia if available"/>
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
      						drawRatingPieChart(${artist.artistNumberOfPositiveRating}, ${artist.artistNumberOfNeutralRating}, ${artist.artistNumberOfNegativeRating}, "rating-chart-${artist.documentId}")
          				});
      				</script>
      				<div id="rating-chart-${artist.documentId}"></div>
      			</g:if>
      			<g:else>
      				<img style="width: 100%;" src="http://placehold.it/600x300&text=Artist rating is not available"/>
      			</g:else>
      		</div>
       
        <div class="col-lg-6">
        	<g:if test="${firstArtistSongs.size() > 0}">
	        	<table class="table">
	        		<tr>
	        			<th>Songs of ${artist.artistName}</th>
	        		</tr>
	        		
	        		<g:each in="${firstArtistSongs} var="artistSong" status="songArtistCounter">
	        			<g:if test="${artistSong != null}">
		        			<tr>
		        				<td>
		        					<a href="javascript:void(0)" class="load-document-id" entity-id="${artistSong.songID}">${artistSong.songTitle}</a>
		        				</td>
		        			</tr>
	        			</g:if>
	        		</g:each>
	        	</table>
	        </g:if>
	        <g:else>
	        	  <div class="alert alert-info alert-dismissable centered">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    <h4 style="margin:0"><i class="icon fa fa-info"></i> No song found for this artist.</h4>
                  </div>
	        </g:else>
      	 </div>
       </div>
       	</div><!-- /.box-body -->
   </div>