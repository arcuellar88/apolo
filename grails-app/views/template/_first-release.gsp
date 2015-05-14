<div class="box box-success">
	<div class="box-header with-border">
		<h3 class="box-title"><strong>${release.releaseName}</strong></h3>
       	<div class="box-tools pull-right">
           	<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
        </div><!-- /.box-tools -->
	</div><!-- /.box-header -->
    <div class="box-body">
    	<g:if test="${release.irelease != null}">
    		 <div class="row">
	      		<div class="col-lg-12">
	      			<p>
	    				${release.irelease.description}
	    			</p>
	      		</div>
      		 </div>
    	</g:if>
       
    	<div class="row">
	       	<div class="col-lg-6">
	       		<table class="table">
	       			<tr>
	       				<td><b>Name:</b></td>
	       				<td>${release.releaseName}</td>
	       			</tr>
	       		
	       			<tr>
	       				<td><b>Type:</b></td>
	       				<td>${release.releaseType}</td>
	       			</tr>
	       			
	       			<tr>
	       				<td><b>Artist:</b></td>
	       				<td>${release.releaseArtist}</td>
	       			</tr>
	       			
	       			<tr>
	       				<td><b>#Song:</b></td>
	       				<td>
	       					<g:if test="${release.releaseSongs.equals("")}">
	       						N/A
	       					</g:if>
	       					<g:else>
	       						${releaseSongs.size()}
	       					</g:else>
	       				</td>
	       			</tr>
	       		</table>
	       	</div>
       	
	       	<div class="col-lg-6" style="max-height: 170px; overflow: auto;">
	       		<g:if test="${releaseSongs.size() > 0}">
		     		<table class="table">
		       			<tr>
		       				<th>#</th>
		       				<th>Song</th>
		       			</tr>
		       			
		       			<g:each in="${releaseSongs}" var="releaseSong" status="releaseSongCounter">
		       				<tr>
		       					<td>${releaseSongCounter + 1}</td>
		       					<td>
		       						<a href="javascript:void(0)" class="load-document-id" entity-id="${releaseSongIDs.get(releaseSongCounter)}">${releaseSong}</a>
		       					</td>
		       				</tr>
		       			</g:each>
		       		</table>
		       	</g:if>
		       	<g:else>
		       		 <div class="alert alert-info alert-dismissable centered">
                    	<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                    	<h4 style="margin:0"><i class="icon fa fa-info"></i>No song found for this release.</h4>
                  	</div>
		       	</g:else>
	        </div>
       </div>
       	</div><!-- /.box-body -->
   </div>