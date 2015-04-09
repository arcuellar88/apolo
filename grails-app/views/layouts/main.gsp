<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Apolo Music Search Engine</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <!-- Bootstrap 3.3.2 -->
    <link href="${resource(dir: 'css', file: 'bootstrap.min.css')}" rel="stylesheet" type="text/css" />    
    
    <!-- FontAwesome 4.3.0 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    
    <!-- Ionicons 2.0.0 -->
    <link href="http://code.ionicframework.com/ionicons/2.0.0/css/ionicons.min.css" rel="stylesheet" type="text/css" />
        
    <!-- Theme style -->
    <link href="${resource(dir: 'css', file: 'AdminLTE.min.css')}" rel="stylesheet" type="text/css" />
    
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
         folder instead of downloading all of them to reduce the load. -->
    <link href="${resource(dir: 'css', file: 'skins/_all-skins.min.css')}" rel="stylesheet" type="text/css" />
    
    <!-- custom style -->
    <link href="${resource(dir: 'css', file: 'styles.css')}" rel="stylesheet" type="text/css" />
    
    <!-- jQuery 2.1.3 -->
    <script src="${resource(dir: 'js', file: 'plugins/jQuery/jQuery-2.1.3.min.js')}" type="text/javascript"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
  <body class="skin-blue sidebar-collapse">
    <div class="wrapper">
      
      <header class="main-header">
        <!-- Logo -->
        <a href="" class="logo"><b>Apolo Music</b></a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
          </a>
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <!-- User Account: style can be found in dropdown.less -->
              <li class="dropdown user user-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <img src="https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpa1/v/t1.0-1/p160x160/10491073_10153153099494560_2553714796370408923_n.jpg?oh=fb6a70d39147c4dc12071fe20f615702&oe=5579EDA8&__gda__=1438375993_50b2defe0b014dbd0bf3786ecb042694" class="user-image" alt="User Image"/>
                  <span class="hidden-xs">Shakira</span>
                </a>
                <ul class="dropdown-menu">
                  <!-- User image -->
                  <li class="user-header">
                    <img src="https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpa1/v/t1.0-1/p160x160/10491073_10153153099494560_2553714796370408923_n.jpg?oh=fb6a70d39147c4dc12071fe20f615702&oe=5579EDA8&__gda__=1438375993_50b2defe0b014dbd0bf3786ecb042694" class="img-circle" alt="User Image" />
                    <p>
                      Shakira
                      <small>Member since March. 2015</small>
                    </p>
                  </li>
                  <!-- Menu Body -->
                  <!-- Menu Footer-->
                  <li class="user-footer">
                    <div class="pull-right">
                      <a href="#" class="btn btn-default btn-flat">Sign out</a>
                    </div>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </nav>
      </header>
      
      <!-- Left side column. contains the logo and sidebar -->
      <aside class="main-sidebar">
			<g:render template="/template/team" />          
      </aside>
     
      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
        </section>

        <!-- Main content -->
        <section class="content">
			<g:layoutBody/>
        </section><!-- /.content -->
      </div><!-- /.content-wrapper -->
    </div><!-- ./wrapper -->
    
    <!-- jQuery UI 1.11.2 -->
    <script src="http://code.jquery.com/ui/1.11.2/jquery-ui.min.js" type="text/javascript"></script>
    <!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
    
    <script>
      $.widget.bridge('uibutton', $.ui.button);
    </script>
    <!-- Bootstrap 3.3.2 JS -->
    <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}" type="text/javascript"></script>    
    <!--script src="http://cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js"></script-->
    
    <!-- Bootstrap WYSIHTML5 -->
    <!-- script src="${resource(dir: 'js', file: 'plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js')}" type="text/javascript"></script-->
    
    <!-- Slimscroll -->
    <script src="${resource(dir: 'js', file: 'plugins/slimScroll/jquery.slimscroll.min.js')}" type="text/javascript"></script>
    
    <!-- Typeahead -->
    <script src="${resource(dir: 'js', file: 'plugins/bootstrap-typeahead/bootstrap3-typeahead.js')}" type="text/javascript"></script>
    
    <!-- AdminLTE App -->
    <script src="${resource(dir: 'js', file: 'app.min.js')}" type="text/javascript"></script>
    
    <!-- AdminLTE for demo purposes -->
    <script src="${resource(dir: 'js', file: 'demo.js')}" type="text/javascript"></script>
  </body>
</html>