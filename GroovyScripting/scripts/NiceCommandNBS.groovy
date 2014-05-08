#!/usr/bin/env CLASSPATH=out/production/GroovyScripting groovy --basescript org.ifcx.scripting.JCommanderScript

import com.beust.jcommander.Parameter
import groovy.transform.Field

@Parameter
@Field List<String> parameters

@Parameter(names = ["-log", "-verbose" ], description = "Level of verbosity")
@Field Integer verbose = 1;

@Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
@Field String groups;

@Parameter(names = "-debug", description = "Debug mode")
@Field boolean debug = false;

@Parameter(names = ["-cp", "--codepath"])
@Field List<String> codepath = []

@Parameter(names = "-conf", converter = com.beust.jcommander.converters.FileConverter)
@Field File configuration

println parameters

println verbose

println groups

println debug.class

println codepath

println configuration?.length()

println zoos
