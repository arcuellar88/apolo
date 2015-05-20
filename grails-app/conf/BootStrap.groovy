import java.io.File;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import apolo.querybuilder.Searcher
import apolo.queryrefinement.Autocomplete
import apolo.queryrefinement.NER
import apolo.queryrefinement.SpellingCorrection
import apolo.recommender.Recommender
import apolo.msc.Global_Configuration

class BootStrap {

    def init = { servletContext ->
		servletContext.putAt("autocomplete", new Autocomplete())
		servletContext.putAt("ner", new NER())
		servletContext.putAt("spellchecker", new SpellingCorrection())
		servletContext.putAt("recommender", new Recommender())
		servletContext.putAt("indexSearcher", new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Global_Configuration.INDEX_DIRECTORY)))))
    }
    def destroy = {
    }
}
