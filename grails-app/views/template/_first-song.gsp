<%@page import="apolo.msc.Global_Configuration"%>
<%@page import="apolo.entity.ApoloDocument"%>
<%@page import="java.text.DecimalFormat"%>
    	<div class="box box-success">
        	<div class="box-header with-border">
             	<h3 class="box-title"><strong>${song.songTitle}</strong></h3>
             	<div class="box-tools pull-right">
               		<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
            	</div><!-- /.box-tools -->
         	</div><!-- /.box-header -->
         
         	<div class="box-body">
         	
         		<div class="row">
    				<div class="col-lg-6">
    					<table class="table">
       			<tr>
       				<% 
						ArrayList<String> songArtists = song.getSplittedFields(song.songArtists);
						ArrayList<String> songArtistIDs = song.getSplittedFields(song.songArtistsID);
       				%>
       				<td>Artist:</td>
       				<td>
       					<g:each in="${songArtists}" status="i" var="artist">
      							<g:if test="${i > 0}">
      								<br>
      							</g:if>
      							<g:if test="${artist.equalsIgnoreCase('others')}">
      								${artist}
      							</g:if>
      							<g:if test="${!artist.equalsIgnoreCase('others')}">
      								<a entity-id="artist_${songArtistIDs.get(i)}" class="load-document-id" href="javascript:void(0)">${artist}</a>
      							</g:if>
       					</g:each>
       				</td>
    						</tr>
    		
       			<tr>
       				<td>Rating:</td>
       				<td>
       					<g:if test="${song.songRating != Integer.MAX_VALUE}">
       						<% 
							   DecimalFormat df = new DecimalFormat();
							   df.setMaximumFractionDigits(0);
	       					%>
       						${df.format(song.getSongRating())}
       					</g:if>
       				</td>
       			</tr>
       			
       			<tr>
       				<td>Duration:</td>
       				<td>
       					<g:if test="${song.songDuration != Integer.MAX_VALUE}">
       						${song.songDuration}
       					</g:if>
       				</td>
       			</tr>
    			
       			<tr>
       				<td>Genre: </td>
       				<td>
       					<% ArrayList<String> songGenres = song.getSplittedFields(song.songGenres); %>
       					<g:each in="${songGenres}" var="genre" status="i">
       						<g:if test="${i > 0}"><br></g:if>
       						${genre}
       					</g:each>
       				</td>
       			</tr>
       			
       			<tr>
       				<td>Date: </td>
       				<td>
       					<g:if test="${song.songMonth != Integer.MAX_VALUE}">
       						${song.songMonth}/${song.songYear}
       					</g:if>
       				</td>
       			</tr>
    			
       			<tr>
       				<td>Label: </td>
       				<td>
       					${song.songLabel}
       				</td>
       			</tr>
    					</table>
    				</div>
    	
       		<div class="col-lg-6">
       			<g:if test="${song.songYoutubeURL.equals("")}">
       				<div class="alert alert-info alert-dismissable centered">
                    	<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    	<h4 style="margin:0"><i class="icon fa fa-info"></i> We could find any related video.</h4>
                  	</div>
       			</g:if>
       			<g:else>
       				<h5 class="centered text-success" style="margin-top: 0">You might want to watch</h5>
	       			<div class="youtube-wrapper">
       					<div class="alert alert-info alert-dismissable centered">
                    		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    		<h4 style="margin:0"><i class="icon fa fa-info"></i> We could not find any video related to this song.</h4>
                  		</div>
       					<iframe src="${song.songYoutubeURL}" frameborder="0" allowfullscreen></iframe>
	            	</div>
            	</g:else>
           </div>
    			</div>
            	
           	<div class="row">
           		
           		<div class="col-lg-6">
           			<!--  
           				<img style="max-width: 100%; border: 4px solid #DDD;" src="http://placehold.it/600x340&text=Song play count graph"/>
           				<div id="song-playcount-chart"></div>
           			-->
           			
           			<div>
              		<label class="text-aqua">Additional information</label>
              		<p>
              			<g:if test="${song.isong != null}">
              				${song.isong.description}
              			</g:if>
              		</p>
              	</div>
           			
           		</div>
            
              <div class="col-lg-6">
              	<table class="table">
              		<tr>
              			<th colspan="4" class="text-aqua">Features</th>
              		</tr>
              		
              		<tr>
              			<td><b>Tempo</b></td>
              			<td>
              				<g:if test="${song.songTempo != Integer.MAX_VALUE}">
       							${song.songTempo}
       						</g:if>
              			</td>
              		</tr>
              		
              		<tr>
              			<td><b>Loudness</b></td>
              			<td>
              				<g:if test="${song.songLoudness != Integer.MAX_VALUE}">
       					${song.songLoudness}
       				</g:if>
              			</td>
              		</tr>
              		
              		<tr>
              			<td><b>Energy</b></td>
              			<td>
              				<g:if test="${song.songEnergy != Integer.MAX_VALUE}">
       					${song.songEnergy}
       				</g:if>
              			</td>
              		</tr>
              		
              		<tr>
              			<td><b>Hotness</b></td>
              			<td>
              				<g:if test="${song.songHotness != Integer.MAX_VALUE}">
       					${song.songHotness}
       				</g:if>
              			</td>
              		</tr>
              		
              		<tr>
              			<td><b>Dancebility</b></td>
              			<td>
              				<g:if test="${song.songDancebility != Integer.MAX_VALUE}">
       					${song.songDancebility}
       				</g:if>
              			</td>
              		</tr>
              	</table>
             	
              
           	 	</div>
    		</div>
		</div><!-- /.box-body -->
</div>
