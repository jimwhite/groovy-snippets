#!/usr/bin/env groovy

@GrabResolver(name='script', root='repo')
@Grab(group='org.ifcx.lucene', module='lucene-core', version='4.5.1')
@Grab(group='org.ifcx.lucene', module='lucene-analyzers-common', version='4.5.1')

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version

args = args as List

if (!args) {
    System.err.println "Output lines from the input file(s) that are new since the last time this script was run."
    System.err.println "A Lucene index is maintained in the directory specified by the first argument."
    System.err.println "usage: whats_new.groovy <index_dir> [input_file]*"
    System.exit(-1)
}

indexDirectory = FSDirectory.open(new File(args.remove(0)))

indexWriter = new IndexWriter(indexDirectory
        , new IndexWriterConfig(Version.LUCENE_CURRENT, new StandardAnalyzer(Version.LUCENE_CURRENT)))

indexReader = DirectoryReader.open(indexWriter, false)

indexSearcher = new IndexSearcher(indexReader)

total_lines = 0
new_lines = 0

args.each {
    new File(it).eachLine { line ->
        def query = new TermQuery(new Term("contents", line))
        if (indexSearcher.search(query, 1).totalHits == 0) {
            def doc = new Document()
            doc.add(new Field("contents", line, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS))
            indexWriter.addDocument(doc)

            new_lines += 1
            println line
        }
        total_lines += 1
    }
}

indexWriter.close()

System.err.println "There are $new_lines new lines of $total_lines total."
