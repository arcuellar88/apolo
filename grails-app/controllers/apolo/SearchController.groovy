package apolo

class SearchController extends BaseController {

    def index() { 
		render (view : "search" , layout : "main");
	}
}
