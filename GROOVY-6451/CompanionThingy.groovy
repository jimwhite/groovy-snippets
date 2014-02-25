class CompanionThingy {
   @groovy.transform.SourceURI def myURI
   String toString() { "I'm a CompanionThingy and I'm from " + myURI }
}
