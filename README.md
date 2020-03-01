# cliche

**cliche** is just a demo of how to use the [medium-editor](https://yabwe.github.io/medium-editor/) in [ClojureScript](https://clojurescript.org) and [Reagent](https://github.com/reagent-project/reagent). 

## Overview

**cliche** was inspired by the [Clich](https://github.com/clartaq/clich) rich text editor, which itself was inspired by [Pell](https://github.com/jaredreich/pell).

**cliche** doesn't do anything useful itself, but can serve as a starting point for further work or incorporating into other projects where you need rich text editing.

## Development

To get an interactive ([figwheel](https://figwheel.org)) development environment with a REPL run:

    clj -A:fig:repl

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To run a figwheel environment without the REPL, use:

    clj -A:fig:dev

To remove all compilation artifacts (clean) run:

    clj -A:clean

To create a production build run:

	clj -A:clean
	clj -A:fig:min

You can check for out-of-date project dependencies with:

    clj -A:ancient

## License

Copyright © 2020, David D. Clark

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
