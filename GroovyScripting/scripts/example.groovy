#!/usr/bin/env groovy --basescript groovy.cli.JCommanderScript
import com.beust.jcommander.Parameter
import groovy.transform.Field

@Parameter(description="These are all the unflagged argument values")
@Field List<String> parameters

@Parameter(names = ["-cp", "--codepath"], description="Zero or more paths to code")
@Field List<String> codepath = []

println parameters
println codepath

assert parameters == ['placeholder', 'another']
assert codepath == ['/usr/x.jar', '/bin/y.jar']