import apolo.queryrefinement.Autocomplete
import apolo.queryrefinement.NER
import apolo.queryrefinement.SpellingCorrection

class BootStrap {

    def init = { servletContext ->
		servletContext.putAt("autocomplete", new Autocomplete())
		servletContext.putAt("ner", new NER())
		servletContext.putAt("spellchecker", new SpellingCorrection());
    }
    def destroy = {
    }
}
