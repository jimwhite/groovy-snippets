import CompanionThingy

def x = new CompanionThingy()

@groovy.transform.SourceURI def myURI

println "This script is from $myURI"

println x
