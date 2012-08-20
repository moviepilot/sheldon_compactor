Compactor
---------

     # once
     mvn package

     # run with enough RAM by setting $MAVEN_OPTS or 
     # use default settings from the script if you have (32+db size)G machine
     ./sheldon_compactor --mode GLOPS --config propsFile --source oldStoreDir --target newStoreDir

     # 
     # for mp.com users only; this serves as an example to other uses
     #
     # --node-handler .SheldonNodePreprocessor --edge-handler .SheldonEdgePreprocessor
     # --node-indexer .SheldonNodeIndexer --edge-indexer .SheldonEdgeIndexer


What it does
------------

Copy neo4j database in oldStoreDir configured with propsFile to newStoreDir applying handlers and indexers as requested.

As a result, your database gets way smaller.


How it does that
----------------

Compactor uses the high performance concurrency framework disruptor. If you
have to touch the code, make sure you understand disruptor before doing so.

Basically compactor runs in two phases, one for nodes, and one for edges.

Each phase consists of a sequence of stages that is passed by each 
item (node/edge):

* read item
* write item to new db
* figure out what to index
* write index

Each stage is executed in parallel to the maximum degree possible, i.e.
while the index is being written to other stages may already build up
more data for the index.


How to influence what gets done how
-----------------------------------

* Edit `src/main/java/com/moviepilot/sheldon/compactor/config/Defaults.java` to change global default parameters
* Edit `src/main/java/com/moviepilot/sheldon/compactor/main/Main.java` to change startup, arguments and db setup
  (look here for hidden options, too)
* Edit `src/main/java/com/moviepilot/sheldon/compactor/Compactor.java` to change the general workflow of node and edge
  processing
* Edit the pre-processors and indexers as required



Versions
--------

* Consider using the stable branch on github if you hit troubles
* The pom version is not very telling; Usually it is kept in sync with internal versioning of mp.com; ignore for now


Dependencies
------------

* neo4j
* GNU Trove collection classes for speedy maps
* disruptor
* java-sizeof
* snakeyaml


Caveats
-------

Almost no tests. Written in an emergency. Turned out to work quite nicely.

Released so you may use it in case you need something like this.


Tuning
------

* Use --ring-size-info to see stats about the size of the allocated ring
** --ring-size
** --index-flush-min-count
** --index-flush-max-count
** --num-props
** --num-index-entries
** --num-index-props
** --num-index-writers
** --index-batch-size


mp.com users
------------

* --num-index-writers 2